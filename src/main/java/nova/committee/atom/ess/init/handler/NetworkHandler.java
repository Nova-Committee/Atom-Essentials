package nova.committee.atom.ess.init.handler;

import cn.evolvefield.mods.atom.lib.init.handler.NetBaseHandler;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.common.net.ClearTrashPacket;
import nova.committee.atom.ess.common.net.GeneralRewardsForCurrentMonthPacket;
import nova.committee.atom.ess.common.net.UserRewardsForCurrentMonthPacket;
import nova.committee.atom.ess.core.reward.UserRewards;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 20:41
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    protected static final Logger log = Static.LOGGER;

    public static final NetBaseHandler INSTANCE = new NetBaseHandler(new ResourceLocation(Static.MOD_ID, "main"));

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            INSTANCE.register(ClearTrashPacket.class, new ClearTrashPacket());
            INSTANCE.register(GeneralRewardsForCurrentMonthPacket.class, new GeneralRewardsForCurrentMonthPacket());
            INSTANCE.register(UserRewardsForCurrentMonthPacket.class, new UserRewardsForCurrentMonthPacket());

        });

    }


    public static void syncGeneralRewardForCurrentMonth(ServerPlayer serverPlayer) {
        CompoundTag data = RewardsHandler.configRewards.getRewardsForCurrentMonthSyncData();
        if (serverPlayer != null && serverPlayer.getUUID() != null && data != null && !data.isEmpty()) {
            log.debug("Sending general reward for current month to {}: {}", serverPlayer, data);
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new GeneralRewardsForCurrentMonthPacket(data));
        }
    }

    /**
     * Send user rewards for current month to player.
     */
    public static void syncUserRewardForCurrentMonth(ServerPlayer serverPlayer) {
        if (serverPlayer == null || serverPlayer.getUUID() == null) {
            return;
        }
        UUID uuid = serverPlayer.getUUID();
        CompoundTag data = RewardsHandler.userRewards.getRewardsForCurrentMonthSyncData(uuid);
        int rewardedDays = RewardsHandler.userRewards.getRewardedDaysForCurrentMonth(uuid);
        if (data != null && !data.isEmpty()) {
            log.debug("Sending user reward for current month to {}: {}", serverPlayer, data);
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new UserRewardsForCurrentMonthPacket(data, rewardedDays));
        }
    }


}
