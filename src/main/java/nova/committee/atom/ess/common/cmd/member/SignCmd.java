package nova.committee.atom.ess.common.cmd.member;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.api.common.config.ConfigField;
import nova.committee.atom.ess.common.menu.RewardMenu;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/25 14:38
 * Version: 1.0
 */
public class SignCmd {

    @ConfigField
    public static String sign = "sign";


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(sign)
                        .executes(context -> sign(context.getSource().getPlayerOrException()))
                        .requires(context -> Static.cmdPermission(context, "atomess.command.sign", false))
        );
    }


    private static int sign(ServerPlayer source) {
        MenuProvider provider = new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return new TextComponent("Rewards");
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
                return new RewardMenu(windowId, inventory);
            }
        };
        NetworkHooks.openGui(source, provider, buffer -> {
        });

        return 0;
    }
}
