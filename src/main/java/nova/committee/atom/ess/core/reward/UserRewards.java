package nova.committee.atom.ess.core.reward;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.util.RewardUtil;
import org.apache.logging.log4j.Logger;

import java.util.*;
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

    public CompoundTag getRewardsForCurrentMonthSyncData(UUID uuid) {
        List<ItemStack> rewardItems = getRewardsForCurrentMonth(uuid);
        CompoundTag syncData = new CompoundTag();
        ListTag itemListTag = new ListTag();
        for (ItemStack itemStack : rewardItems) {
            CompoundTag itemStackTag = new CompoundTag();
            itemStack.save(itemStackTag);
            itemListTag.add(itemStackTag);
        }
        syncData.put("ItemList", itemListTag);
        return syncData;
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


    public void fromJson(JsonObject jsonObject) {

        var rewardKey = jsonObject.get("YearMonthUser").getAsString();
        var rewardItems = RewardUtil.arrayToStacks(jsonObject.getAsJsonArray("RewardItems"));
        var rewardedDays = jsonObject.get("RewardedDays").getAsInt();
        var lastRewardedDay = jsonObject.get("LastRewardedDay").getAsString();

        rewardItemsMap.put(rewardKey, rewardItems);

        // Restoring last rewarded day and totally rewarded days for the month.
        rewardedDaysMap.put(rewardKey, rewardedDays);
        lastRewardedDayMap.put(rewardKey, lastRewardedDay);
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        Set<String> rewardKeys = new HashSet<>();
        rewardKeys.addAll(rewardItemsMap.keySet());
        rewardKeys.addAll(rewardedDaysMap.keySet());
        rewardKeys.addAll(lastRewardedDayMap.keySet());

        if (rewardKeys.isEmpty()) {
            log.warn("unable to save reward user data, because data are empty!");
            return null;
        }

        for (String rewardKey : rewardKeys) {
            jsonObject.addProperty("YearMonthUser", rewardKey);

            List<ItemStack> rewardItems = rewardItemsMap.get(rewardKey);
            jsonObject.add("RewardItems", RewardUtil.stacksToArray(rewardItems));
            jsonObject.addProperty("RewardedDays", rewardedDaysMap.getOrDefault(rewardKey, 0));
            jsonObject.addProperty("LastRewardedDay", lastRewardedDayMap.getOrDefault(rewardKey, ""));

        }
        return jsonObject;
    }


}
