package nova.committee.atom.ess.init.handler;

import cn.evolvefield.mods.atom.lib.utils.FileUtil;
import cn.evolvefield.mods.atom.lib.utils.json.JsonUtil;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.atom.ess.core.reward.ConfigRewards;

import java.io.File;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/21 18:23
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RewardsHandler {


    @SubscribeEvent
    public static void onServerStaring(ServerStartingEvent event) {
        File config = new File(FileHandler.REWARD_FOLDER, "config.json");
        ConfigRewards configRewards = new ConfigRewards();
        if (!config.exists()) {
            FileUtil.createSubFile("config.json", FileHandler.REWARD_FOLDER);
            JsonUtil.write(config, configRewards.toDefaultJson());
        }
        configRewards.fromJson(JsonUtil.get(config));

    }


}
