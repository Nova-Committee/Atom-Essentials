package nova.committee.atom.ess.core.reward;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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
public class Rewards {
    protected static final Random random = new Random();

    protected static final Logger log = Static.LOGGER;

    protected static ConcurrentHashMap<String, List<ItemStack>> jsonConfigMap =
            new ConcurrentHashMap<>();


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

    public static List<ItemStack> getRewardItemForMonth(int month) {
        return switch (month) {
            case 1 -> jsonConfigMap.get("1");
            case 2 -> jsonConfigMap.get("2");
            case 3 -> jsonConfigMap.get("3");
            case 4 -> jsonConfigMap.get("4");
            case 5 -> jsonConfigMap.get("5");
            case 6 -> jsonConfigMap.get("6");
            case 7 -> jsonConfigMap.get("7");
            case 8 -> jsonConfigMap.get("8");
            case 9 -> jsonConfigMap.get("9");
            case 10 -> jsonConfigMap.get("10");
            case 11 -> jsonConfigMap.get("11");
            case 12 -> jsonConfigMap.get("12");
            default -> new ArrayList<>();
        };
    }

    public static List<ItemStack> getNormalFillItems() {
        return jsonConfigMap.get("normalFillItems");
    }

    public static ItemStack getNormalFillItem() {
        List<ItemStack> normalFillItems = getNormalFillItems();
        return normalFillItems.get(random.nextInt(normalFillItems.size()));
    }

    public static List<ItemStack> getRareFillItems() {
        return jsonConfigMap.get("rareFillItems");
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

    public void fromJson(JsonObject jsonObject) {

        JsonArray normalFillItems = jsonObject.getAsJsonArray("normalFillItems");
        jsonConfigMap.putIfAbsent("normalFillItems", arrayToStacks(normalFillItems));

        JsonArray rareFillItems = jsonObject.getAsJsonArray("rareFillItems");
        jsonConfigMap.putIfAbsent("rareFillItems", arrayToStacks(rareFillItems));

        for (int i = 1; i < 13; i++) {
            JsonArray moths = jsonObject.getAsJsonArray(String.valueOf(i));
            jsonConfigMap.putIfAbsent(String.valueOf(i), arrayToStacks(moths));

        }
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("normalFillItems", stacksToArray(jsonConfigMap.get("normalFillItems")));

        jsonObject.add("rareFillItems", stacksToArray(jsonConfigMap.get("rareFillItems")));

        for (int i = 1; i < 13; i++) {
            jsonObject.add(String.valueOf(i), stacksToArray(jsonConfigMap.get(String.valueOf(i))));
        }
        return jsonObject;
    }
}
