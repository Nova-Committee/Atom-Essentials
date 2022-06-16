package nova.committee.atom.ess.init.handler;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.common.cmd.member.TrashcanCmd;
import nova.committee.atom.ess.common.cmd.teleport.TpaCmd;
import nova.committee.atom.ess.core.model.TPARequest;
import nova.committee.atom.ess.common.menu.TrashcanMenu;
import nova.committee.atom.ess.util.text.I18Util;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/9 14:50
 * Version: 1.0
 */
@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerTickHandler {
    private static int counter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (counter >= 20) {
                counter = 0;
                new Thread(() -> {
                    long now = System.currentTimeMillis();
                    // TPA Request
                    for (TPARequest next : TpaHandler.getTpaRequest().values()) {
                        if ((next.getCreateTime() + TpaCmd.maxTPARequestTimeoutSeconds * 1000L) <= now) {
                            TpaHandler.getTpaRequest().remove(next.getId());
                            next.getSource().displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                                    I18Util.getTranslationKey("message", "requestTimeout"), next.getTarget().getGameProfile().getName()), false
                            );
                        }
                    }

                    // Player Fly Time
                    PlayerDataHandler.PLAYER_DATA_LIST.stream().filter(player -> player.getPlayer() != null &&
                                    player.isFlyable() &&
                                    player.getCanFlyUntil() != -1 &&
                                    player.getCanFlyUntil() <= now)
                            .forEach(player -> {
                                player.setFlyable(false);
                                player.setCanFlyUntil(-1);
                                player.getPlayer().displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                                        I18Util.getTranslationKey("message", "cantFlyNow")), false);

                            });

                    // Trashcan count down
                    PlayerDataHandler.PLAYER_DATA_LIST.forEach(data -> {
                        TrashcanMenu.TrashcanData trashcan = data.getTrashcan();
                        if (trashcan != null) {
                            long nextCleanTime = trashcan.getLastCleanLong() + TrashcanCmd.cleanTrashcanIntervalSeconds * 1000L;
                            if (nextCleanTime <= now) {
                                trashcan.clear();
                                trashcan.setNextCleanSeconds(TrashcanCmd.cleanTrashcanIntervalSeconds);
                            } else {
                                trashcan.setNextCleanSeconds((int) (nextCleanTime - now) / 1000);
                            }
                        }
                    });
                }).start();
            }
            counter++;
        }
    }
}
