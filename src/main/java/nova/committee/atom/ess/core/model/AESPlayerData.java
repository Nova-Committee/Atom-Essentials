package nova.committee.atom.ess.core.model;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import nova.committee.atom.ess.common.cmd.teleport.BackCmd;
import nova.committee.atom.ess.common.menu.TrashcanMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 10:30
 * Version: 1.0
 */
public class AESPlayerData {
    private Player player;
    private UUID uuid;
    private String playerName;
    private final Map<String, TeleportPos> homes = new HashMap<>(5);

    private final TeleportPos[] teleportHistory = new TeleportPos[BackCmd.maxBacks];

    private int currentBackIndex = 0;

    private boolean isFlyable;
    private long canFlyUntil = -1;

    private TrashcanMenu.TrashcanData trashcan;

    private long lastSpawnTime = 0;
    private long lastHomeTime = 0;
    private long lastHomeOtherTime = 0;
    private long lastBackTime = 0;
    private long lastRTPTime = 0;
    private long lastWarpTime = 0;
    private long lastTPATime = 0;
    private long lastKitTime = 0;

    public AESPlayerData(@NotNull UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }


    public @NotNull CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        // Info
        nbt.putString("uuid", this.uuid.toString());
        nbt.putString("name", this.playerName);

        // Fly
        nbt.putBoolean("flyable", this.isFlyable);
        nbt.putLong("canFlyUntil", this.canFlyUntil);

        // Homes
        ListTag nbtHomes = new ListTag();
        for (Map.Entry<String, TeleportPos> home : this.homes.entrySet()) {
            CompoundTag nbtHome = new CompoundTag();
            nbtHome.putString("name", home.getKey());
            nbtHome.put("pos", home.getValue().serializeNBT());
            nbtHomes.add(nbtHome);
        }
        nbt.put("homes", nbtHomes);

        // Backs
        nbt.putInt("currentBackIndex", this.currentBackIndex);
        ListTag nbtBacks = new ListTag();
        for (TeleportPos backPos : this.teleportHistory) {
            if (backPos == null) break;
            nbtBacks.add(backPos.serializeNBT());
        }
        nbt.put("backHistory", nbtBacks);

        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        try {
            this.uuid = UUID.fromString(nbt.getString("uuid"));
        } catch (IllegalArgumentException ignore) {}
        this.playerName = nbt.getString("name");

        this.isFlyable = nbt.getBoolean("flyable");
        this.canFlyUntil = nbt.getLong("canFlyUntil");

        Optional.ofNullable((ListTag) nbt.get("homes")).ifPresent((nbtHomes) -> {
            for (Tag home : nbtHomes) {
                CompoundTag temp = (CompoundTag) home;
                TeleportPos pos = new TeleportPos();
                pos.deserializeNBT(temp.getCompound("pos"));
                this.homes.put(temp.getString("name"), pos);
            }
        });

        this.currentBackIndex = nbt.getInt("currentBackIndex");
        Optional.ofNullable((ListTag) nbt.get("backHistory")).ifPresent(backs -> {
            int i = 0;
            for (Tag back : backs) {
                CompoundTag temp = (CompoundTag) back;
                TeleportPos pos = new TeleportPos();
                pos.deserializeNBT(temp);
                try {
                    this.teleportHistory[i] = pos;
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
                i++;
            }
        });
    }


    public @Nullable TrashcanMenu.TrashcanData getTrashcan() {
        return trashcan;
    }

    public void setTrashcan(TrashcanMenu.TrashcanData trashcan) {
        this.trashcan = trashcan;
    }

    public boolean isFlyable() {
        return this.isFlyable;
    }

    public void setFlyable(boolean flyable) {
        if (this.player.isCreative()) {
            this.isFlyable = true;
            return;
        }
        if (this.player != null) {
            if (flyable) {
                this.player.getAbilities().mayfly = true;
            } else {
                this.player.getAbilities().mayfly = false;
                this.player.getAbilities().flying = false;
                this.canFlyUntil = -1;
            }
            this.player.onUpdateAbilities();
            this.isFlyable = flyable;
        }
    }

    public long getCanFlyUntil() {
        return canFlyUntil;
    }

    public void setCanFlyUntil(long canFlyUntil) {
        this.canFlyUntil = canFlyUntil;
    }

    public void addTeleportHistory(TeleportPos teleportPos) {
        System.arraycopy(this.teleportHistory, 0, this.teleportHistory, 1, BackCmd.maxBacks - 1);
        this.teleportHistory[0] = teleportPos;
        this.currentBackIndex = 0;
    }

    public @Nullable TeleportPos getTeleportHistory() {
        if (this.currentBackIndex < BackCmd.maxBacks) {
            return this.teleportHistory[this.currentBackIndex];
        } else {
            return null;
        }
    }

    public void moveCurrentBackIndex() {
        this.currentBackIndex++;
    }

    public @Nullable TeleportPos getHomePos(String homeName) {
        return this.homes.get(homeName);
    }

    public void delHome(String name) {
        this.homes.remove(name);
    }

    public void setHome(String name, TeleportPos newPos) {
        this.homes.put(name, newPos);
    }

    public Map<String, TeleportPos> getHomes() {
        return this.homes;
    }

    public @Nullable Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getName() {
        if (this.playerName == null) {
            this.playerName =  this.player.getGameProfile().getName();
        }
        return this.playerName;
    }

    public long getLastSpawnTime() {
        return this.lastSpawnTime;
    }

    public void setLastSpawnTime(long lastSpawnTime) {
        this.lastSpawnTime = lastSpawnTime;
    }

    public long getLastHomeTime() {
        return lastHomeTime;
    }

    public void setLastHomeTime(long lastHomeTime) {
        this.lastHomeTime = lastHomeTime;
    }

    public long getLastHomeOtherTime() {
        return lastHomeOtherTime;
    }

    public void setLastHomeOtherTime(long lastHomeOtherTime) {
        this.lastHomeOtherTime = lastHomeOtherTime;
    }

    public long getLastBackTime() {
        return lastBackTime;
    }

    public void setLastBackTime(long lastBackTime) {
        this.lastBackTime = lastBackTime;
    }

    public long getLastRTPTime() {
        return lastRTPTime;
    }

    public void setLastRTPTime(long lastRTPTime) {
        this.lastRTPTime = lastRTPTime;
    }

    public long getLastWarpTime() {
        return lastWarpTime;
    }

    public void setLastWarpTime(long lastWarpTime) {
        this.lastWarpTime = lastWarpTime;
    }

    public long getLastTPATime() {
        return lastTPATime;
    }

    public void setLastTPATime(long lastTPATime) {
        this.lastTPATime = lastTPATime;
    }

    public long getLastKitTime() {
        return lastKitTime;
    }

    public void setLastKitTime(long lastKitTime) {
        this.lastKitTime = lastKitTime;
    }

    public UUID getUuid() {
        return this.uuid;
    }


    @Override
    public String toString() {
        return this.uuid.toString();
    }


}
