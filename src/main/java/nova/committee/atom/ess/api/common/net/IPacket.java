package nova.committee.atom.ess.api.common.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 15:49
 * Version: 1.0
 */
public abstract class IPacket {

    public IPacket(FriendlyByteBuf buffer) {}

    public IPacket() {}

    public abstract void encode(FriendlyByteBuf buffer);

    public abstract void handle(Supplier<NetworkEvent.Context> context);
}
