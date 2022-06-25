package nova.committee.atom.ess.core.reward;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.util.RewardUtil;
import org.apache.logging.log4j.Logger;

import javax.json.Json;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static nova.committee.atom.ess.util.RewardUtil.arrayToStacks;
import static nova.committee.atom.ess.util.RewardUtil.stacksToArray;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/21 21:41
 * Version: 1.0
 */
public class ConfigRewards {
    protected static final Random random = new Random();

    protected static final Logger log = Static.LOGGER;

    protected static ConcurrentHashMap<String, List<ItemStack>> defaultRewardConfig =
            new ConcurrentHashMap<>();

    protected static ConcurrentHashMap<String, List<ItemStack>> rewardItemsMap =
            new ConcurrentHashMap<>();//reward for each month


    public static ConcurrentHashMap<String, List<ItemStack>> getDefaultRewardConfig() {
        return defaultRewardConfig;
    }

    public static ConcurrentHashMap<String, List<ItemStack>> getRewardItemsMap() {
        return rewardItemsMap;
    }

    public static String getKeyId(int year, int month) {
        return year + "-" + month;
    }

    public static List<ItemStack> getRewardItemForMonth(int month) {
        return switch (month) {
            case 1 -> rewardItemsMap.get("1");
            case 2 -> rewardItemsMap.get("2");
            case 3 -> rewardItemsMap.get("3");
            case 4 -> rewardItemsMap.get("4");
            case 5 -> rewardItemsMap.get("5");
            case 6 -> rewardItemsMap.get("6");
            case 7 -> rewardItemsMap.get("7");
            case 8 -> rewardItemsMap.get("8");
            case 9 -> rewardItemsMap.get("9");
            case 10 -> rewardItemsMap.get("10");
            case 11 -> rewardItemsMap.get("11");
            case 12 -> rewardItemsMap.get("12");
            default -> new ArrayList<>();
        };
    }


    public static int getCurrentDay() {
        return LocalDate.now().getDayOfMonth();
    }

    public static int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    public static int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    public static String getCurrentYearMonthDay() {
        return getCurrentYear() + "-" + getCurrentMonth() + "-" + getCurrentDay();
    }

    public static int getDaysCurrentMonth() {
        YearMonth yearMonth = YearMonth.of(getCurrentYear(), getCurrentMonth());
        return yearMonth.lengthOfMonth();
    }


    public static List<ItemStack> calculateRewardItemsForMonth(int month) {
        log.info("Calculate Reward items for month {} ...", month);
        YearMonth yearMonth = YearMonth.of(getCurrentYear(), month);
        int numberOfDays = yearMonth.lengthOfMonth();
        List<ItemStack> rewardItemsForMonth = getRewardItemForMonth(month);

        // Early return if we have matching items without shuffle.
        if (rewardItemsForMonth.size() >= numberOfDays) {
            return rewardItemsForMonth.stream().limit(numberOfDays).collect(Collectors.toList());
        }

        // Fill missing days with fill items.
        int numRewardItems = rewardItemsForMonth.size();
        int numMissingRewardItems = numberOfDays - numRewardItems;
        List<ItemStack> normalFillItems = getNormalFillItems();
        List<ItemStack> rareFillItems = getRareFillItems();
        Set<ItemStack> rareDuplicates = new HashSet<>();

        for (int i = 0; i < numMissingRewardItems; i++) {
            ItemStack fillItem = null;

            // There is a 1:7 change to get an rare item instead of an normal item.
            if (random.nextInt(7) == 0) {
                ItemStack rareFillItem = rareFillItems.get(random.nextInt(rareFillItems.size()));
                // Make sure we avoid duplicates of rare fill items.
                if (!rareDuplicates.contains(rareFillItem)) {
                    fillItem = rareFillItem;
                    rareDuplicates.add(rareFillItem);
                }
            }

            // Make sure we have filled something.
            if (fillItem == null) {
                fillItem = normalFillItems.get(random.nextInt(normalFillItems.size()));
            }

            rewardItemsForMonth.add(fillItem);
        }

        // Shuffle items before returning
        Collections.shuffle(rewardItemsForMonth);
        return rewardItemsForMonth;
    }

    public static List<ItemStack> getNormalFillItems() {
        return defaultRewardConfig.get("normalFillItems");
    }

    public static List<ItemStack> getRareFillItems() {
        return defaultRewardConfig.get("rareFillItems");
    }

    public static ItemStack getNormalFillItem() {
        List<ItemStack> normalFillItems = getNormalFillItems();
        return normalFillItems.get(random.nextInt(normalFillItems.size()));
    }

    public List<ItemStack> getRewardsFor(int year, int month) {
        String key = getKeyId(year, month);
        return rewardItemsMap.computeIfAbsent(key, id -> ConfigRewards.calculateRewardItemsForMonth(month));
    }

    public static ItemStack getRareFillItem() {
        List<ItemStack> rareFillItems = getRareFillItems();
        return rareFillItems.get(random.nextInt(rareFillItems.size()));
    }

    public List<ItemStack> getRewardsForCurrentMonth() {
        return getRewardsFor(getCurrentYear(), getCurrentMonth());
    }

    public CompoundTag getRewardsForCurrentMonthSyncData() {
        List<ItemStack> rewardItems = getRewardsForCurrentMonth();
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

    public ItemStack getRewardForCurrentMonth(int day) {
        List<ItemStack> rewards = getRewardsForCurrentMonth();
        int rewardIndex = --day;
        if (rewardIndex >= 0 && rewards.size() > rewardIndex) {
            return rewards.get(rewardIndex).copy();
        }
        return ItemStack.EMPTY;
    }


    public JsonObject toDefaultJson() {
        JsonObject jsonObject = new JsonObject();
        JsonObject items = new JsonObject();


        List<ItemStack> normal = new ArrayList<>();
        normal.add(Items.DIAMOND.getDefaultInstance());

        List<ItemStack> rare = new ArrayList<>();
        rare.add(Items.EMERALD.getDefaultInstance());

        jsonObject.add("normalFillItems", stacksToArray(normal));

        jsonObject.add("rareFillItems", stacksToArray(rare));

        for (int i = 1; i < 13; i++) {
            JsonArray array = new JsonArray();

            switch (i) {
                case 1, 5, 7, 8, 10, 12, 3 -> {
                    for (int j = 1; j < 32; j++)
                        addDefaultItems(array);
                }
                case 2 -> {
                    if (getCurrentYear() % 4 == 0 && getCurrentYear() % 100 != 0)
                        for (int j = 1; j < 30; j++)
                            addDefaultItems(array);
                    else
                        for (int j = 1; j < 29; j++)
                            addDefaultItems(array);

                }
                case 4, 6, 9, 11 -> {
                    for (int j = 1; j < 31; j++)
                        addDefaultItems(array);
                }
            }

            items.add(String.valueOf(i), array);
        }

        jsonObject.add("itemList", items);
        return jsonObject;
    }

    public void addDefaultItems(JsonArray array) {
        array.add(Objects.requireNonNull(Items.APPLE.getRegistryName()).toString());
    }

    public void fromJson(JsonObject jsonObject) {

        JsonArray normalFillItems = jsonObject.getAsJsonArray("normalFillItems");
        defaultRewardConfig.putIfAbsent("normalFillItems", arrayToStacks(normalFillItems));

        JsonArray rareFillItems = jsonObject.getAsJsonArray("rareFillItems");
        defaultRewardConfig.putIfAbsent("rareFillItems", arrayToStacks(rareFillItems));

        JsonObject items = jsonObject.getAsJsonObject("itemList");
        if (items != null)
            for (int i = 1; i < 13; i++) {
                JsonArray moths = items.getAsJsonArray(String.valueOf(i));
                rewardItemsMap.putIfAbsent(String.valueOf(i), arrayToStacks(moths));
            }
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        JsonObject items = new JsonObject();

        jsonObject.add("normalFillItems", stacksToArray(defaultRewardConfig.get("normalFillItems")));

        jsonObject.add("rareFillItems", stacksToArray(defaultRewardConfig.get("rareFillItems")));

        for (int i = 1; i < 13; i++) {
            items.add(String.valueOf(i), stacksToArray(rewardItemsMap.get(String.valueOf(i))));
        }
        jsonObject.add("itemList", items);
        return jsonObject;
    }


}
