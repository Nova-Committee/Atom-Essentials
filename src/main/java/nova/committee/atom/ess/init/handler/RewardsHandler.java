package nova.committee.atom.ess.init.handler;

import cn.evolvefield.mods.atom.lib.utils.FileUtil;
import cn.evolvefield.mods.atom.lib.utils.json.JsonUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.api.common.config.ConfigField;
import nova.committee.atom.ess.core.model.AESPlayerData;
import nova.committee.atom.ess.core.reward.ConfigRewards;
import nova.committee.atom.ess.core.reward.UserRewards;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/21 18:23
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RewardsHandler {

    private static final Logger log = Static.LOGGER;
    private static final short REWARD_CHECK_TICK = 20 * 60; // every 1 Minute
    private static final MutableComponent claimCommand = new TextComponent("/sign")
            .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withClickEvent(
                    new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sign")));
    @ConfigField
    public static int rewardTimePerDay = 30;
    public static UserRewards userRewards = new UserRewards();
    public static ConfigRewards configRewards = new ConfigRewards();
    private static int rewardTimePerDayTicks = 30 * 60 * 20;
    private static short ticker = 0;
    private static Set<ServerPlayer> playerList = ConcurrentHashMap.newKeySet();

    @SubscribeEvent
    public static void onServerStaring(ServerStartingEvent event) {
        File config = new File(FileHandler.REWARD_FOLDER, "config.json");
        if (!config.exists()) {
            FileUtil.createSubFile("config.json", FileHandler.REWARD_FOLDER);
            JsonUtil.write(config, configRewards.toDefaultJson());
        }
        configRewards.fromJson(JsonUtil.get(config));

    }

    @SubscribeEvent
    public static void onServerAboutToStartEvent(ServerAboutToStartEvent event) {
        playerList = ConcurrentHashMap.newKeySet();
        rewardTimePerDayTicks = rewardTimePerDay * 60 * 20;

        log.info("{} Daily rewards will be granted after {} min ({} ticks) a player is online.",
                Static.REWARD_LOG, rewardTimePerDay, rewardTimePerDayTicks);
    }


    @SubscribeEvent
    public static void onPlayerSaved(PlayerEvent.SaveToFile event) {
        File dataFile = new File(FileHandler.REWARD_PLAYER_FOLDER.getAbsolutePath() + "/" + event.getPlayer().getUUID() + ".json");
        JsonUtil.write(dataFile, userRewards.toJson());
        log.debug("{}Successfully save player " + event.getPlayer().getUUID() + "reward data to file!", Static.REWARD_LOG);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerLoaded(PlayerEvent.LoadFromFile event) {
        File dataFile = new File(FileHandler.REWARD_PLAYER_FOLDER.getAbsolutePath() + "/" + event.getPlayer().getUUID() + ".json");
        if (dataFile.exists())
            userRewards.fromJson(JsonUtil.read(dataFile, true).getAsJsonObject());

    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        String username = event.getPlayer().getName().getString();
        if (username.isEmpty()) {
            return;
        }
        ServerPlayer player =
                ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(username);
        log.debug("{} Player {} {} logged out.", Static.REWARD_LOG, username, player);
        playerList.remove(player);
    }


    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        String username = event.getPlayer().getName().getString();
        if (username.isEmpty()) {
            return;
        }
        ServerPlayer player =
                ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(username);
        log.debug("{} Player {} {} logged in.", Static.REWARD_LOG, username, player);

        // Sync data and add Player to reward.
        NetworkHandler.syncGeneralRewardForCurrentMonth(player);
        NetworkHandler.syncUserRewardForCurrentMonth(player);
        playerList.add(player);
    }

    @SubscribeEvent
    public static void handleServerTickEvent(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || ticker++ < REWARD_CHECK_TICK
                || playerList.isEmpty()) {
            return;
        }
        for (ServerPlayer player : playerList) {
            if (player.tickCount > rewardTimePerDayTicks) {
                UUID uuid = player.getUUID();
                if (!userRewards.hasRewardedToday(uuid)) {
                    // Update stored data
                    userRewards.setLastRewardedDayForCurrentMonth(uuid);
                    int rewardedDays = userRewards.increaseRewardedDaysForCurrentMonth(uuid);

                    // Add reward for rewarded Days.
                    ItemStack itemStack = configRewards.getRewardForCurrentMonth(rewardedDays);
                    if (itemStack.isEmpty()) {
                        log.error("Reward {} for day {} for current month was empty!", itemStack, rewardedDays);
                    } else {
                        userRewards.addRewardForCurrentMonth(rewardedDays, uuid, itemStack);
                        player.sendMessage(new TranslatableComponent(Static.REWARD_PREFIX + "rewarded_item",
                                player.getName(), itemStack, rewardedDays), UUID.randomUUID());
                        player.sendMessage(
                                new TranslatableComponent(Static.REWARD_PREFIX + "claim_rewards", claimCommand),
                                UUID.randomUUID());
                        NetworkHandler.syncUserRewardForCurrentMonth(player);
                    }

                    log.info("Reward player {} daily reward for {} days with {} ...", player, rewardedDays,
                            itemStack);
                }
            }
        }
        ticker = 0;
    }

}
