package nova.committee.atom.ess.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import nova.committee.atom.ess.init.registry.ModMenuTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 17:28
 * Version: 1.0
 */
public class TrashcanMenu extends AbstractContainerMenu {

    private final Inventory playerInventory;
    private final ItemStackHandler itemStackHandler;
    private final TrashcanData trashcan;




    private TrashcanMenu(int id, Inventory playerInventory, TrashcanData trashcan) {
        super(ModMenuTypes.trashcanContainerType, id);
        this.addDataSlots(trashcan);
        this.playerInventory = playerInventory;
        this.itemStackHandler = trashcan.getCurrentContents();
        this.trashcan = trashcan;
        this.addSlots();

    }

    public static TrashcanMenu getServerSideInstance(int id, Inventory playerInventory, TrashcanData trashcan) {
        return new TrashcanMenu(id, playerInventory, trashcan);
    }

    public static TrashcanMenu getClientSideInstance(int id, Inventory playerInventory, FriendlyByteBuf data) {
        return new TrashcanMenu(id, playerInventory, new TrashcanData());
    }



    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null) return ItemStack.EMPTY;
        ItemStack itemStack = slot.getItem();
        if (index < 36) {
            if (!this.moveItemStackTo(itemStack, 36, 85, false)) return ItemStack.EMPTY;
        } else {
            if (!this.moveItemStackTo(itemStack, 9, 35, false)) {
                if (!this.moveItemStackTo(itemStack, 0, 8, true)) return ItemStack.EMPTY;
            }
        }
        return itemStack;
    }

    private void addSlots() {
        final int slotLength = 18, startX = 8, playerHotbarStartY = 198, playerMainInvStartY = 140, chestInvStartY = 18;

        int index = 0;
        // Player hotbar 0 - 8 inclusive
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(this.playerInventory, index, startX + slotLength * i, playerHotbarStartY));
            index++;
        }
        // player main inv 9 - 35 inclusive
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(this.playerInventory, index, startX + slotLength * j, playerMainInvStartY + slotLength * i));
                index++;
            }
        }
        // chest inv 36 - 49
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 7; j++) {
                this.addSlot(new SlotItemHandler(this.itemStackHandler, index, startX + slotLength * j, chestInvStartY + slotLength * i));
                index++;
            }
        }
        // chest inv 50 - 85
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new SlotItemHandler(this.itemStackHandler, index, startX + slotLength * j, chestInvStartY + slotLength * i));
                index++;
            }
        }
    }

    public TrashcanData getTrashcan() {
        return this.trashcan;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }



    public static class TrashcanData implements ContainerData {

        public static final int SIZE = 50 + 36;

        private final ItemStackHandler currentContents;
        private long lastCleanLong;
        private int nextCleanSeconds;

        public TrashcanData() {
            this.currentContents = new ItemStackHandler(SIZE);
        }

        public void clear() {
            for (int i = 0; i < 50; i++) {
                this.currentContents.setStackInSlot(i + 36, ItemStack.EMPTY);
            }
            this.lastCleanLong = System.currentTimeMillis();
        }

        public ItemStackHandler getCurrentContents() {
            return currentContents;
        }

        public int getNextCleanSeconds() {
            return this.nextCleanSeconds;
        }

        public void setNextCleanSeconds(int nextCleanSeconds) {
            this.nextCleanSeconds = nextCleanSeconds;
        }

        public long getLastCleanLong() {
            return lastCleanLong;
        }

        public void setLastCleanLong(long lastCleanLong) {
            this.lastCleanLong = lastCleanLong;
        }

        @Override
        public int get(int index) {
            return this.nextCleanSeconds;
        }

        @Override
        public void set(int index, int value) {
            this.nextCleanSeconds = value;
        }

        @Override
        public int getCount() {
            return 1;
        }


    }
}
