package nova.committee.atom.ess.common.cmd.teleport;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
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
import nova.committee.atom.ess.util.TeleportUtil;
import nova.committee.atom.ess.util.text.I18Util;

import java.util.Map;
import java.util.Set;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 16:21
 * Version: 1.0
 */
public class HomeCmd{

    @ConfigField
    public static boolean isHomeEnable = true;
    @ConfigField
    public static String
            setHomeAlias        = "sethome",
            homeAlias           = "home",
            homeOtherAlias      = "homeother",
            delHomeAlias        = "delhome",
            listHomesAlias      = "listhomes",
            listOtherHomesAlias = "listotherhomes",
            delOtherHomeAlias   = "delotherhome";

    @ConfigField
    public static int homeCooldownSeconds = 3;
    @ConfigField
    public static int homeOtherCooldownSeconds = 3;
    @ConfigField
    public static int maxHomes = 5;


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(setHomeAlias)
                        .then(Commands.argument("Name", StringArgumentType.string())
                                .executes(context -> setHome(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "Name"))))
                        .executes(context -> setHome(context.getSource().getPlayerOrException(), "home"))
                        .requires(context -> Static.cmdPermission(context, "atomess.command.home.set", false))

        );

        dispatcher.register(
                Commands.literal(homeAlias)
                        .then(Commands.argument("Name", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(PlayerDataHandler.getInstance(context.getSource().getPlayerOrException()).getHomes().keySet(), builder))
                                .executes(context -> home(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "Name")))
                        )
                        .executes(context -> home(context.getSource().getPlayerOrException(), "home"))
                        .requires(context -> Static.cmdPermission(context, "atomess.command.home", false))
        );
        dispatcher.register(
                Commands.literal(homeOtherAlias)
                        .then(Commands.argument("Other", EntityArgument.player())
                                .then(Commands.argument("HomeName", StringArgumentType.string())
                                        .requires(commandSource -> commandSource.hasPermission(2))
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(PlayerDataHandler.getInstance(EntityArgument.getPlayer(context, "Other")).getHomes().keySet(), builder))
                                        .executes(context -> homeOther(context.getSource().getPlayerOrException(),
                                                EntityArgument.getPlayer(context, "Other"),
                                                StringArgumentType.getString(context, "HomeName"))
                                        )
                                )
                        )
                        .requires(context -> Static.cmdPermission(context, "atomess.command.home.other", true))
        );

        dispatcher.register(
                Commands.literal(delHomeAlias)
                        .then(Commands.argument("Name", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(PlayerDataHandler.getInstance(context.getSource().getPlayerOrException()).getHomes().keySet(), builder))
                                .executes(context -> delHome(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "Name"))))
                        .executes(context -> delHome(context.getSource().getPlayerOrException(), "home"))
                        .requires(context -> Static.cmdPermission(context, "atomess.command.home.del", false))

        );

        dispatcher.register(
                Commands.literal(delOtherHomeAlias)
                        .then(Commands.argument("Target", EntityArgument.player())
                                .then(Commands.argument("Name", StringArgumentType.string())
                                        .requires(source -> source.hasPermission(2))
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(PlayerDataHandler.getInstance(EntityArgument.getPlayer(context, "Target")).getHomes().keySet(), builder))
                                        .executes(context -> delOthersHome(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "Target"), StringArgumentType.getString(context, "Name")))
                                )
                        )
                        .requires(context -> Static.cmdPermission(context, "atomess.command.home.other.del", true))
        );

        dispatcher.register(
                Commands.literal(listHomesAlias)
                        .executes(context -> listHome(context.getSource().getPlayerOrException()))
                        .requires(context -> Static.cmdPermission(context, "atomess.command.home.list", false))

        );
        dispatcher.register(
                Commands.literal(listOtherHomesAlias)
                        .then(Commands.argument("other", EntityArgument.player())
                                .requires(commandSource -> commandSource.hasPermission(2))
                                .executes(context -> listOthersHome(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "other"))))
                        .requires(context -> Static.cmdPermission(context, "atomess.command.home.other.list", true))
        );
    }



    private static int setHome(ServerPlayer player, String name) {
        AESPlayerData data = PlayerDataHandler.getInstance(player);
        if (data.getHomes().size() >= maxHomes) {
            player.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "reachMaxHome"), maxHomes), false);
            return 1;
        }
        if (data.getHomePos(name) != null) {
            player.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "homeExist"), name), false);
            return 1;
        }
        data.setHome(name, new TeleportPos(player.getLevel().dimension(), player.getOnPos()));
        player.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "setHomeSuccess"), name), false);
        return 1;
    }

    private static int home(ServerPlayer player, String name) {
        AESPlayerData data = PlayerDataHandler.getInstance(player);
        if (TeleportUtil.isInCooldown(player, data.getLastHomeTime(), homeCooldownSeconds)) {
            return 1;
        }
        TeleportPos homePos = data.getHomePos(name);
        if (homePos == null) {
            if (data.getHomes().size() == 1) {
                home(player, data.getHomes().keySet().toArray()[0].toString());
                return 1;
            }
            player.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "homeNotFound"), name), false);
            return 1;
        }
        data.addTeleportHistory(new TeleportPos(player.getLevel().dimension(), player.getOnPos()));
        TeleportUtil.teleport(player, homePos);
        data.setLastHomeTime(System.currentTimeMillis());
        player.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "homeSuccess"), name), true);
        return 1;
    }

    private static int homeOther(ServerPlayer source, ServerPlayer other, String homeName) {
        AESPlayerData sourceData = PlayerDataHandler.getInstance(source);
        if (TeleportUtil.isInCooldown(source, sourceData.getLastHomeOtherTime(), homeOtherCooldownSeconds)) {
            return 1;
        }
        AESPlayerData otherData = PlayerDataHandler.getInstance(other);
        TeleportPos otherHomePos = otherData.getHomePos(homeName);
        if (otherHomePos == null) {
            source.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "homeOtherNotFound"), otherData.getName(), homeName), false);
            return 1;
        }
        sourceData.addTeleportHistory(new TeleportPos(source.getLevel().dimension(), source.getOnPos()));
        TeleportUtil.teleport(source, otherHomePos);
        sourceData.setLastHomeOtherTime(System.currentTimeMillis());
        source.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "otherHomeSuccess"), otherData.getName(), homeName), true);
        return 1;
    }

    private static int delHome(ServerPlayer player, String name) {
        AESPlayerData data = PlayerDataHandler.getInstance(player);
        TeleportPos homePos = data.getHomePos(name);
        if (homePos == null) {
            player.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "homeNotFound"), name), false);
            return 1;
        }
        data.delHome(name);
        player.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "delHomeSuccess"), name), false);
        return 1;
    }

    private static int delOthersHome(ServerPlayer source, ServerPlayer target, String name) {
        AESPlayerData data = PlayerDataHandler.getInstance(target);
        if (!data.getHomes().containsKey(name)) {
            source.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "homeOtherNotFound"), data.getName(), name), false);
            return 1;
        }
        data.delHome(name);
        source.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "delOthersHomeSuccess"), data.getName(), name), false);
        return 1;
    }

    private static int listHome(ServerPlayer player) {
        Thread thread = new Thread(() -> {
            AESPlayerData data = PlayerDataHandler.getInstance(player);
            Map<String, TeleportPos> homes = data.getHomes();
            if (homes.isEmpty()) {
                player.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                        I18Util.getTranslationKey("message", "noHome")), false);
                return;
            }
            player.displayClientMessage(new TextComponent(I18Util.getSeparator("=", 20)), false);
            Set<String> names = homes.keySet();
            int index = 1;
            for (String name : names) {
                TeleportPos teleportPos = homes.get(name);
                MutableComponent text = I18Util.getGreenTextFromString(false, true, false, index + ": " + name);
                text.setStyle(text.getStyle()
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + name))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(teleportPos.toString()).append("\n")
                                .append(I18Util.getGreenTextFromI18n(true, false, false,
                                        I18Util.getTranslationKey("message", "clickToTeleport"))
                                ))));
                player.displayClientMessage(text, false);
                index++;
            }
            player.displayClientMessage(new TextComponent(I18Util.getSeparator("=", 20)), false);
        });
        thread.start();
        return 1;
    }

    private static int listOthersHome(ServerPlayer source, ServerPlayer other) {
        AESPlayerData otherData = PlayerDataHandler.getInstance(other);
        Map<String, TeleportPos> otherHomes = otherData.getHomes();
        if (otherHomes.isEmpty()) {
            source.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "otherNoHome"), otherData.getName()), false);
            return Command.SINGLE_SUCCESS;
        }
        source.displayClientMessage(new TextComponent(I18Util.getSeparator("=", 20)), false);
        int index = 0;
        for (Map.Entry<String, TeleportPos> e : otherHomes.entrySet()) {
            TeleportPos teleportPos = e.getValue();
            MutableComponent text = I18Util.getGreenTextFromString(false, true, false, (index + 1) + ": " + e.getKey());
            text.setStyle(text.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + e.getKey()))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(teleportPos.toString()).append("\n")
                            .append(I18Util.getGreenTextFromI18n(true, false, false,
                                    I18Util.getTranslationKey("message", "clickToTeleport"))
                            ))));
            source.displayClientMessage(text, false);
            index++;
        }
        source.displayClientMessage(new TextComponent(I18Util.getSeparator("=", 20)), false);
        return 1;
    }
}
