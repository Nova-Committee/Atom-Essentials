package nova.committee.atom.ess.common.cmd.teleport;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.api.common.config.ConfigField;
import nova.committee.atom.ess.core.model.AESPlayerData;
import nova.committee.atom.ess.core.model.TeleportPos;
import nova.committee.atom.ess.init.handler.PlayerDataHandler;
import nova.committee.atom.ess.util.TeleportUtil;
import nova.committee.atom.ess.util.text.I18Util;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 16:49
 * Version: 1.0
 */
public class SpawnCmd{

    @ConfigField
    public static boolean isSpawnEnable = true;
    @ConfigField
    public static String spawnAlias = "spawn";
    @ConfigField
    public static int spawnCooldownSeconds = 3;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(spawnAlias)
                .executes(SpawnCmd::execute)
                .requires(context -> Static.cmdPermission(context, "atomess.command.spawn", false))
        ) ;
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        AESPlayerData data = PlayerDataHandler.getInstance(player);

        if (TeleportUtil.isInCooldown(player, data.getLastSpawnTime(), spawnCooldownSeconds)) {
            return 1;
        }
        MinecraftServer server = player.getServer();
        if (server != null) {
            ServerLevel world = server.getLevel(Level.OVERWORLD);
            if (world != null) {
                BlockPos spawnPoint = world.getSharedSpawnPos();
                data.addTeleportHistory(new TeleportPos(player.getLevel().dimension(), player.getOnPos()));
                TeleportUtil.teleport(player, world, spawnPoint);
                data.setLastSpawnTime(System.currentTimeMillis());
                player.displayClientMessage(I18Util.getGreenTextFromI18n(false ,false, false,
                        I18Util.getTranslationKey("message", "spawnSuccess")), true);
            }
        }
        return 1;
    }
}
