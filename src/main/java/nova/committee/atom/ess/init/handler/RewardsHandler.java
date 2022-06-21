package nova.committee.atom.ess.init.handler;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.atom.ess.core.reward.Rewards;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/21 18:23
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RewardsHandler {
    private static ConcurrentHashMap<String, List<ItemStack>> rewardItemsMap =
            new ConcurrentHashMap<>();

    public static String getKeyId(int year, int month) {
        return year + "-" + month;
    }

    @SubscribeEvent
    public static void onServerStaring(ServerStartingEvent event) {

    }

    public List<ItemStack> getRewardsFor(int year, int month) {
        String key = getKeyId(year, month);
        return rewardItemsMap.computeIfAbsent(key, id -> Rewards.calculateRewardItemsForMonth(month));
    }
}
