package nova.committee.atom.ess.api.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 21:12
 * Version: 1.0
 */
public abstract class AbstractModConfig {

    protected ForgeConfigSpec.Builder builder;

    public AbstractModConfig(ForgeConfigSpec.Builder builder) {
        this.builder = builder;
    }

    /**
     * 初始化配置文件
     */
    public abstract void init();

    /**
     * 通过 @ConfigField 获取配置之中的变量
     */
    public abstract void get();
}
