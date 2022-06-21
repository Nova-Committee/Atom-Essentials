package nova.committee.atom.ess.common.net;

import cn.evolvefield.mods.atom.lib.common.net.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import nova.committee.atom.ess.core.model.AESPlayerData;
import nova.committee.atom.ess.init.handler.PlayerDataHandler;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 15:50
 * Version: 1.0
 */
public class ClearTrashPacket extends IPacket<ClearTrashPacket> {

    public ClearTrashPacket() {
    }

    @Override
    public ClearTrashPacket read(FriendlyByteBuf friendlyByteBuf) {
        return null;
    }

    @Override
    public void write(ClearTrashPacket clearTrashPacket, FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    public void run(ClearTrashPacket clearTrashPacket, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Optional<ServerPlayer> sender = Optional.ofNullable(context.get().getSender());
            sender.ifPresent(player -> {
                AESPlayerData data = PlayerDataHandler.getInstance(player);
                Optional.ofNullable(data.getTrashcan()).ifPresent(trashcan -> {
                    trashcan.clear();
                    player.containerMenu.broadcastChanges();
                });
            });
            context.get().setPacketHandled(true);
        });
    }


}
