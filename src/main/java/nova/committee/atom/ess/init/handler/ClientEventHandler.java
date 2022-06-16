package nova.committee.atom.ess.init.handler;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import nova.committee.atom.ess.client.screen.OthersInvScreen;
import nova.committee.atom.ess.client.screen.TrashcanScreen;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.init.registry.ModMenuTypes;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 19:06
 * Version: 1.0
 */

@Mod.EventBusSubscriber(modid = Static.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void onFMLClientSetupEvent(final FMLClientSetupEvent event) {
        MenuScreens.register(ModMenuTypes.trashcanContainerType, TrashcanScreen::new);
        MenuScreens.register(ModMenuTypes.othersContainerType, OthersInvScreen::new);
    }
}
