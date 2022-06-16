package nova.committee.atom.ess;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.util.Tristate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 8:57
 * Version: 1.0
 */
public class Static {
    public static final String MOD_ID = "atomess";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static MinecraftServer SERVER = ServerLifecycleHooks.getCurrentServer();

    public static boolean isCurios = false;
    public static boolean isLuckPerms = false;
    public static void sendMessageToAllPlayers(Component message, boolean actionBar) {
        new Thread(() -> Optional.ofNullable(SERVER).ifPresent(server -> server.getPlayerList().getPlayers()
                .forEach(player -> player.displayClientMessage(message, actionBar)))).start();
    }


    public static Boolean hasPermission(ServerPlayer playerEntity, String permission) throws CommandSyntaxException {
        AtomicReference<Boolean> exist = new AtomicReference<>(false);

        LuckPermsProvider.get().getUserManager().loadUser(playerEntity.getUUID())
                .thenApplyAsync(user -> {
                    CachedPermissionData permissionData = user.getCachedData()
                            .getPermissionData(user.getQueryOptions());
                    Tristate tristate = permissionData.checkPermission(permission);
                    if (tristate.equals(Tristate.UNDEFINED)) {
                        return false;
                    }

                    return tristate.asBoolean();
                }).thenAcceptAsync(aBoolean -> {
                    if (aBoolean)
                        exist.set(true);
                });

        return exist.get();
    }

    public static Boolean cmdPermission(CommandSourceStack source, String permission, boolean admin){
        if (!Static.isLuckPerms)
            if (admin)
                return source.hasPermission(2);
            else
                return true;
        else
            try {
                return Static.hasPermission(source.getPlayerOrException(), permission);
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
    }
}
