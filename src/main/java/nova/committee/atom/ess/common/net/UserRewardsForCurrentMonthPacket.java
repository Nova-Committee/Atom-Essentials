package nova.committee.atom.ess.common.net;

import cn.evolvefield.mods.atom.lib.common.net.IPacket;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import nova.committee.atom.ess.core.reward.ClientRewards;

import javax.json.Json;
import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/25 13:47
 * Version: 1.0
 */
public class UserRewardsForCurrentMonthPacket extends IPacket<UserRewardsForCurrentMonthPacket> {

    protected final CompoundTag data;
    protected final int rewardedDays;

    public UserRewardsForCurrentMonthPacket(CompoundTag data, int rewardedDays) {
        this.rewardedDays = rewardedDays;
        this.data = data;
    }

    public static void handlePacket(UserRewardsForCurrentMonthPacket message) {
        ClientRewards.setRewardedDaysForCurrentMonth(message.getRewardedDays());
        ClientRewards.setUserRewardsForCurrentMonth(message.getData());
    }

    public CompoundTag getData() {
        return this.data;
    }

    public int getRewardedDays() {
        return this.rewardedDays;
    }

    @Override
    public UserRewardsForCurrentMonthPacket read(FriendlyByteBuf friendlyByteBuf) {
        return null;
    }

    @Override
    public void write(UserRewardsForCurrentMonthPacket generalRewardsForCurrentMonthPacket, FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public void run(UserRewardsForCurrentMonthPacket generalRewardsForCurrentMonthPacket, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(
                () -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handlePacket(generalRewardsForCurrentMonthPacket)));
        context.setPacketHandled(true);
    }
}
