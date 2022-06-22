package nova.committee.atom.ess.core.reward;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.util.RewardUtil;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    private static List<ItemStack> arrayToStacks(JsonArray array) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (JsonElement element : array) {
            String itemName = element.getAsString();
            if (RewardUtil.isStringItem(itemName))
                itemStacks.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName))));
        }
        return itemStacks;
    }

    private static JsonArray stacksToArray(List<ItemStack> stacks) {
        JsonArray array = new JsonArray();
        for (ItemStack stack : stacks) {
            array.add(Objects.requireNonNull(stack.getItem().getRegistryName()).toString());
        }
        return array;
    }

    public JsonObject toDefaultJson() {
        JsonObject jsonObject = new JsonObject();

        List<ItemStack> normal = new ArrayList<>();
        normal.add(Items.DIAMOND.getDefaultInstance());

        List<ItemStack> rare = new ArrayList<>();
        normal.add(Items.EMERALD.getDefaultInstance());

        jsonObject.add("normalFillItems", stacksToArray(normal));

        jsonObject.add("rareFillItems", stacksToArray(rare));

        for (int i = 1; i < 13; i++) {
            jsonObject.add(String.valueOf(i), new JsonArray());//todo: default monthly rewards
        }
        return jsonObject;
    }

    public void fromJson(JsonObject jsonObject) {

        JsonArray normalFillItems = jsonObject.getAsJsonArray("normalFillItems");
        defaultRewardConfig.putIfAbsent("normalFillItems", arrayToStacks(normalFillItems));

        JsonArray rareFillItems = jsonObject.getAsJsonArray("rareFillItems");
        defaultRewardConfig.putIfAbsent("rareFillItems", arrayToStacks(rareFillItems));

        for (int i = 1; i < 13; i++) {
            JsonArray moths = jsonObject.getAsJsonArray(String.valueOf(i));
            rewardItemsMap.putIfAbsent(String.valueOf(i), arrayToStacks(moths));

        }
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("normalFillItems", stacksToArray(defaultRewardConfig.get("normalFillItems")));

        jsonObject.add("rareFillItems", stacksToArray(defaultRewardConfig.get("rareFillItems")));

        for (int i = 1; i < 13; i++) {
            jsonObject.add(String.valueOf(i), stacksToArray(rewardItemsMap.get(String.valueOf(i))));
        }
        return jsonObject;
    }


}
