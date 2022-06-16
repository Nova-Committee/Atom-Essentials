package nova.committee.atom.ess.common.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/9 15:46
 * Version: 1.0
 */
public class OthersInvContainer implements Container {
    Inventory playerInventory;

    public OthersInvContainer(Inventory player) {
        this.playerInventory = player;
    }

    private int getPlayerInvIndex(int index) {
        return index - 36;
    }

    @Override
    public int getContainerSize() {
        return 41;
    }

    @Override
    public boolean isEmpty() {
        return this.playerInventory.isEmpty();
    }


    @Override
    public @NotNull ItemStack getItem(int index) {
        int realIndex = this.getPlayerInvIndex(index);
        return playerInventory.getItem(realIndex);
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        int realIndex = this.getPlayerInvIndex(index);
        ItemStack itemStack = this.playerInventory.removeItem(realIndex, count);
        this.setChanged();
        return itemStack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        int realIndex = this.getPlayerInvIndex(index);
        ItemStack itemStack = this.playerInventory.removeItemNoUpdate(realIndex);
        this.setChanged();
        return itemStack;
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        int realIndex = this.getPlayerInvIndex(index);
        this.playerInventory.setItem(realIndex, stack);
        this.setChanged();
    }

    @Override
    public void setChanged() {
        this.playerInventory.setChanged();
        this.playerInventory.player.containerMenu.broadcastChanges();
        this.playerInventory.player.inventoryMenu.broadcastChanges();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }


    @Override
    public int getMaxStackSize() {
        return this.playerInventory.getMaxStackSize();
    }



    @Override
    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        int realIndex = this.getPlayerInvIndex(index);
        return this.playerInventory.canPlaceItem(realIndex, stack);
    }

    @Override
    public void clearContent() {
        playerInventory.clearContent();
        this.setChanged();
    }
}
