package nova.committee.atom.ess.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/21 22:07
 * Version: 1.0
 */
public class RewardUtil {
    public static boolean isStringItem(String name) {
        Pattern pattern = Pattern.compile("(\\s+):(\\s+)");
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }


    public static List<ItemStack> arrayToStacks(JsonArray array) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (JsonElement element : array) {
            String itemName = element.getAsString();
            if (RewardUtil.isStringItem(itemName))
                itemStacks.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName))));
        }
        return itemStacks;
    }

    public static JsonArray stacksToArray(List<ItemStack> stacks) {
        JsonArray array = new JsonArray();
        for (ItemStack stack : stacks) {
            array.add(Objects.requireNonNull(stack.getItem().getRegistryName()).toString());
        }
        return array;
    }
}
