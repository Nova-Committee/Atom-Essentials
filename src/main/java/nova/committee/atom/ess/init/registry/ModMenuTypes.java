package nova.committee.atom.ess.init.registry;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.common.menu.OthersInvMenu;
import nova.committee.atom.ess.common.menu.RewardMenu;
import nova.committee.atom.ess.common.menu.TrashcanMenu;
import nova.committee.atom.ess.util.RegistryUtil;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 17:32
 * Version: 1.0
 */
@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModMenuTypes {

    public static MenuType<TrashcanMenu> trashcanContainerType;
    public static MenuType<OthersInvMenu> othersContainerType;
    public static MenuType<RewardMenu> rewardMenuType;

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        final IForgeRegistry<MenuType<?>> registry = event.getRegistry();

        registry.registerAll(
                trashcanContainerType = RegistryUtil.registerContainer("trashcan", TrashcanMenu::getClientSideInstance),
                othersContainerType = RegistryUtil.registerContainer("others_inv", OthersInvMenu::getClientSideInstance),
                rewardMenuType = RegistryUtil.registerContainer("reward", RewardMenu::getClientSideInstance)


        );
    }

}
