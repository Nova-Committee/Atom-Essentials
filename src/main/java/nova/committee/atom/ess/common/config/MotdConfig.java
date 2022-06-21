package nova.committee.atom.ess.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.api.common.config.AbstractModConfig;
import nova.committee.atom.ess.init.handler.MotdHandler;

import java.util.Arrays;
import java.util.List;

public class MotdConfig extends AbstractModConfig {

    private ForgeConfigSpec.ConfigValue<List<List<? extends String>>> raws;
    private ForgeConfigSpec.BooleanValue isCustomizedMOTDEnable;

    public MotdConfig(ForgeConfigSpec.Builder builder) {
        super(builder);
    }

    private static boolean isValidMOTD(Object o) {
        if (o instanceof List<?> list) {
            if (list.size() > 0) {
                return list.get(0) instanceof List;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init() {
        this.builder.push("MOTD");
        this.isCustomizedMOTDEnable = this.builder
                .comment("Set it to true to enable customized server motd (server description)",
                        "Default value: false")
                .define("IsCustomizedMOTDEnable", false);
        this.raws = this.builder
                .comment("The description lines of motd, with max two lines.",
                        "Every [\"\", \"\"] inside the outermost is a motd that players will see.",
                        "You could add many [\"\", \"\"] to dynamically change the motd.",
                        "You could also use '&' or '§' to specify the format of each line of description.",
                        "For more information about formatting, please check google or minecraft wiki.",
                        "Default value: [[\"&a&lFirst line &fof &b&lMOTD&f!\", \"&kSecond!\"], [\"Thanks for using &d&lAtom Essentials&f!\", \"&6&lWuuuhoooooo\"]]")
                .define("Descriptions", Arrays.asList(Arrays.asList("&a&lFirst line &fof &b&lMOTD&f!", "&kSecond!"),
                                Arrays.asList("Thanks for using &d&lAtom Essentials&f!", "&6&lWuuuhoooooo")),
                        MotdConfig::isValidMOTD);
        this.builder.pop();
    }

    @Override
    public void get() {
        MotdHandler.raws = this.raws.get();
        MotdHandler.isCustomizedMOTDEnable = this.isCustomizedMOTDEnable.get();
        if (MotdHandler.isCustomizedMOTDEnable) {
            MotdHandler.init();
            Static.LOGGER.info("MOTD Reloaded!");
        }
    }

}
