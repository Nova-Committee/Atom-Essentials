package nova.committee.atom.ess;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nova.committee.atom.ess.init.handler.ConfigHandler;

@Mod(Static.MOD_ID)
public class AtomEss {


    public AtomEss() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigHandler.SERVER_CONFIG);

        Static.isCurios = ModList.get().isLoaded("curios");
        Static.isLuckPerms = ModList.get().isLoaded("luckperms");

    }

    private void setup(final FMLCommonSetupEvent event) {

    }



}
