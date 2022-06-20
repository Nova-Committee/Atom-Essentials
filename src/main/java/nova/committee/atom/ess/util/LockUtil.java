package nova.committee.atom.ess.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import nova.committee.atom.ess.core.lock.ILockHolder;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/20 21:34
 * Version: 1.0
 */
public class LockUtil {
    public static final UnitMessenger MESSENGER = new UnitMessenger("lock");

    public static boolean canEditSecuredBlock(Location location, Player player) {

        BlockEntity blockEntity = location.getBlockEntity();

        if (blockEntity instanceof ILockHolder securityHolder) {
            return securityHolder.getLockProfile().isOwner(player) || player.isCreative();
        }

        return true;
    }

    public static void printErrorMessage(Location location, Player player) {

        BlockEntity blockEntity = location.getBlockEntity();

        if (blockEntity instanceof ILockHolder securityHolder) {
            MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.notyours").append(" ").append(securityHolder.getLockProfile().getOwnerName()), player);
        }
    }
}
