package nova.committee.atom.ess.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import nova.committee.atom.ess.core.model.TeleportPos;
import nova.committee.atom.ess.util.text.I18Util;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 16:03
 * Version: 1.0
 */
public class TeleportUtil {

    public static void teleport(@NotNull ServerPlayer player, ServerLevel world, @NotNull BlockPos targetPos) {
        // +0.5 teleport to the center of a block -> avoid suffocating
        player.teleportTo(world, targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5, player.yRotO, player.xRotO);
    }

    public static void teleport(@NotNull ServerPlayer player, TeleportPos pos) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            teleport(player, server.getLevel(pos.getDimension()), pos.getPos());
        }
    }

    /**
     *
     * @param lastTeleportTime 上一次传送的时间
     * @param cooldownSeconds 配置中的冷却时间
     * @return -1 返回冷却值，没有则为-1
     */
    public static double getCooldown(long lastTeleportTime, int cooldownSeconds) {
        long now = System.currentTimeMillis();
        long target = lastTeleportTime + cooldownSeconds * 1000L;
        if (now < target) {
            return (target - now) / 1000D;
        } else {
            return -1;
        }
    }

    public static boolean isInCooldown(ServerPlayer player, long lastTeleportTime, int cooldownSeconds) {
        if (cooldownSeconds <= 0) return false;
        double cooldown = TeleportUtil.getCooldown(lastTeleportTime, cooldownSeconds);
        if (cooldown != -1) {
            player.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "inCoolDown"), cooldown), false);
            return true;
        } else {
            return false;
        }
    }
}
