package nova.committee.atom.ess.init.handler;

import com.google.common.collect.Sets;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.api.common.config.AbstractModConfig;
import nova.committee.atom.ess.common.config.CleanerConfig;
import nova.committee.atom.ess.common.config.CmdConfig;
import nova.committee.atom.ess.common.config.MotdConfig;

import java.util.List;
import java.util.Set;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 21:39
 * Version: 1.0
 */
@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigHandler {
    public static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SERVER_CONFIG;

    private static final Set<? extends AbstractModConfig> CONFIGS;

    static {
        CONFIGS = init();
        SERVER_CONFIG = SERVER_BUILDER.build();
        get(CONFIGS);
    }

    public static Set<? extends AbstractModConfig> init() {
        Set<? extends AbstractModConfig> configs = Sets.newHashSet(
                new CmdConfig(SERVER_BUILDER),
                new CleanerConfig(SERVER_BUILDER),
                new MotdConfig(SERVER_BUILDER)

        );
        configs.forEach(AbstractModConfig::init);
        Static.LOGGER.info("SCE Config init!");
        return configs;
    }

    public static void get(Set<? extends AbstractModConfig> configs) {
        configs.forEach(AbstractModConfig::get);
    }

    @SubscribeEvent
    public static void onLoading(ModConfigEvent.Loading event) {
        get(CONFIGS);
        Static.LOGGER.info("SCE Config loaded!");
    }

    @SubscribeEvent
    public static void onReloading(ModConfigEvent.Reloading event) {
        get(CONFIGS);
        Static.LOGGER.info("SCE Config Reloaded!");
    }

    public static boolean isResourceLocationList(Object o) {
        if (!(o instanceof List<?> list)) {
            return false;
        }
        for (Object s : list) {
            if (!s.toString().contains(":")) {
                return false;
            }
        }
        return true;
    }
}
