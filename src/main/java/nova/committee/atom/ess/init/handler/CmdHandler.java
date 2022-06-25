package nova.committee.atom.ess.init.handler;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.atom.ess.common.cmd.admin.*;
import nova.committee.atom.ess.common.cmd.member.BanItemListCmd;
import nova.committee.atom.ess.common.cmd.member.SignCmd;
import nova.committee.atom.ess.common.cmd.member.TrashcanCmd;
import nova.committee.atom.ess.common.cmd.member.HatCmd;
import nova.committee.atom.ess.common.cmd.teleport.*;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 16:15
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CmdHandler {

    @SubscribeEvent
    public static void registryCmd(RegisterCommandsEvent event) {
        final var dispatcher = event.getDispatcher();

        BackCmd.register(dispatcher);

        RtpCmd.register(dispatcher);

        SpawnCmd.register(dispatcher);

        TrashcanCmd.register(dispatcher);

        WarpCmd.register(dispatcher);

        HomeCmd.register(dispatcher);

        TpaCmd.register(dispatcher);

        OpenInvCmd.register(dispatcher);

        FlyCmd.register(dispatcher);

        CleanCommand.register(dispatcher);

        DayCmd.register(dispatcher);

        NightCmd.register(dispatcher);

        BanItemListCmd.register(dispatcher);

        BanItemCmd.register(dispatcher);

        UnBanItemCmd.register(dispatcher);

        HatCmd.register(dispatcher);

        SignCmd.register(dispatcher);
    }
}
