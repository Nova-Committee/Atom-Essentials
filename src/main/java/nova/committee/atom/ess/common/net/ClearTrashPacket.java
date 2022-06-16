package nova.committee.atom.ess.common.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import nova.committee.atom.ess.api.common.net.IPacket;
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
public class ClearTrashPacket extends IPacket {

    public ClearTrashPacket(){}

    public ClearTrashPacket(FriendlyByteBuf buffer){
        super(buffer);
    }


    @Override
    public void encode(FriendlyByteBuf buffer) {

    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
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
