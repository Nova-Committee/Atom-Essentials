package nova.committee.atom.ess.common.cmd.teleport;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.LavaFluid;
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
 * Date: 2022/4/8 15:56
 * Version: 1.0
 */
public class BackCmd{

    @ConfigField
    public static boolean isBackEnable = true;
    @ConfigField
    public static String backAlias = "back";
    @ConfigField
    public static int backCooldownSeconds;
    @ConfigField
    public static int maxBacks = 10;


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(backAlias)
                .executes(BackCmd::execute)
                .requires(context -> Static.cmdPermission(context, "atomess.command.tp.back", false))
        );
    }


    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        AESPlayerData data = PlayerDataHandler.getInstance(player);
        if (TeleportUtil.isInCooldown(player, data.getLastBackTime(), backCooldownSeconds)) {
            return 1;
        }
        TeleportPos teleportPos = data.getTeleportHistory();
        if (teleportPos == null) {
            player.displayClientMessage(I18Util.getYellowTextFromI18n(true, false, false,
                    I18Util.getTranslationKey("message", "noBack")), false);
            return 1;
        }
        // 安全传送，放置调入岩浆，但是不能在虚空中保护你
        BlockPos.MutableBlockPos pos = teleportPos.getPos().mutable();
        ServerLevel world = Static.SERVER.getLevel(teleportPos.getDimension());
        if (world != null && world.getFluidState(pos).getType() instanceof  LavaFluid) {
            // 将脚底下的岩浆替换成圆石
            while (world.getFluidState(pos).getType() instanceof  LavaFluid) {
                pos = pos.move(0, 1, 0);
            }
            world.setBlockAndUpdate(pos, Blocks.COBBLESTONE.defaultBlockState());
            teleportPos.setPos(pos.above());
        }
        TeleportUtil.teleport(player, teleportPos);
        data.setLastBackTime(System.currentTimeMillis());
        player.displayClientMessage(I18Util.getGreenTextFromI18n(false, false, false,
                I18Util.getTranslationKey("message", "backSuccess")), true);
        data.moveCurrentBackIndex();
        return 1;
    }
}
