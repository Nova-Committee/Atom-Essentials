package nova.committee.atom.ess.init.handler;

import cn.evolvefield.mods.atom.lib.init.handler.NetBaseHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.common.net.ClearTrashPacket;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 20:41
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkHandler {

    public static final NetBaseHandler INSTANCE = new NetBaseHandler(new ResourceLocation(Static.MOD_ID, "main"));

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            INSTANCE.register(ClearTrashPacket.class, new ClearTrashPacket());
        });

    }



}
