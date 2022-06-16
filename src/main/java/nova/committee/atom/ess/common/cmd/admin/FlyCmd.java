package nova.committee.atom.ess.common.cmd.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.core.model.AESPlayerData;
import nova.committee.atom.ess.init.handler.PlayerDataHandler;
import nova.committee.atom.ess.api.common.config.ConfigField;
import nova.committee.atom.ess.util.text.I18Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 18:06
 * Version: 1.0
 */
public class FlyCmd {
    @ConfigField
    public static boolean isFlyEnable = true;
    @ConfigField
    public static String flyAlias = "fly";
    @ConfigField
    public static String datePattern = "hh:mm:ss MM/dd/yyyy";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(flyAlias)
                        .then(Commands.argument("Target", EntityArgument.player())
                                .requires(context -> Static.cmdPermission(context, "atomess.command.fly.target", true))
                                .executes(context -> fly(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "Target"), FlyType.PERMANENT))
                                .then(Commands.argument("Minutes", IntegerArgumentType.integer())
                                        .requires(context -> Static.cmdPermission(context, "atomess.command.fly.minutes", true))
                                        .executes(context -> fly(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "Target"), FlyType.TEMPORARY, IntegerArgumentType.getInteger(context, "Minutes")))
                                )
                        )
                        .requires(context -> Static.cmdPermission(context, "atomess.command.fly", true))
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            return fly(player, player, FlyType.PERMANENT);
                        })
        );
    }

    private static int fly(ServerPlayer source, ServerPlayer target, FlyType type, int... minutes) {
        AESPlayerData data = PlayerDataHandler.getInstance(target);
        if (target.isCreative()) {
            source.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "cantSetFly"), data.getName()), false);
            return 1;
        } else if (data.isFlyable()) {
            data.setFlyable(false);
            source.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                    I18Util.getTranslationKey("message", "ok")), false);
            target.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "cantFlyNow")), false);
            return 1;
        } else {
            data.setFlyable(true);
        }
        switch (type) {
            case PERMANENT -> {
                data.setCanFlyUntil(-1L);
                if (!source.equals(target)) {
                    source.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                            I18Util.getTranslationKey("message", "flyPermanentlySource"), data.getName()), false);
                }
                target.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                        I18Util.getTranslationKey("message", "flyPermanentlyTarget")), false);
            }
            case TEMPORARY -> {
                long canFlyUntil = System.currentTimeMillis() + minutes[0] * 60 * 1000L;
                data.setCanFlyUntil(canFlyUntil);
                Date date = new Date(canFlyUntil);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
                String formattedDate = simpleDateFormat.format(date);
                if (!source.equals(target)) {
                    target.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                            I18Util.getTranslationKey("message", "flyTempTarget"), formattedDate), false);
                }
                source.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                        I18Util.getTranslationKey("message", "flyTempSource"), data.getName(), formattedDate), false);
            }
        }
        return 1;
    }

    private enum FlyType {
        TEMPORARY, PERMANENT
    }
}
