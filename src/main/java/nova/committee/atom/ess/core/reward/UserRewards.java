package nova.committee.atom.ess.core.reward;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import nova.committee.atom.ess.Static;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/22 17:54
 * Version: 1.0
 */
public class UserRewards {

    protected static final Logger log = Static.LOGGER;


    private static ConcurrentHashMap<String, List<ItemStack>> rewardItemsMap =
            new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Integer> rewardedDaysMap = new ConcurrentHashMap<>();// months - days
    private static ConcurrentHashMap<String, String> lastRewardedDayMap = new ConcurrentHashMap<>();// months - lastReward


    public static String getKeyId(int year, int month, UUID uuid) {
        return year + "-" + month + ":" + uuid.toString();
    }


    public void addRewardFor(int year, int month, int day, UUID uuid, ItemStack itemStack) {
        List<ItemStack> rewards = getRewardsFor(year, month, uuid);
        int rewardIndex = --day;
        if (rewardIndex >= 0 && rewards.size() > rewardIndex) {
            rewards.add(rewardIndex, itemStack);
        } else {
            rewards.add(itemStack);
        }
    }

    public void addRewardForCurrentMonth(int day, UUID uuid, ItemStack itemStack) {
        addRewardFor(ConfigRewards.getCurrentYear(), ConfigRewards.getCurrentMonth(), day, uuid, itemStack);
    }

    public List<ItemStack> getRewardsFor(int year, int month, UUID uuid) {
        String key = getKeyId(year, month, uuid);
        return rewardItemsMap.computeIfAbsent(key, id -> new ArrayList<>());
    }

    public List<ItemStack> getRewardsForCurrentMonth(UUID uuid) {
        return getRewardsFor(ConfigRewards.getCurrentYear(), ConfigRewards.getCurrentMonth(), uuid);
    }


    public void setRewardsForCurrentMonth(UUID uuid, List<ItemStack> rewardItems) {
        setRewardsFor(ConfigRewards.getCurrentYear(), ConfigRewards.getCurrentMonth(), uuid, rewardItems);
    }

    public void setRewardsFor(int year, int month, UUID uuid, List<ItemStack> rewardItems) {
        log.debug("Set rewards for {}-{} and player {} to: {}", year, month, uuid, rewardItems);
        String key = getKeyId(year, month, uuid);
        rewardItemsMap.put(key, rewardItems);
    }

    public String getLastRewardedDay(int year, int month, UUID uuid) {
        String key = getKeyId(year, month, uuid);
        return lastRewardedDayMap.computeIfAbsent(key, id -> "");
    }

    public String getLastRewardedDayForCurrentMonth(UUID uuid) {
        return getLastRewardedDay(ConfigRewards.getCurrentYear(), ConfigRewards.getCurrentMonth(), uuid);
    }

    public void setLastRewardedDay(int year, int month, UUID uuid, String lastRewardedDay) {
        log.debug("Set last rewarded day for {}-{} and player {} to {}", year, month, uuid,
                lastRewardedDay);
        String key = getKeyId(year, month, uuid);
        lastRewardedDayMap.put(key, lastRewardedDay);
    }

    public void setLastRewardedDayForCurrentMonth(UUID uuid) {
        setLastRewardedDay(ConfigRewards.getCurrentYear(), ConfigRewards.getCurrentMonth(), uuid,
                ConfigRewards.getCurrentYearMonthDay());
    }

    public boolean hasRewardedToday(UUID uuid) {
        String lastRewardedDay = getLastRewardedDayForCurrentMonth(uuid);
        return ConfigRewards.getCurrentYearMonthDay().equals(lastRewardedDay);
    }

    public int getRewardedDays(int year, int month, UUID uuid) {
        String key = getKeyId(year, month, uuid);
        return rewardedDaysMap.getOrDefault(key, 0);
    }

    public int getRewardedDaysForCurrentMonth(UUID uuid) {
        return getRewardedDays(ConfigRewards.getCurrentYear(), ConfigRewards.getCurrentMonth(), uuid);
    }

    public int increaseRewardedDays(int year, int month, UUID uuid) {
        String key = getKeyId(year, month, uuid);
        int rewardedDays = rewardedDaysMap.getOrDefault(key, 0);
        rewardedDaysMap.put(key, ++rewardedDays);
        return rewardedDays;
    }

    public int increaseRewardedDaysForCurrentMonth(UUID uuid) {
        return increaseRewardedDays(ConfigRewards.getCurrentYear(), ConfigRewards.getCurrentMonth(), uuid);
    }


}
