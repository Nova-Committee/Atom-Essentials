package nova.committee.atom.ess.common.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import nova.committee.atom.ess.common.menu.RewardMenu;
import nova.committee.atom.ess.init.registry.ModItems;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/21 17:54
 * Version: 1.0
 */
public class AccessRewardSlot extends Slot {
    private RewardMenu menu;

    public AccessRewardSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }

    public AccessRewardSlot(Container container, int index, int x, int y, RewardMenu menu) {
        super(container, index, x, y);
        this.menu = menu;
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack itemStack) {
        if (!getItem().is(ModItems.rewardItem)) {
            set(new ItemStack(ModItems.rewardItem));
            this.menu.syncRewardsUserContainer(player);
        } else {
            this.setChanged();
        }
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return !getItem().is(ModItems.rewardItem);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return false;
    }
}
