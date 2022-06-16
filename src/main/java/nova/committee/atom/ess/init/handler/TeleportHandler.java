package nova.committee.atom.ess.init.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.core.model.TeleportPos;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 11:11
 * Version: 1.0
 */
@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TeleportHandler {

    public static final Map<String, TeleportPos> WARPS = new HashMap<>();


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void load(ServerAboutToStartEvent event) {
        if (FileHandler.WARPS_FILE.exists()) {
            try {
                CompoundTag temp = NbtIo.readCompressed(FileHandler.WARPS_FILE);
                Optional.ofNullable((ListTag) temp.get("warps")).ifPresent(warps -> {
                    for (Tag e : warps) {
                        CompoundTag warp = (CompoundTag) e;
                        TeleportPos pos = new TeleportPos();
                        pos.deserializeNBT((CompoundTag) Objects.requireNonNull(warp.get("pos")));
                        WARPS.put(warp.getString("name"), pos);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void save(WorldEvent.Save event) {
        try {
            CompoundTag temp = new CompoundTag();
            ListTag warps = new ListTag();
            for (Map.Entry<String, TeleportPos> warp : WARPS.entrySet()) {
                CompoundTag warpNbt = new CompoundTag();
                warpNbt.putString("name", warp.getKey());
                warpNbt.put("pos", warp.getValue().serializeNBT());
                warps.add(warpNbt);
            }
            temp.put("warps", warps);
            NbtIo.writeCompressed(temp, FileHandler.WARPS_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
