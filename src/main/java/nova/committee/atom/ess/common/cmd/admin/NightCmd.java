package nova.committee.atom.ess.common.cmd.admin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.util.text.I18Util;

import java.util.Objects;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/9 0:14
 * Version: 1.0
 */
public class NightCmd {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("night")
                        .requires(context -> Static.cmdPermission(context, "atomess.command.night", true))
                        .executes(context -> run(context.getSource().getPlayerOrException()))
        );
    }

    private static int run(ServerPlayer player){
        for (ServerLevel level : Objects.requireNonNull(player.getServer()).getAllLevels()) {
            level.setDayTime(20000);
        }

        player.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                I18Util.getTranslationKey("message", "setTimeNight")), false);
        return 1;
    }

}
