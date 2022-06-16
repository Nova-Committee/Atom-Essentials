package nova.committee.atom.ess.common.cmd.admin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.network.NetworkHooks;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.api.common.config.ConfigField;
import nova.committee.atom.ess.common.menu.OthersInvMenu;
import nova.committee.atom.ess.util.text.I18Util;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 17:58
 * Version: 1.0
 */
public class OpenInvCmd {

    @ConfigField
    public static boolean isOpenInvEnable = true;
    @ConfigField
    public static String invseeAlias = "invsee";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(invseeAlias)
                        .then(Commands.argument("Target", EntityArgument.player())

                                .executes(OpenInvCmd::invSee)
                        )
                        .requires(source -> Static.cmdPermission(source, "atomess.command.invsee", true))
        );
    }

    private static int invSee(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerPlayer requestedPlayer = getRequestedPlayer(context);


            if (isProtected(requestedPlayer)) {
                context.getSource().sendFailure(new TextComponent("Requested inventory is protected"));
            } else {
                NetworkHooks.openGui(player, new MenuProvider() {
                    @Override
                    public @NotNull Component getDisplayName() {
                        return I18Util.getContainerNameTextFromI18n(false, false, false,
                                I18Util.getTranslationKey("text", "playerInv"), requestedPlayer.getGameProfile().getName());
                    }

                    @Override
                    public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory sourceInv, @NotNull Player source) {
                        return OthersInvMenu.getServerSideInstance(id, sourceInv, requestedPlayer.getInventory());
                    }
                });
            }


        return 1;
    }


    private static ServerPlayer getRequestedPlayer(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        GameProfile requestedProfile = GameProfileArgument.getGameProfiles(context, "target").iterator().next();
        ServerPlayer requestedPlayer = Static.SERVER.getPlayerList().getPlayerByName(requestedProfile.getName());

        if (requestedPlayer == null) {
            requestedPlayer = Static.SERVER.getPlayerList().getPlayerForLogin(requestedProfile);
            CompoundTag compound = Static.SERVER.getPlayerList().load(requestedPlayer);
            if (compound != null) {
                ServerLevel world = Static.SERVER.getLevel(
                        DimensionType.parseLegacy(new Dynamic<>(NbtOps.INSTANCE, compound.get("Dimension")))
                                .result().orElseThrow());

                if (world != null) {
                    requestedPlayer.setLevel(world);
                }
            }
        }

        return requestedPlayer;
    }

    //检测目标是否有保护标签
    private static Boolean isProtected(ServerPlayer playerEntity) throws CommandSyntaxException {
        if (!Static.isLuckPerms) return false;
        return Static.hasPermission(playerEntity, "atomess.inv.protected");
    }

}
