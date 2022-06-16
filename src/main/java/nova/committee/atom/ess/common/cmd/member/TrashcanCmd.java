package nova.committee.atom.ess.common.cmd.member;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.api.common.config.ConfigField;
import nova.committee.atom.ess.core.model.AESPlayerData;
import nova.committee.atom.ess.init.handler.PlayerDataHandler;
import nova.committee.atom.ess.common.menu.TrashcanMenu;
import nova.committee.atom.ess.util.text.I18Util;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 17:24
 * Version: 1.0
 */
public class TrashcanCmd {
    @ConfigField
    public static boolean isTrashcanEnable = true;
    @ConfigField
    public static String trashcanAlias = "trashcan";
    @ConfigField
    public static int cleanTrashcanIntervalSeconds = 60;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(trashcanAlias)
                        .executes(context -> trashcan(context.getSource().getPlayerOrException()))
                        .requires(context -> Static.cmdPermission(context, "atomess.command.trashcan", false))
        );
    }


    private static int trashcan(ServerPlayer source) {
        AESPlayerData data = PlayerDataHandler.getInstance(source);
        TrashcanMenu.TrashcanData trashcan = data.getTrashcan();
        if (trashcan == null) {
            trashcan = new TrashcanMenu.TrashcanData();
            data.setTrashcan(trashcan);
        }
        NetworkHooks.openGui(source, new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return I18Util.getContainerNameTextFromI18n(false, false, false,
                        I18Util.getTranslationKey("text", "trashcan"));
            }

            @Override
            public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
                return TrashcanMenu.getServerSideInstance(id, playerInventory, data.getTrashcan());
            }
        });
        return 1;
    }
}
