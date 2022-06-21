package nova.committee.atom.ess.common.cmd.misc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.api.common.config.ConfigField;

public class HatCmd {
    @ConfigField
    public static boolean canHat = true;

    @ConfigField
    public static String hatAlias = "hat";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(hatAlias)
                .executes(HatCmd::execute)
                .requires(context -> Static.cmdPermission(context, "atomess.command.misc.hat", false))
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (!canHat) return 0;
        final var player = context.getSource().getPlayerOrException();
        final var helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (EnchantmentHelper.hasBindingCurse(helmet)) return 0;
        final var handIn = player.getItemBySlot(EquipmentSlot.MAINHAND);
        player.setItemSlot(EquipmentSlot.HEAD, handIn);
        player.setItemSlot(EquipmentSlot.MAINHAND, helmet);
        return 1;
    }
}
