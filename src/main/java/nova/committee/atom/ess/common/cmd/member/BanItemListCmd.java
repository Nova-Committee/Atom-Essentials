package nova.committee.atom.ess.common.cmd.member;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.api.common.config.ConfigField;
import nova.committee.atom.ess.init.handler.BanItemHandler;
import nova.committee.atom.ess.util.BanUtil;

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
        //BanUtil.printListMessage(BanItemHandler.itemListToString(BanItemHandler.BANNED_ITEMS), source);
        source.displayClientMessage(new TextComponent("[封禁]已经封禁的物品: ").append(BanItemHandler.itemListToString(BanItemHandler.BANNED_ITEMS)), false);
        return 1;
    }
}
