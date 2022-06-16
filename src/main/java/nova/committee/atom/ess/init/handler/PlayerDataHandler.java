package nova.committee.atom.ess.init.handler;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.atom.ess.core.model.AESPlayerData;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.core.model.TeleportPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 12:14
 * Version: 1.0
 */
@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerDataHandler {

    public static final List<AESPlayerData> PLAYER_DATA_LIST = new ArrayList<>();

    public static @Nullable Player getPlayer(String playerName) {
        for (AESPlayerData data : PLAYER_DATA_LIST) {
            if (data.getName().equals(playerName)) {
                return data.getPlayer();
            }
        }
        return null;
    }

    public static @NotNull List<String> getAllPlayerNamesFormatted() {
        List<String> result = new ArrayList<>();
        PLAYER_DATA_LIST.forEach(data -> {
            StringBuilder name = new StringBuilder(data.getName());
            for (char c : name.toString().toCharArray()) {
                if (!StringReader.isAllowedInUnquotedString(c)) {
                    name = new StringBuilder("\"" + name + "\"");
                    break;
                }
            }
            result.add(name.toString());
        });
        return result;
    }


    public static @NotNull AESPlayerData getInstance(@NotNull Player player) {
        GameProfile gameProfile = player.getGameProfile();
        AESPlayerData data = new AESPlayerData(gameProfile.getId(), gameProfile.getName());
        int i = PLAYER_DATA_LIST.indexOf(data);
        if (i != -1) {
            data = PLAYER_DATA_LIST.get(i);
        } else {
            PLAYER_DATA_LIST.add(data);
        }
        data.setPlayer(player);
        data.setFlyable(data.isFlyable());
        return data;
    }

    @SubscribeEvent
    public static void onPlayerSaved(PlayerEvent.SaveToFile event) {
        try {
            File dataFile = new File(FileHandler.PLAYER_DATA_FOLDER.getAbsolutePath() + "/" + event.getPlayerUUID() + ".dat");
            AESPlayerData data = getInstance(event.getPlayer());
            NbtIo.writeCompressed(data.serializeNBT(), dataFile);
            Static.LOGGER.debug("Successfully save player " + data.getUuid() + " to file!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (PLAYER_DATA_LIST) {
                PLAYER_DATA_LIST.remove(getInstance(event.getPlayer()));
            }
        }).start();
    }

    // Deserialize Player Data
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerLoaded(PlayerEvent.LoadFromFile event) {
        File dataFile = new File(FileHandler.PLAYER_DATA_FOLDER.getAbsolutePath() + "/" + event.getPlayerUUID() + ".dat");
        if (dataFile.exists()) {
            try {
                CompoundTag dataNbt = NbtIo.readCompressed(dataFile);
                AESPlayerData data = getInstance(event.getPlayer());
                data.deserializeNBT(dataNbt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 记录玩家死亡的地点，然后使用/back
     * @param event Player Death event
     */
    @SubscribeEvent
    public static void onPlayerDied(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide) {
            if (entity instanceof Player) {
                ServerPlayer player = (ServerPlayer) entity;
                AESPlayerData data = getInstance(player);
                data.addTeleportHistory(new TeleportPos(player));
            }
        }
    }

    /**
     * 让能够飞行的玩家登录后也能飞行
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        AESPlayerData data = getInstance(event.getPlayer());
        data.setFlyable(data.isFlyable());
    }

    /**
     * 让能够飞行的玩家重生后也能飞行
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e) {
        AESPlayerData data = getInstance(e.getPlayer());
        data.setFlyable(data.isFlyable());
    }

    /**
     * 更换游戏模式后改变飞行状态
     */
    @SubscribeEvent
    public static void onPlayerChangeGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if (event.getCurrentGameMode().isCreative() && event.getNewGameMode().isSurvival()) {
            new Thread(() -> {
                AESPlayerData data = getInstance(event.getPlayer());
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                data.setFlyable(data.isFlyable());
            }).start();
        }
    }
}
