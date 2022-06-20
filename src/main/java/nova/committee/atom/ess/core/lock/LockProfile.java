package nova.committee.atom.ess.core.lock;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/20 21:32
 * Version: 1.0
 */
public class LockProfile {
    private String ownerName = "";

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwner(Player player) {
        ownerName = player.getName().getString();
    }

    public boolean hasOwner() {
        return !ownerName.isEmpty();
    }

    public boolean isOwner(Player player) {
        return !hasOwner() || ownerName.equalsIgnoreCase(player.getName().getString());
    }

    public void loadFromNBT(CompoundTag tag) {
        CompoundTag securityTag = tag.getCompound("Lock");
        ownerName = securityTag.getString("OwnerName");
    }

    public void saveToNBT(CompoundTag tag) {
        CompoundTag securityTag = new CompoundTag();
        securityTag.putString("OwnerName", getOwnerName());
        tag.put("Lock", securityTag);
    }

}
