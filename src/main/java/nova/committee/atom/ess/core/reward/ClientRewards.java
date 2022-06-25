package nova.committee.atom.ess.core.reward;

import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import nova.committee.atom.ess.Static;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/25 12:42
 * Version: 1.0
 */
public class ClientRewards {

    protected static final Logger log = Static.LOGGER;

    private static List<ItemStack> generalRewardItems = new ArrayList<>();
    private static List<ItemStack> userRewardItems = new ArrayList<>();
    private static int userRewardedDays = 0;

    protected ClientRewards() {

    }

    public static int getRewardedDaysForCurrentMonth() {
        return userRewardedDays;
    }

    public static void setRewardedDaysForCurrentMonth(int rewardedDays) {
        userRewardedDays = rewardedDays;
    }

    public static List<ItemStack> getGeneralRewardsForCurrentMonth() {
        return generalRewardItems;
    }

    public static void setGeneralRewardsForCurrentMonth(List<ItemStack> generalRewards) {
        generalRewardItems = generalRewards;
    }

    public static void setGeneralRewardsForCurrentMonth(String data) {
        CompoundTag compoundTag;
        try {
            compoundTag = TagParser.parseTag(data);
        } catch (CommandSyntaxException commandSyntaxException) {
            throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException.getMessage());
        }
        if (compoundTag != null) {
            setGeneralRewardsForCurrentMonth(compoundTag);
        }
    }

    public static void setGeneralRewardsForCurrentMonth(CompoundTag compoundTag) {
        if (compoundTag.contains("ItemList")) {
            ListTag itemListTag = compoundTag.getList("ItemList", 10);
            generalRewardItems = new ArrayList<>();
            for (int i = 0; i < itemListTag.size(); ++i) {
                generalRewardItems.add(ItemStack.of(itemListTag.getCompound(i)));
            }
        } else {
            log.error("{} Unable to load general rewards for current month data from {}!", Static.REWARD_LOG, compoundTag);
        }
    }

    public static List<ItemStack> getUserRewardsForCurrentMonth() {
        return userRewardItems;
    }

    public static void setUserRewardsForCurrentMonth(List<ItemStack> userRewards) {
        userRewardItems = userRewards;
    }

    public static void setUserRewardsForCurrentMonth(String data) {
        CompoundTag compoundTag;
        try {
            compoundTag = TagParser.parseTag(data);
        } catch (CommandSyntaxException commandSyntaxException) {
            throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException.getMessage());
        }
        if (compoundTag != null) {
            setUserRewardsForCurrentMonth(compoundTag);
        }
    }

    public static void setUserRewardsForCurrentMonth(CompoundTag compoundTag) {
        if (compoundTag.contains("ItemList")) {
            ListTag itemListTag = compoundTag.getList("ItemList", 10);
            userRewardItems = new ArrayList<>();
            for (int i = 0; i < itemListTag.size(); ++i) {
                userRewardItems.add(ItemStack.of(itemListTag.getCompound(i)));
            }
        } else {
            log.error("{} Unable to load user rewards for current month data from {}!", Static.REWARD_LOG, compoundTag);
        }
    }
}
