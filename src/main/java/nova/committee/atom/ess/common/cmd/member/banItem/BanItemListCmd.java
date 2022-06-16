package nova.committee.atom.ess.common.cmd.member.banItem;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.api.common.config.ConfigField;
import nova.committee.atom.ess.init.handler.BanItemHandler;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 17:24
 * Version: 1.0
 */
public class BanItemListCmd {
    @ConfigField
    public static String banItemList = "banitemlist";


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(banItemList)
                        .executes(context -> banList(context.getSource().getPlayerOrException()))
                        .requires(context -> Static.cmdPermission(context, "atomess.command.ban.item.list", false))
        );
    }


    private static int banList(ServerPlayer source) {
        source.displayClientMessage(new TextComponent("Items banned: ").append(BanItemHandler.itemListToString(BanItemHandler.BANNED_ITEMS)), false);
        return 1;
    }
}
