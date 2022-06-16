package nova.committee.atom.ess.init.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/14 21:48
 * Version: 1.0
 */
@Event.HasResult
public class BanItemEvent extends Event{
    public final ItemStack stack;

    public BanItemEvent(ItemStack stack) {
        this.stack = stack;
    }
}
