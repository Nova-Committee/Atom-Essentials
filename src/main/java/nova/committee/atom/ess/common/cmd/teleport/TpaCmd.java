package nova.committee.atom.ess.common.cmd.teleport;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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
import nova.committee.atom.ess.core.model.TPARequest;
import nova.committee.atom.ess.core.model.TeleportPos;
import nova.committee.atom.ess.init.handler.PlayerDataHandler;
import nova.committee.atom.ess.init.handler.TpaHandler;
import nova.committee.atom.ess.util.TeleportUtil;
import nova.committee.atom.ess.util.text.I18Util;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 16:52
 * Version: 1.0
 */
public class TpaCmd{

    @ConfigField
    public static boolean isTPAEnable = true;

    @ConfigField
    public static String
            tpaAlias        = "tpa",
            tpaHereAlias    = "tpahere",
            tpHereAlias     = "tphere",
            tpAllHereAlias  = "tpallhere",
            tpAccept  = "tpaaccept",
            tpDeny  = "tpadeny";

    @ConfigField
    public static int tpaCooldownSeconds = 3;
    @ConfigField
    public static int maxTPARequestTimeoutSeconds = 30;

    private static long id = 0;


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(tpaAlias)
                        .then(Commands.argument("Target", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(PlayerDataHandler.getAllPlayerNamesFormatted(), builder))
                                .executes(context -> tpa(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "Target")))
                        )
                        .requires(context -> Static.cmdPermission(context, "atomess.command.tpa", false))

        );

        dispatcher.register(
                Commands.literal(tpaHereAlias)
                        .then(Commands.argument("Target", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(PlayerDataHandler.getAllPlayerNamesFormatted(), builder))
                                .executes(context -> tpaHere(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "Target")))
                        )
                        .requires(context -> Static.cmdPermission(context, "atomess.command.tpa.here", false))

        );

        dispatcher.register(
                Commands.literal(tpHereAlias)
                        .then(Commands.argument("Target", StringArgumentType.string())
                                .requires(commandSource -> commandSource.hasPermission(2))
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(PlayerDataHandler.getAllPlayerNamesFormatted(), builder))
                                .executes(context -> tpHere(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "Target")))
                        )
                        .requires(context -> Static.cmdPermission(context, "atomess.command.tp.here", true))
        );

        dispatcher.register(
                Commands.literal(tpAllHereAlias)
                        .requires(context -> Static.cmdPermission(context, "atomess.command.tp.here.all", true))
                        .executes(context -> tpAllHere(context.getSource().getPlayerOrException()))
        );

        dispatcher.register(
                Commands.literal(tpAccept)
                        .then(Commands.argument("id", LongArgumentType.longArg())
                                .executes(context -> tpaaccept(context.getSource().getPlayerOrException(), LongArgumentType.getLong(context, "id")))
                        )
                        .requires(context -> Static.cmdPermission(context, "atomess.command.tp.accept", false))

        );

        dispatcher.register(
                Commands.literal(tpDeny)
                        .then(Commands.argument("id", LongArgumentType.longArg())
                                .executes(context -> tpadeny(context.getSource().getPlayerOrException(), LongArgumentType.getLong(context, "id")))
                        )
                        .requires(context -> Static.cmdPermission(context, "atomess.command.tp.deny", false))

        );

    }


    private static int tpa(ServerPlayer source, String targetName) {
        ServerPlayer target = (ServerPlayer) PlayerDataHandler.getPlayer(targetName);
        if (target == null) {
            source.displayClientMessage(I18Util.getYellowTextFromI18n(true, false , false,
                    I18Util.getTranslationKey("message", "playerNotFound"), targetName), false);
            return 1;
        }
        AESPlayerData sourceData = PlayerDataHandler.getInstance(source);
        if (TeleportUtil.isInCooldown(source, sourceData.getLastTPATime(), tpaCooldownSeconds)) {
            return 1;
        }
        if (source.equals(target)) {
            source.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "cantTPASelf")), false);
            return 1;
        }

        TPARequest request = TpaHandler.getInstance(nextId(), source, target, false);
        String sourceName = sourceData.getName();

        source.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "requestSent"), targetName), false);

        MutableComponent line01 = I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "tpaRequestMessage"), sourceName);

        MutableComponent line0201 = I18Util.getYellowTextFromString(true, false, false, sourceName);
        MutableComponent line0202 = I18Util.getWhiteTextFromString(false, false, false, " -> ");
        MutableComponent line0203 = I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "you"));
        MutableComponent line02 = line0201.append(line0202).append(line0203);

        MutableComponent line0301 = I18Util.getGreenTextFromI18n(true, true, false,
                I18Util.getTranslationKey("message", "accept"));
        MutableComponent line0301Hover = I18Util.getGreenTextFromI18n(true, false, false,
                I18Util.getTranslationKey("message", "acceptHover"));
        line0301 = line0301.setStyle(line0301.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaaccept " + request.getId()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, line02.copy().append("\n").append(line0301Hover)))
        );
        MutableComponent line0302 = I18Util.getRedTextFromI18n(true, true, false,
                I18Util.getTranslationKey("message", "deny"));
        MutableComponent line0302Hover = I18Util.getRedTextFromI18n(true, false, false,
                I18Util.getTranslationKey("message", "denyHover"));
        line0302 = line0302.setStyle(line0302.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + request.getId()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, line02.copy().append("\n").append(line0302Hover)))
        );
        MutableComponent line03 = line0301.append(I18Util.getWhiteTextFromString(false, false, false, " | ")).append(line0302);

        target.displayClientMessage(new TextComponent(I18Util.getSeparator("=", 40)), false);
        target.displayClientMessage(line01, false);
        target.displayClientMessage(line02, false);
        target.displayClientMessage(line03, false);
        target.displayClientMessage(new TextComponent(I18Util.getSeparator("=", 40)), false);

        return 1;
    }

    private static int tpaHere(ServerPlayer source, String targetName) {
        ServerPlayer target = (ServerPlayer) PlayerDataHandler.getPlayer(targetName);
        if (target == null) {
            source.displayClientMessage(I18Util.getYellowTextFromI18n(true, false , false,
                    I18Util.getTranslationKey("message", "playerNotFound"), targetName), false);
            return 1;
        }
        if (source.equals(target)) {
            source.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "cantTPASelf")), false);
            return 1;
        }
        AESPlayerData sourceData = PlayerDataHandler.getInstance(source);
        if (TeleportUtil.isInCooldown(source, sourceData.getLastTPATime(), tpaCooldownSeconds)) {
            return 1;
        }

        TPARequest request = TpaHandler.getInstance(nextId(), source, target, true);

        String sourceName = sourceData.getName();

        source.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "requestSent"), targetName), false);

        MutableComponent line01 = I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "tpaHereRequestMessage"), sourceName);

        MutableComponent line0201 = I18Util.getGreenTextFromString(false, false, false, "You");
        MutableComponent line0202 = I18Util.getWhiteTextFromString(false, false, false, " -> ");
        MutableComponent line0203 = I18Util.getYellowTextFromString(true, false, false, sourceName);
        MutableComponent line02 = line0201.append(line0202).append(line0203);

        MutableComponent line0301 = I18Util.getGreenTextFromI18n(true, true, false,
                I18Util.getTranslationKey("message", "accept"));
        MutableComponent line0301Hover = I18Util.getGreenTextFromI18n(true, false, false,
                I18Util.getTranslationKey("message", "acceptHover"));
        line0301 = line0301.setStyle(line0301.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaaccept " + request.getId()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, line02.copy().append("\n").append(line0301Hover)))
        );
        MutableComponent line0302 = I18Util.getRedTextFromI18n(true, true, false,
                I18Util.getTranslationKey("message", "deny"));
        MutableComponent line0302Hover = I18Util.getRedTextFromI18n(true, false, false,
                I18Util.getTranslationKey("message", "denyHover"));
        line0302 = line0302.setStyle(line0302.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + request.getId()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, line02.copy().append("\n").append(line0302Hover)))
        );
        MutableComponent line03 = line0301.append(I18Util.getWhiteTextFromString(false, false, false, " | ")).append(line0302);

        target.displayClientMessage(new TextComponent(I18Util.getSeparator("=", 40)), false);
        target.displayClientMessage(line01, false);
        target.displayClientMessage(line02, false);
        target.displayClientMessage(line03, false);
        target.displayClientMessage(new TextComponent(I18Util.getSeparator("=", 40)), false);

        return 1;
    }

    private static int tpaaccept(ServerPlayer player, long id) {
        TPARequest request = TpaHandler.getInstance(id);
        if (request == null) {
            player.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "requestNotFound")), false);
            return 1;
        }
        ServerPlayer source = request.getSource();
        AESPlayerData sourceData = PlayerDataHandler.getInstance(source);
        sourceData.addTeleportHistory(new TeleportPos(source));
        TeleportUtil.teleport(source, new TeleportPos(request.getTarget()));
        sourceData.setLastTPATime(System.currentTimeMillis());
        player.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "tpaSuccessTarget"), sourceData.getName()), true);
        source.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "tpaSuccessSource"), player.getGameProfile().getName()), true);
        TpaHandler.getTpaRequest().remove(id);
        return 1;
    }

    private static int tpadeny(ServerPlayer player, long id) {
        TPARequest request = TpaHandler.getInstance(id);
        if (request == null) {
            player.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "requestNotFound")), false);
            return 1;
        }
        TpaHandler.getTpaRequest().remove(request.getId());
        ServerPlayer source = request.getSource();
        source.displayClientMessage(I18Util.getRedTextFromI18n(true, false, false,
                I18Util.getTranslationKey("message", "tpaDenySource"), player.getGameProfile().getName()), false);
        player.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "ok")), false);
        return 1;
    }

    private static int tpHere(ServerPlayer source, String targetName) {
        ServerPlayer target = (ServerPlayer) PlayerDataHandler.getPlayer(targetName);
        if (target == null) {
            source.displayClientMessage(I18Util.getYellowTextFromI18n(true, false , false,
                    I18Util.getTranslationKey("message", "playerNotFound"), targetName), false);
            return 1;
        }
        TeleportUtil.teleport(target, new TeleportPos(source));
        return 1;
    }

    private static int tpAllHere(ServerPlayer source) {
        Static.SERVER.getPlayerList().getPlayers().stream()
                .filter(player -> !player.equals(source))
                .forEach(player -> TeleportUtil.teleport(player, new TeleportPos(source)));
        return 1;
    }

    private static long nextId() {
        return ++id;
    }

}
