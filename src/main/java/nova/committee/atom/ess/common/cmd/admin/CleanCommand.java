package nova.committee.atom.ess.common.cmd.admin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.core.model.AESItemEntity;
import nova.committee.atom.ess.core.model.AESMobEntity;
import nova.committee.atom.ess.init.handler.CleanerHandler;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 18:08
 * Version: 1.0
 */
public class CleanCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(Static.MOD_ID)
                        .then(
                                Commands.literal("clean")
                                        .then(
                                                Commands.literal("items")
                                                        .executes(context -> CleanerHandler.cleanupEntity(Static.SERVER.getAllLevels(), entity -> entity instanceof ItemEntity,
                                                                entity -> new AESItemEntity((ItemEntity) entity).filtrate()))
                                        )
                                        .then(
                                                Commands.literal("monsters")
                                                        .executes(context -> CleanerHandler.cleanupEntity(Static.SERVER.getAllLevels(), entity -> entity instanceof Monster,
                                                                entity -> new AESMobEntity((Mob) entity).filtrate()))
                                        )
                                        .then(
                                                Commands.literal("animals")
                                                        .executes(context -> CleanerHandler.cleanupEntity(Static.SERVER.getAllLevels(), entity -> (entity instanceof Mob) && !(entity instanceof Monster),
                                                                entity -> new AESMobEntity((Mob) entity).filtrate()))
                                        )
                                        .then(
                                                Commands.literal("others")
                                                        .executes(context -> CleanerHandler.cleanOtherEntities(Static.SERVER.getAllLevels()))
                                        )
                                        .requires(context -> Static.cmdPermission(context, "atomess.command.clean", true))
                        )
        );
    }
}
