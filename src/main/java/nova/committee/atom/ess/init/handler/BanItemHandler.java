package nova.committee.atom.ess.init.handler;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.atom.ess.init.event.BanItemEvent;
import nova.committee.atom.ess.util.BanUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/14 21:49
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BanItemHandler {
    public static File BANLIST;
    public static List<Item> BANNED_ITEMS = new ArrayList<>();


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        Path modFolder = event.getServer().getWorldPath(new LevelResource("serverconfig"));
        BANLIST = BanUtil.initialize(modFolder, "serverconfig", "itemblacklist.json");
        BANNED_ITEMS = BanUtil.readItemsFromJson(BANLIST);
    }


    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if(event.getEntity() instanceof ItemEntity) {
            if(shouldDelete(((ItemEntity) event.getEntity()).getItem())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onItemPickup(PlayerEvent.ItemPickupEvent event) {
        if(shouldDelete(event.getStack())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerContainerOpen(PlayerContainerEvent event) {
        for(int i = 0; i < event.getContainer().slots.size(); ++i) {
            if(shouldDelete(event.getContainer().getItems().get(i))) {
                event.getContainer().getItems().set(i, ItemStack.EMPTY);
            }
        }
    }

    public static boolean shouldDelete(ItemStack stack) {
        BanItemEvent event = new BanItemEvent(stack);
        MinecraftForge.EVENT_BUS.post(event);
        if(event.getResult() == Event.Result.DEFAULT) return BANNED_ITEMS.contains(stack.getItem());
        else return event.getResult() == Event.Result.DENY;
    }

    public static String itemListToString(List<Item> itemList) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for(Item item: itemList) {
            builder.append(item.getRegistryName().toString()).append(", ");
        }
        if(itemList.size() > 0) builder.delete(builder.length() - 2, builder.length());
        builder.append(']');
        return builder.toString();
    }
}
