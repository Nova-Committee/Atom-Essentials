package nova.committee.atom.ess.common.net;

import cn.evolvefield.mods.atom.lib.common.net.IPacket;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import nova.committee.atom.ess.core.reward.ClientRewards;

import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/25 13:42
 * Version: 1.0
 */
public class GeneralRewardsForCurrentMonthPacket extends IPacket<GeneralRewardsForCurrentMonthPacket> {
    protected final CompoundTag data;

    public GeneralRewardsForCurrentMonthPacket(CompoundTag data) {
        this.data = data;
    }

    public static void handlePacket(GeneralRewardsForCurrentMonthPacket message) {
        ClientRewards.setGeneralRewardsForCurrentMonth(message.getData());
    }

    public CompoundTag getData() {
        return this.data;
    }

    @Override
    public GeneralRewardsForCurrentMonthPacket read(FriendlyByteBuf friendlyByteBuf) {
        return null;
    }

    @Override
    public void write(GeneralRewardsForCurrentMonthPacket generalRewardsForCurrentMonthPacket, FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public void run(GeneralRewardsForCurrentMonthPacket generalRewardsForCurrentMonthPacket, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(
                () -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handlePacket(generalRewardsForCurrentMonthPacket)));
        context.setPacketHandled(true);
    }
}
