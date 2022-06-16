package nova.committee.atom.ess.common.cmd.teleport;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.api.common.config.ConfigField;
import nova.committee.atom.ess.core.model.AESPlayerData;
import nova.committee.atom.ess.core.model.TeleportPos;
import nova.committee.atom.ess.init.handler.PlayerDataHandler;
import nova.committee.atom.ess.init.handler.TeleportHandler;
import nova.committee.atom.ess.util.TeleportUtil;
import nova.committee.atom.ess.util.text.I18Util;

import java.util.Map;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 17:03
 * Version: 1.0
 */
public class WarpCmd {
    @ConfigField
    public static boolean isWarpEnable = true;
    @ConfigField
    public static String
            setWarpAlias    = "setwarp",
            warpAlias       = "warp",
            listWarpsAlias  = "listwarps",
            delWarpAlias    = "delwarp";
    @ConfigField
    public static int warpCooldownSeconds = 3;


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(setWarpAlias)
                        .then(Commands.argument("name", StringArgumentType.string())
                                .requires(commandSource -> commandSource.hasPermission(2))
                                .executes(context -> setWarp(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name")))
                        )
                        .requires(context -> Static.cmdPermission(context, "atomess.command.warp.set", true))
        );

        dispatcher.register(
                Commands.literal(warpAlias)
                        .then(Commands.argument("name", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(TeleportHandler.WARPS.keySet(), builder))
                                .executes(context -> warp(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name")))
                        )
                        .requires(context -> Static.cmdPermission(context, "atomess.command.warp", false))

        );

        dispatcher.register(
                Commands.literal(listWarpsAlias)
                        .executes(context -> listWarps(context.getSource().getPlayerOrException()))
                        .requires(context -> Static.cmdPermission(context, "atomess.command.warp.list", false))

        );

        dispatcher.register(
                Commands.literal(delWarpAlias)
                        .then(Commands.argument("name", StringArgumentType.string())
                                .requires(commandSource -> commandSource.hasPermission(2))
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(TeleportHandler.WARPS.keySet(), builder))
                                .executes(context -> delWarp(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name")))
                        )
                        .requires(context -> Static.cmdPermission(context, "atomess.command.warp.del", true))
        );
    }





    private static int setWarp(ServerPlayer player, String name) {
        TeleportHandler.WARPS.put(name, new TeleportPos(player.getLevel().dimension(), player.getOnPos()));
        player.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "setWarpSuccess"), name), false);
        return 1;
    }

    private static int warp(ServerPlayer player, String name) {
        AESPlayerData data = PlayerDataHandler.getInstance(player);
        if (TeleportUtil.isInCooldown(player, data.getLastWarpTime(), warpCooldownSeconds)) {
            return 1;
        }
        if (!TeleportHandler.WARPS.containsKey(name)) {
            player.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "warpNotFound"), name), false);
            return 1;
        }
        data.addTeleportHistory(new TeleportPos(player));
        TeleportUtil.teleport(player, TeleportHandler.WARPS.get(name));
        data.setLastWarpTime(System.currentTimeMillis());
        player.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "warpSuccess"), name), true);
        return 1;
    }

    private static int listWarps(ServerPlayer player) {
        Thread thread = new Thread(() -> {
            if (TeleportHandler.WARPS.isEmpty()) {
                player.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                        I18Util.getTranslationKey("message", "noWarp")), false);
                return;
            }
            player.displayClientMessage(new TextComponent(I18Util.getSeparator("=", 20)), false);
            int index = 1;
            for (Map.Entry<String, TeleportPos> warp : TeleportHandler.WARPS.entrySet()) {
                MutableComponent text = I18Util.getGreenTextFromString(false, true, false, index + ": " + warp.getKey());
                MutableComponent hoverText = new TextComponent(warp.getValue().toString() + "\n")
                        .append(I18Util.getGreenTextFromI18n(false, false, false,
                                I18Util.getTranslationKey("message", "clickToTeleport")));
                player.displayClientMessage(text.setStyle(text.getStyle()
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp.getKey()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))), false
                );
            }
            player.displayClientMessage(new TextComponent(I18Util.getSeparator("=", 20)), false);
        });
        thread.start();
        return 1;
    }

    private static int delWarp(ServerPlayer player, String name) {
        if (!TeleportHandler.WARPS.containsKey(name)) {
            player.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "warpNotFound"), name), false);
            return 1;
        }
        TeleportHandler.WARPS.remove(name);
        player.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "delWarpSuccess"), name), false);
        return 1;
    }
}
