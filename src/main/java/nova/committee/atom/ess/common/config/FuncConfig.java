package nova.committee.atom.ess.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.api.common.config.AbstractModConfig;
import nova.committee.atom.ess.init.handler.LockHandler;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/21 18:34
 * Version: 1.0
 */
public class FuncConfig extends AbstractModConfig {

    private ForgeConfigSpec.BooleanValue isLockEnable;

    public FuncConfig(ForgeConfigSpec.Builder builder) {
        super(builder);
    }

    @Override
    public void init() {
        this.builder.push("Functions");
        this.isLockEnable = this.builder
                .comment("Set it to true to enable lock blocks function",
                        "Default value: false")
                .define("IsLockEnable", false);

        this.builder.pop();
    }

    @Override
    public void get() {
        LockHandler.isLockEnable = isLockEnable.get();
        if (LockHandler.isLockEnable) {
            Static.LOGGER.info("Lock Function On!");
        }
    }
}
