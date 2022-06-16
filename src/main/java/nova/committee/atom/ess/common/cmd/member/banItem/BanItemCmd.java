package nova.committee.atom.ess.common.cmd.member.banItem;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
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
public class BanItemCmd {
    @ConfigField
    public static String banItem = "banitem";


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(banItem)
                        .executes(BanItemCmd::ban)
                        .requires(context -> Static.cmdPermission(context, "atomess.command.ban.item", true))
        );
    }


    private static int ban(CommandContext<CommandSourceStack> context) {
        BanUtil.appendItemToJson(BanItemHandler.BANLIST, ItemArgument.getItem(context, "item").getItem());
        context.getSource().getServer().getPlayerList().broadcastMessage(new TextComponent("Item banned: ")
                .append(Objects.requireNonNull(ItemArgument.getItem(context, "item").getItem().getRegistryName()).toString()),
                ChatType.CHAT, Util.NIL_UUID);
        return 1;
    }
}
