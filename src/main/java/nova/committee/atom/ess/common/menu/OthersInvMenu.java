package nova.committee.atom.ess.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import nova.committee.atom.ess.common.container.OthersInvContainer;
import nova.committee.atom.ess.init.registry.ModMenuTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 17:38
 * Version: 1.0
 */
public class OthersInvMenu extends AbstractContainerMenu {
    private static final int OTHER_INV_SIZE = 77;

    private static final int SLOT_LENGTH = 18, START_X = 8,
            TARGETINV_HOTBAR_START_Y = 99, TARGETINV_MAININV_START_Y = 41,
            TARGETINV_ARMORANDSECONDHAND_START_Y = 19, TARGETINV_ARMOR_START_X = 62, TARGETINV_SECONDHAND_START_X = 98,
            PLAYER_MAININV_START_Y = 132, PLAYER_HOTBAR_START_Y = 190;


    private final Inventory playerInventory;
    private final Container targetInventory;
    public Player targetPlayer;

    public OthersInvMenu(int id, Inventory playerInventory, Container targetInventory) {
        super(ModMenuTypes.othersContainerType, id);
        this.playerInventory = playerInventory;
        this.targetInventory = targetInventory;
        this.targetPlayer = ((Inventory)targetInventory).player;
        this.targetInventory.startOpen(this.playerInventory.player);
        this.addSlots();
    }

    public static OthersInvMenu getClientSideInstance(int id, Inventory playerInventory, FriendlyByteBuf data) {
        return new OthersInvMenu(id, playerInventory, new SimpleContainer(OTHER_INV_SIZE));
    }

    public static OthersInvMenu getServerSideInstance(int id, Inventory playerInventory, Inventory targetInv) {
        return new OthersInvMenu(id, playerInventory, new OthersInvContainer(targetInv));
    }

    private void addSlots() {
        int index = 0;
        // Player hotbar 0 - 8 inclusive
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(this.playerInventory, index, START_X + SLOT_LENGTH * i, PLAYER_HOTBAR_START_Y));
            index++;
        }
        // player main inv 9 - 35 inclusive
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(this.playerInventory, index, START_X + SLOT_LENGTH * j, PLAYER_MAININV_START_Y + SLOT_LENGTH * i));
                index++;
            }
        }
        // Target inv hot bar 36 - 44 inclusive
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(this.targetInventory, index, START_X + SLOT_LENGTH * i, TARGETINV_HOTBAR_START_Y));
            index++;
        }
        // Target inv main inventory 45 - 71
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(this.targetInventory, index, START_X + SLOT_LENGTH * j, TARGETINV_MAININV_START_Y + SLOT_LENGTH * i));
                index++;
            }
        }
        // Target inv armor slots 72 - 75
        for (int i = 0; i < 4; i++) {
            this.addSlot(new Slot(this.targetInventory, index, TARGETINV_ARMOR_START_X - SLOT_LENGTH * i, TARGETINV_ARMORANDSECONDHAND_START_Y));
            index++;
        }
        // Target inv second hand 76
        this.addSlot(new Slot(this.targetInventory, index, TARGETINV_SECONDHAND_START_X, TARGETINV_ARMORANDSECONDHAND_START_Y));
    }



    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        this.targetInventory.stopOpen(playerIn);
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemStack = this.slots.get(index).getItem();
        if (!itemStack.sameItem(ItemStack.EMPTY)) {
            if (index < 36) {
                Item item = itemStack.getItem();
                if (item instanceof ArmorItem) {
                    ResourceLocation registryName = item.getRegistryName();
                    if (registryName != null) {
                        String s = registryName.toString();
                        if (s.contains("helmet")) {
                            if (this.moveItemStackTo(itemStack, 75, 76, false)) return itemStack;
                        } else if (s.contains("chestplate")) {
                            if (this.moveItemStackTo(itemStack, 74, 75, false)) return itemStack;
                        } else if (s.contains("leggings")) {
                            if (this.moveItemStackTo(itemStack, 73, 74, false)) return itemStack;
                        } else if (s.contains("boots")) {
                            if (this.moveItemStackTo(itemStack, 72, 73, false)) return itemStack;
                        }
                    }
                }
                if (!this.moveItemStackTo(itemStack, 45, 71, false)) {
                    if (!this.moveItemStackTo(itemStack, 36, 44, false)) return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemStack, 9, 35, false)) {
                    if (!this.moveItemStackTo(itemStack, 0, 8, false)) return ItemStack.EMPTY;
                }
            }
        }
        return itemStack;
    }


}
