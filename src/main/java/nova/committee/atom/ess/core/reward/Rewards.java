package nova.committee.atom.ess.core.reward;

import net.minecraft.world.item.ItemStack;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/21 18:01
 * Version: 1.0
 */
public class Rewards {
    protected static final Random random = new Random();


    public static List<ItemStack> calculateRewardItemsForMonth(int month) {
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

}
