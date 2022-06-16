package nova.committee.atom.ess.init.handler;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.atom.ess.common.cmd.admin.*;
import nova.committee.atom.ess.common.cmd.member.TrashcanCmd;
import nova.committee.atom.ess.common.cmd.member.banItem.BanItemCmd;
import nova.committee.atom.ess.common.cmd.member.banItem.BanItemListCmd;
import nova.committee.atom.ess.common.cmd.member.banItem.UnBanItemCmd;
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
    public static void registryCmd(RegisterCommandsEvent event){
        BackCmd.register(event.getDispatcher());

        RtpCmd.register(event.getDispatcher());

        SpawnCmd.register(event.getDispatcher());

        TrashcanCmd.register(event.getDispatcher());

        WarpCmd.register(event.getDispatcher());

        HomeCmd.register(event.getDispatcher());

        TpaCmd.register(event.getDispatcher());

        OpenInvCmd.register(event.getDispatcher());

        FlyCmd.register(event.getDispatcher());

        CleanCommand.register(event.getDispatcher());

        DayCmd.register(event.getDispatcher());

        NightCmd.register(event.getDispatcher());

        BanItemListCmd.register(event.getDispatcher());

        BanItemCmd.register(event.getDispatcher());

        UnBanItemCmd.register(event.getDispatcher());

    }
}
