package nova.committee.atom.ess.util;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;
import net.minecraftforge.registries.ForgeRegistries;
import nova.committee.atom.ess.core.lock.ILockHolder;
import nova.committee.atom.ess.init.handler.BanItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/14 21:50
 * Version: 1.0
 */
public class BanUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger();

    public static final UnitMessenger MESSENGER = new UnitMessenger("ban.item");

    public static void printErrorMessage(ItemStack itemStack, Player player) {
        MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.banned").append(" ").append(itemStack.getDisplayName().getString()), player);
    }


    public static File initialize(Path folder, String fileName) {
        File file = new File(folder.toFile(), fileName);
        try {
            if (file.createNewFile()) {
                Path defaultConfigPath = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).resolve("itemblacklist.json");
                if (Files.exists(defaultConfigPath)) {
                    Files.copy(defaultConfigPath, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    FileWriter configWriter = new FileWriter(file);
                    configWriter.write(gson.toJson(new JsonArray()));
                    configWriter.close();
                }
            }
        } catch(IOException e) {
            LOGGER.warn(e.getMessage());
        }
        return file;
    }

    /**
     * Reads items from a Json that has a top level array
     */
    public static List<Item> readItemsFromJson(File jsonFile) {
        try {
            Reader reader = new FileReader(jsonFile);
            JsonArray array = GsonHelper.fromJson(gson, reader, JsonArray.class);
            List<Item> returnedArrays = new ArrayList<>();
            assert array != null;
            for(JsonElement element: array) {
                Item item = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(element.getAsString()))).asItem();
                if(item != null && !(item instanceof AirItem)) {
                    returnedArrays.add(item);
                }
            }
            return returnedArrays;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Writes a new item to a json that has a top level array
     */
    public static void appendItemToJson(File jsonFile, Item item) {
        try (Reader reader = new FileReader(jsonFile)) {
            JsonArray array = GsonHelper.fromJson(gson, reader, JsonArray.class);
            assert array != null;

            JsonPrimitive string = new JsonPrimitive(Objects.requireNonNull(item.getRegistryName()).toString());
            if(!array.contains(string))
                array.add(string);

            try (FileWriter fileWriter = new FileWriter(jsonFile)) {
                fileWriter.write(gson.toJson(array));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        BanItemHandler.BANNED_ITEMS = BanUtil.readItemsFromJson(BanItemHandler.BANLIST);
    }

    /**
     * Removes an item from a json that has a top level array
     */
    public static void removeItemFromJson(File jsonFile, Item item) throws IndexOutOfBoundsException {
        try (Reader reader = new FileReader(jsonFile)) {
            JsonArray array = GsonHelper.fromJson(gson, reader, JsonArray.class);
            assert array != null;
            int itemLocation = -1;
            int i = 0;
            for(JsonElement element: array) {
                if (element.getAsString().equals(Objects.requireNonNull(item.getRegistryName()).toString()))
                    itemLocation = i;
                i++;
            }
            array.remove(itemLocation);
            try (FileWriter fileWriter = new FileWriter(jsonFile)) {
                fileWriter.write(gson.toJson(array));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        BanItemHandler.BANNED_ITEMS = BanUtil.readItemsFromJson(BanItemHandler.BANLIST);
    }

    public static void removeAllItemsFromJson(File jsonFile) throws IndexOutOfBoundsException {
        try (FileWriter fileWriter = new FileWriter(jsonFile)) {
            fileWriter.write(gson.toJson(new JsonArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BanItemHandler.BANNED_ITEMS = BanUtil.readItemsFromJson(BanItemHandler.BANLIST);
    }
}
