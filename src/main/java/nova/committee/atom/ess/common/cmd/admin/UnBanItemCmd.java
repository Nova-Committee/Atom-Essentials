package nova.committee.atom.ess.common.cmd.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.api.common.config.ConfigField;
import nova.committee.atom.ess.init.handler.BanItemHandler;
import nova.committee.atom.ess.util.BanUtil;

import java.util.Objects;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 17:24
 * Version: 1.0
 */
public class UnBanItemCmd {
    @ConfigField
    public static String unbanItem = "unbanitem";


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(unbanItem)
                        .requires(context -> Static.cmdPermission(context, "atomess.command.ban.item.del", true))
                        .then(Commands.argument("item", ItemArgument.item())
                                .executes(UnBanItemCmd::unban))
                        .then(Commands.literal("all")
                                .executes(UnBanItemCmd::unbanAll))

        );
    }


    private static int unban(CommandContext<CommandSourceStack> context) {
        try {
            BanUtil.removeItemFromJson(BanItemHandler.BANLIST, ItemArgument.getItem(context, "item").getItem());
            context.getSource().getServer().getPlayerList().broadcastMessage(new TextComponent("解封物品: ")
                    .append(Objects.requireNonNull(ItemArgument.getItem(context, "item").getItem().getRegistryName()).toString()), ChatType.CHAT, Util.NIL_UUID);
        } catch (IndexOutOfBoundsException e) {
            context.getSource().sendFailure(new TextComponent("这个物品不能被封禁!"));
        }
        return 1;
    }


    public static int unbanAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BanUtil.removeAllItemsFromJson(BanItemHandler.BANLIST);
        context.getSource().getServer().getPlayerList().broadcastMessage(new TextComponent("所有物品已经被解封!"), ChatType.CHAT, Util.NIL_UUID);
        return 1;
    }

}
