package nova.committee.atom.ess.init.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.atom.ess.core.lock.ILockHolder;
import nova.committee.atom.ess.util.Location;
import nova.committee.atom.ess.util.LockUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/20 21:38
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LockHandler {


    @SubscribeEvent
    public static void onBlockOpen(PlayerInteractEvent.RightClickBlock event) {
        Location location = new Location(event.getPlayer().getLevel(), event.getPos());

        if (!LockUtil.canEditSecuredBlock(location, event.getPlayer())) {
            LockUtil.printErrorMessage(location, event.getPlayer());
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {

        BlockEntity blockEntity = event.getWorld().getBlockEntity(event.getPos());

        if (event.getEntity() instanceof Player player && blockEntity instanceof ILockHolder securityHolder) {
            securityHolder.getLockProfile().setOwner(player);
            blockEntity.setChanged();
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {

        Location location = new Location(event.getPlayer().getLevel(), event.getPos());

        if (!event.getPlayer().isCreative()) {

            if (!LockUtil.canEditSecuredBlock(location, event.getPlayer())) {

                event.setCanceled(true);
                LockUtil.printErrorMessage(location, event.getPlayer());
            }

        }
    }


    @SubscribeEvent
    public static void onBlockExploded(ExplosionEvent event) {

        List<BlockPos> affectedBlocks = event.getExplosion().getToBlow();
        List<BlockPos> securedBlocksFound = new ArrayList<>();

        for (BlockPos pos : affectedBlocks) {

            BlockEntity blockEntity = event.getWorld().getBlockEntity(pos);

            if (blockEntity instanceof ILockHolder) {
                securedBlocksFound.add(pos);
            }
        }

        affectedBlocks.removeAll(securedBlocksFound);
    }
}
