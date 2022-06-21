package nova.committee.atom.ess.init.handler;

import cn.evolvefield.mods.atom.lib.utils.FileUtil;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import nova.committee.atom.ess.Static;

import java.io.File;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 11:20
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FileHandler {

    public static File ATOM_FOLDER;
    public static File MAIN_FOLDER;
    public static File PLAYER_DATA_FOLDER;
    public static File INFO_STORAGE_FOLDER;
    public static File WARPS_FILE;
    public static File STATISTICS_FILE;
    public static File Reward_FOLDER;

    @SubscribeEvent
    public static void onServerInit(ServerAboutToStartEvent event) {
        Static.SERVER = event.getServer();
        ATOM_FOLDER = FileUtil.checkFolder(FMLPaths.GAMEDIR.get().resolve("atom")).toFile();
        MAIN_FOLDER = FileUtil.checkFolder(new File(ATOM_FOLDER.getAbsolutePath() + "/" + "essentials"));
        Reward_FOLDER = FileUtil.checkFolder(new File(MAIN_FOLDER.getAbsolutePath() + "/" + "rewards"));
        init();
    }


    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save event) {
        init();
    }


    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        resetData();
    }


    public static void init() {
        if (MAIN_FOLDER == null) return;
        PLAYER_DATA_FOLDER = FileUtil.checkFolder(new File(MAIN_FOLDER.getAbsolutePath() + "/" + "playerData"));
        INFO_STORAGE_FOLDER = FileUtil.checkFolder(new File(MAIN_FOLDER.getAbsolutePath() + "/" + "infoRecorder"));
        WARPS_FILE = new File(MAIN_FOLDER.getAbsolutePath() + "/" + "warps.dat");
        STATISTICS_FILE = new File(MAIN_FOLDER.getAbsolutePath() + "/" + "statistics.dat");
    }


    public static void resetData() {
        PlayerDataHandler.PLAYER_DATA_LIST.clear();
        TeleportHandler.WARPS.clear();
        TpaHandler.TPA_REQUEST.clear();
        Static.LOGGER.debug("Successfully reset all data!");
    }


}
