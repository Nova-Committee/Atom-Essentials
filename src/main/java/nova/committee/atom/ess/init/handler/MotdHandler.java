package nova.committee.atom.ess.init.handler;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.api.common.config.ConfigField;
import nova.committee.atom.ess.util.text.ColorfulString;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/17 12:51
 * Version: 1.0
 */
@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MotdHandler {
    private static final List<TextComponent> TEXTS = new ArrayList<>();
    @ConfigField
    public static List<List<? extends String>> raws;
    @ConfigField
    public static boolean isCustomizedMOTDEnable = false;
    public static int counter = 0;
    private static int index = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (isCustomizedMOTDEnable) {
            if (counter >= 20) {
                counter = 0;
                ServerStatus response = Static.SERVER.getStatus();
                try {
                    response.setDescription(TEXTS.get(index));
                } catch (IndexOutOfBoundsException ignore) {
                }
                index = index == TEXTS.size() - 1 ? 0 : index + 1;
            }
            counter++;
        }
    }

    public static void init() {
        TEXTS.clear();
        raws.forEach(raw -> TEXTS.add(new ColorfulString(raw).getText()));
        counter = 0;
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        init();
    }
}
