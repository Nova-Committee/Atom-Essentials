//package nova.committee.atomess.common.cmd.admin;
//
//import com.mojang.brigadier.CommandDispatcher;
//import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.commands.Commands;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.level.dimension.DimensionType;
//
///**
// * Description:
// * Author: cnlimiter
// * Date: 2022/4/14 13:45
// * Version: 1.0
// */
//public class MvCmd {
//
//    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
//        dispatcher.register(Commands.literal("mw")
//                .requires(source -> source.hasPermission(2))
//                .executes()
//        );
//    }
//
//    private static int run(ServerPlayer player, DimensionType dimensionType, ) {
//
//    }
//
//}
