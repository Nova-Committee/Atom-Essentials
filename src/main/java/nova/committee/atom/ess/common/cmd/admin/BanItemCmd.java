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
import java.util.Optional;

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


    private static int ban(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BanUtil.appendItemToJson(BanItemHandler.BANLIST, context.getSource().getPlayerOrException().getMainHandItem().getItem());
        context.getSource().getServer().getPlayerList().broadcastMessage(new TextComponent("[封禁]封禁物品: ")
                        .append(Objects.requireNonNull(context.getSource().getPlayerOrException().getMainHandItem().getItem().getRegistryName()).toString()),
                ChatType.CHAT, Util.NIL_UUID);
        return 1;
    }
}
