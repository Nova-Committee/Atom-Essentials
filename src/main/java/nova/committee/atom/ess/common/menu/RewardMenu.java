package nova.committee.atom.ess.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import nova.committee.atom.ess.common.slot.AccessRewardSlot;
import nova.committee.atom.ess.common.slot.RewardSlot;
import nova.committee.atom.ess.init.registry.ModMenuTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/21 17:55
 * Version: 1.0
 */
public class RewardMenu extends AbstractContainerMenu {
    public static final int PLAYER_SLOT_START = 9;
    public static final int PLAYER_INVENTORY_SLOT_START = PLAYER_SLOT_START;
    public static final int PLAYER_SLOT_STOP = 3 * 9 + PLAYER_INVENTORY_SLOT_START + 8;


    private static int containerSize = 32;
    private static int slotSize = 18;
    private static int rewardSlotSizeX = 23;
    private static int rewardSlotSizeY = 28;
    // Misc
    protected final Level level;
    protected final Player player;
    private Container rewardsContainer = new SimpleContainer(containerSize);
    private Container rewardsUserContainer = new SimpleContainer(containerSize);
    private int rewardedDays = 0;

    public RewardMenu(final int windowId, final Inventory playerInventory) {
        this(windowId, playerInventory, ModMenuTypes.rewardMenuType);
    }

    public RewardMenu(final int windowId, final Inventory playerInventory, MenuType<?> menuType) {
        super(menuType, windowId);

        // Other
        this.player = playerInventory.player;
        this.level = this.player.getLevel();

        // Sync rewarded days
        this.rewardedDays = level.isClientSide ? RewardClientData.getRewardedDaysForCurrentMonth()
                : RewardUserData.get().getRewardedDaysForCurrentMonth(player.getUUID());

        // Sync possible rewards items for current month
        List<ItemStack> rewardsForCurrentMonth =
                level.isClientSide ? RewardClientData.getGeneralRewardsForCurrentMonth()
                        : RewardData.get().getRewardsForCurrentMonth();
        if (!rewardsForCurrentMonth.isEmpty()) {
            for (int index = 0; index < rewardsForCurrentMonth.size(); index++) {
                this.rewardsContainer.setItem(index, rewardsForCurrentMonth.get(index));
            }
        }

        // Sync user rewarded items for current month
        List<ItemStack> userRewards =
                level.isClientSide ? RewardClientData.getUserRewardsForCurrentMonth()
                        : RewardUserData.get().getRewardsForCurrentMonth(player.getUUID());
        if (!userRewards.isEmpty()) {
            for (int index = 0; index < userRewards.size(); index++) {
                this.rewardsUserContainer.setItem(index, userRewards.get(index));
            }
        }

        // Rewards Slots
        int rewardStartPositionY = 17;
        int rewardStartPositionX = 9;
        for (int rewardRow = 0; rewardRow < 5; ++rewardRow) {
            for (int rewardColumn = 0; rewardColumn < 7; ++rewardColumn) {
                int rewardSlotIndex = rewardColumn + rewardRow * 7;
                if (userRewards.size() > rewardSlotIndex && userRewards.get(rewardSlotIndex) != null
                        && !userRewards.get(rewardSlotIndex).isEmpty()) {
                    this.addSlot(new AccessRewardSlot(this.rewardsUserContainer, rewardSlotIndex,
                            rewardStartPositionX + rewardColumn * rewardSlotSizeX,
                            rewardStartPositionY + rewardRow * rewardSlotSizeY, this));
                } else if (rewardsForCurrentMonth.size() > rewardSlotIndex) {
                    this.addSlot(new RewardSlot(this.rewardsContainer, rewardSlotIndex,
                            rewardStartPositionX + rewardColumn * rewardSlotSizeX,
                            rewardStartPositionY + rewardRow * rewardSlotSizeY, this));
                }
            }
        }

        // Player Inventory Slots
        int playerInventoryStartPositionY = 168;
        int playerInventoryStartPositionX = 6;
        for (int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
            for (int inventoryColumn = 0; inventoryColumn < 9; ++inventoryColumn) {
                this.addSlot(new Slot(playerInventory, inventoryColumn + inventoryRow * 9 + 9,
                        playerInventoryStartPositionX + inventoryColumn * slotSize,
                        playerInventoryStartPositionY + inventoryRow * slotSize));
            }
        }

        // Player Hotbar Slots
        int hotbarStartPositionY = 226;
        int hotbarStartPositionX = 6;
        for (int playerInventorySlot = 0; playerInventorySlot < 9; ++playerInventorySlot) {
            this.addSlot(new Slot(playerInventory, playerInventorySlot,
                    hotbarStartPositionX + playerInventorySlot * slotSize, hotbarStartPositionY));
        }
    }

    public static RewardMenu getClientSideInstance(int id, Inventory playerInventory, FriendlyByteBuf data) {
        return new RewardMenu(id, playerInventory);
    }

    public void syncRewardsUserContainer(Player player) {
        List<ItemStack> userRewards = new ArrayList<>();
        for (int index = 0; index < this.rewardsUserContainer.getContainerSize(); index++) {
            ItemStack itemStack = this.rewardsUserContainer.getItem(index);
            if (itemStack != null && !itemStack.isEmpty()) {
                userRewards.add(itemStack);
            }
        }
        if (level.isClientSide) {
            RewardClientData.setUserRewardsForCurrentMonth(userRewards);
        } else {
            RewardUserData.get().setRewardsForCurrentMonth(player.getUUID(), userRewards);
        }
    }

    public int getRewardedDays() {
        return this.rewardedDays;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = slot.getItem();

        // Store changes if itemStack is not empty.
        if (itemStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
