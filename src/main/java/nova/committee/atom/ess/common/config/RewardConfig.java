package nova.committee.atom.ess.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import nova.committee.atom.ess.api.common.config.AbstractModConfig;
import nova.committee.atom.ess.init.handler.RewardsHandler;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/25 13:57
 * Version: 1.0
 */
public class RewardConfig extends AbstractModConfig {
    private ForgeConfigSpec.IntValue rewardTimePerDay;

    public RewardConfig(ForgeConfigSpec.Builder builder) {
        super(builder);
    }

    @Override
    public void init() {
        this.builder.push("MOTD");
        this.rewardTimePerDay = this.builder
                .comment("Time in minutes the players needs to be online on the server before receiving a reward for the day.")
                .defineInRange("rewardTimePerDay", 30, 1, 1440);
    }

    @Override
    public void get() {
        RewardsHandler.rewardTimePerDay = rewardTimePerDay.get();

    }
}
