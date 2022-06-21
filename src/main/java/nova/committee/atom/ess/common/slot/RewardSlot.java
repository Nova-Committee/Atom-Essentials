package nova.committee.atom.ess.common.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import nova.committee.atom.ess.common.menu.RewardMenu;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/21 17:53
 * Version: 1.0
 */
public class RewardSlot extends Slot {
    protected RewardMenu menu;

    public RewardSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }

    public RewardSlot(Container container, int index, int x, int y, RewardMenu menu) {
        super(container, index, x, y);
        this.menu = menu;
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return false;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return false;
    }

}
