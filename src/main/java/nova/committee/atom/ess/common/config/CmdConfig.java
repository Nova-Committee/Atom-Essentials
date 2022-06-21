package nova.committee.atom.ess.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import nova.committee.atom.ess.api.common.config.AbstractModConfig;
import nova.committee.atom.ess.common.cmd.admin.FlyCmd;
import nova.committee.atom.ess.common.cmd.admin.OpenInvCmd;
import nova.committee.atom.ess.common.cmd.member.TrashcanCmd;
import nova.committee.atom.ess.common.cmd.misc.HatCmd;
import nova.committee.atom.ess.common.cmd.teleport.*;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 21:14
 * Version: 1.0
 */
public class CmdConfig extends AbstractModConfig {


    // Spawn
    private ForgeConfigSpec.BooleanValue isSpawnEnableConfig;
    private ForgeConfigSpec.ConfigValue<? extends String> spawnAlias;
    private ForgeConfigSpec.IntValue spawnCooldownSecondsConfig;

    // Home
    private ForgeConfigSpec.BooleanValue isHomeEnableConfig;
    private ForgeConfigSpec.ConfigValue<? extends String>
            setHomeAlias,
            homeAlias,
            homeOtherAlias,
            delHomeAlias,
            listHomesAlias,
            listOtherHomesAlias,
            delOtherHomeAlias;
    private ForgeConfigSpec.IntValue homeCooldownSecondsConfig, homeOtherCooldownSecondsConfig;
    private ForgeConfigSpec.IntValue maxHomesConfig;

    // Back
    private ForgeConfigSpec.BooleanValue isBackEnableConfig;
    private ForgeConfigSpec.ConfigValue<? extends String> backAlias;
    private ForgeConfigSpec.IntValue backCooldownSecondsConfig;
    private ForgeConfigSpec.IntValue maxBacksConfig;

    // Warp
    private ForgeConfigSpec.BooleanValue isWarpEnableConfig;
    private ForgeConfigSpec.ConfigValue<? extends String>
            setWarpAlias,
            warpAlias,
            listWarpsAlias,
            delWarpAlias;
    private ForgeConfigSpec.IntValue warpCooldownSecondsConfig;

    // TPA
    private ForgeConfigSpec.BooleanValue isTPAEnableConfig;
    private ForgeConfigSpec.ConfigValue<? extends String>
            tpaAlias,
            tpaHereAlias,
            tpHereAlias,
            tpAllHereAlias;
    private ForgeConfigSpec.IntValue tpaCooldownSecondsConfig;
    private ForgeConfigSpec.IntValue maxTPARequestTimeoutSecondsConfig;

    // RTP
    private ForgeConfigSpec.BooleanValue isRTPEnableConfig;
    private ForgeConfigSpec.ConfigValue<? extends String> rtpAlias;
    private ForgeConfigSpec.IntValue rtpCooldownSecondsConfig;
    private ForgeConfigSpec.IntValue maxRTPAttemptsConfig;
    private ForgeConfigSpec.IntValue
            minRTPHeightDefaultConfig,
            maxRTPHeightDefaultConfig,
            minRTPRadiusDefaultConfig,
            maxRTPRadiusDefaultConfig,
            minRTPHeightOverworldConfig,
            maxRTPHeightOverworldConfig,
            minRTPRadiusOverworldConfig,
            maxRTPRadiusOverworldConfig,
            minRTPHeightNetherConfig,
            maxRTPHeightNetherConfig,
            minRTPRadiusNetherConfig,
            maxRTPRadiusNetherConfig,
            minRTPHeightEndConfig,
            maxRTPHeightEndConfig,
            minRTPRadiusEndConfig,
            maxRTPRadiusEndConfig;

    // fly
    private ForgeConfigSpec.BooleanValue isFlyEnable;
    private ForgeConfigSpec.ConfigValue<? extends String> flyAlias;
    private ForgeConfigSpec.ConfigValue<? extends String> datePattern;

    // atomess getRegistryName mob
    private ForgeConfigSpec.IntValue entitiesWithinRadius;

    // invsee
    private ForgeConfigSpec.BooleanValue isOpenInvEnable;
    private ForgeConfigSpec.ConfigValue<? extends String> invseeAlias;

    // hat
    private ForgeConfigSpec.BooleanValue isHatEnable;
    private ForgeConfigSpec.ConfigValue<? extends String> hatAlias;

    // trashcan
    private ForgeConfigSpec.BooleanValue isTrashcanEnable;
    private ForgeConfigSpec.ConfigValue<? extends String> trashcanAlias;
    private ForgeConfigSpec.IntValue cleanTrashcanIntervalSeconds;

    // Rank
    private ForgeConfigSpec.BooleanValue isRankEnable;
    private ForgeConfigSpec.ConfigValue<? extends String> rankAlias;


    public CmdConfig(ForgeConfigSpec.Builder builder) {
        super(builder);
    }
    @Override
    public void init() {
        this.builder.push("Commands");

        this.builder.push("Spawn");
        isSpawnEnableConfig = this.builder
                .comment("Set it to false to disable /spawn command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsSpawnEnable", true);
        spawnAlias = this.builder
                .comment("How to trigger command spawn. If you set it to \"sp\", you will need to use /sp to back to the spawn point.",
                        "Default value: spawn",
                        "Do not add \"/\"!")
                .define("SpawnAlias", "spawn", CmdConfig::isValidCommandAlias);
        spawnCooldownSecondsConfig = this.builder
                .comment("The time interval between two /spawn commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("SpawnCooldown", 3, 0, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("Back");
        isBackEnableConfig = this.builder
                .comment("Set it to false to disable /back command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsBackEnable", true);
        backAlias = this.builder
                .comment("How to trigger command back. If you set it to \"bk\", you will need to use /bk to back.",
                        "Default value: back",
                        "Do not add \"/\"!")
                .define("BackAlias", "back", CmdConfig::isValidCommandAlias);
        backCooldownSecondsConfig = this.builder
                .comment("The time interval between two /back commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("BackCooldown", 3, 0, Integer.MAX_VALUE);
        maxBacksConfig = this.builder
                .comment("Max amount of times of /back can use to go back to certain locations. \nDefault value: 10 Times")
                .defineInRange("MaxBacks", 10, 1, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("Home");
        isHomeEnableConfig = this.builder
                .comment("Set it to false to disable /home, /sethome, /delhome, /listhomes, /homeother, and /listotherhomes command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsHomeEnable", true);
        homeAlias = this.builder
                .comment("How to trigger command home.",
                        "Default value: home",
                        "Do not add \"/\"!")
                .define("HomeAlias", "home", CmdConfig::isValidCommandAlias);
        setHomeAlias = this.builder
                .comment("How to trigger command to set a home.",
                        "Default value: sethome",
                        "Do not add \"/\"!")
                .define("SetHomeAlias", "sethome", CmdConfig::isValidCommandAlias);
        delHomeAlias = this.builder
                .comment("How to trigger command to delete a home.",
                        "Default value: delhome",
                        "Do not add \"/\"!")
                .define("DelHomeAlias", "delhome", CmdConfig::isValidCommandAlias);
        listHomesAlias = this.builder
                .comment("How to trigger command to list all your homes.",
                        "Default value: listhomes",
                        "Do not add \"/\"!")
                .define("ListHomesAlias", "listhomes", CmdConfig::isValidCommandAlias);
        homeOtherAlias = this.builder
                .comment("How to trigger command to teleport to other's home.",
                        "Default value: homeother",
                        "Do not add \"/\"!")
                .define("HomeOtherAlias", "homeother", CmdConfig::isValidCommandAlias);
        delOtherHomeAlias = this.builder
                .comment("How to trigger command to delete other's home.",
                        "Default value: delotherhome",
                        "Do not add \"/\"!")
                .define("DelOtherHomeAlias", "delotherhome", CmdConfig::isValidCommandAlias);
        listOtherHomesAlias = this.builder
                .comment("How to trigger command to list all someone's homes.",
                        "Default value: listotherhomes",
                        "Do not add \"/\"!")
                .define("ListOtherHomesAlias", "listotherhomes", CmdConfig::isValidCommandAlias);
        homeCooldownSecondsConfig = this.builder
                .comment("The time interval between two /home commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("HomeCooldown", 3, 0, Integer.MAX_VALUE);
        homeOtherCooldownSecondsConfig = this.builder
                .comment("The time interval between two /homeother commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("HomeOtherCooldown", 3, 0, Integer.MAX_VALUE);
        maxHomesConfig = this.builder
                .comment("The max amount of homes that a player can set.\nDefault value: 5 homes")
                .defineInRange("MaxHomes", 5, 1, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("Warp");
        isWarpEnableConfig = this.builder
                .comment("Set it to false to disable /warp, /setwarp, /delwarp, and /listwarps command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsWarpEnable", true);
        warpAlias = this.builder
                .comment("How to trigger command to teleport to a warp.",
                        "Default value: warp",
                        "Do not add \"/\"!")
                .define("WarpAlias", "warp", CmdConfig::isValidCommandAlias);
        setWarpAlias = this.builder
                .comment("How to trigger command to set a warp.",
                        "Default value: setwarp",
                        "Do not add \"/\"!")
                .define("SetWarpAlias", "setwarp", CmdConfig::isValidCommandAlias);
        delWarpAlias = this.builder
                .comment("How to trigger command to delete a warp.",
                        "Default value: delwarp",
                        "Do not add \"/\"!")
                .define("DelWarpAlias", "delwarp", CmdConfig::isValidCommandAlias);
        listWarpsAlias = this.builder
                .comment("How to trigger command to list all warps.",
                        "Default value: listwarps",
                        "Do not add \"/\"!")
                .define("ListWarpsAlias", "listwarps", CmdConfig::isValidCommandAlias);
        warpCooldownSecondsConfig = this.builder
                .comment("The time interval between two /warp commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("WarpCooldown", 3, 0, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("TPA");
        isTPAEnableConfig = this.builder
                .comment("Set it to false to disable /tpa, /tpahere, /tpaaccept, /tpadeny, /tphere, and /tpallhere command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsTPAEnable", true);
        tpaAlias = this.builder
                .comment("How to trigger command to tpa.",
                        "Default value: tpa",
                        "Do not add \"/\"!")
                .define("TPAAlias", "tpa", CmdConfig::isValidCommandAlias);
        tpaHereAlias = this.builder
                .comment("How to trigger command to tpahere.",
                        "Default value: tpahere",
                        "Do not add \"/\"!")
                .define("TPAHereAlias", "tpahere", CmdConfig::isValidCommandAlias);
        tpHereAlias = this.builder
                .comment("How to trigger command to teleport player to your position.",
                        "Default value: tphere",
                        "Do not add \"/\"!")
                .define("TPHereAlias", "tphere", CmdConfig::isValidCommandAlias);
        tpAllHereAlias = this.builder
                .comment("How to trigger command to teleport all players to your position.",
                        "Default value: tpallhere",
                        "Do not add \"/\"!")
                .define("TPAllHereAlias", "tpallhere", CmdConfig::isValidCommandAlias);
        tpaCooldownSecondsConfig = this.builder
                .comment("The time interval between two /tpa and /tpahere commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("TPACooldown", 3, 0, Integer.MAX_VALUE);
        maxTPARequestTimeoutSecondsConfig = this.builder
                .comment("If a tpa request last more than this seconds, that tpa request will be considered expired. \nDefault value: 60 seconds")
                .defineInRange("MaxTPARequestExpireTime", 60, 1, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("RTP");
        isRTPEnableConfig = this.builder
                .comment("Set it to false to disable /rtp command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsRTPEnable", true);
        rtpAlias = this.builder
                .comment("How to trigger command to randomly teleport to a safe location within the world.",
                        "Default value: rtp",
                        "Do not add \"/\"!")
                .define("RTPAlias", "rtp", CmdConfig::isValidCommandAlias);
        rtpCooldownSecondsConfig = this.builder
                .comment("The time interval between two /rtp commands, or teleport cooldown, in seconds.\nDefault value: 10 seconds")
                .defineInRange("RTPCooldown", 10, 0, Integer.MAX_VALUE);
        maxRTPAttemptsConfig = this.builder
                .comment("Max attempts for /rtp to find a safe landing site.\nDefault value: 10 Attempts")
                .defineInRange("MaxRTPAttempts", 10, 1, Integer.MAX_VALUE);
        this.builder.push("OverworldSettings");
        minRTPHeightOverworldConfig = this.builder
                .comment("The min height in overworld that the /rtp commands could reach.\nDefault value: 1 Blocks")
                .defineInRange("OverworldMinHeight", 1, 0, 256);
        maxRTPHeightOverworldConfig = this.builder
                .comment("The max height in overworld that the /rtp commands could reach.\nDefault value: 150 Blocks")
                .defineInRange("OverworldMaxHeight", 150, 0, 256);
        minRTPRadiusOverworldConfig = this.builder
                .comment("The min radius (centered on player) in overworld that the /rtp commands could reach.\nDefault value: 1000 Blocks")
                .defineInRange("OverworldMinRadius", 1000, 0, Integer.MAX_VALUE);
        maxRTPRadiusOverworldConfig = this.builder
                .comment("The max radius (centered on player) in overworld that the /rtp commands could reach.\nDefault value: 10000 Blocks")
                .defineInRange("OverworldMaxRadius", 10000, 0, Integer.MAX_VALUE);
        this.builder.pop();
        this.builder.push("TheNetherSettings");
        minRTPHeightNetherConfig = this.builder
                .comment("The min height in the nether that the /rtp commands could reach.\nDefault value: 30 Blocks")
                .defineInRange("TheNetherMinHeight", 30, 0, 128);
        maxRTPHeightNetherConfig = this.builder
                .comment("The max height in the nether that the /rtp commands could reach.\nDefault value: 100 Blocks")
                .defineInRange("TheNetherMaxHeight", 100, 0, 128);
        minRTPRadiusNetherConfig = this.builder
                .comment("The min radius (centered on player) in the nether that the /rtp commands could reach.\nDefault value: 1000 Blocks")
                .defineInRange("TheNetherMinRadius", 1000, 0, Integer.MAX_VALUE);
        maxRTPRadiusNetherConfig = this.builder
                .comment("The max radius (centered on player) in the nether that the /rtp commands could reach.\nDefault value: 10000 Blocks")
                .defineInRange("TheNetherMaxRadius", 10000, 0, Integer.MAX_VALUE);
        this.builder.pop();
        this.builder.push("TheEndSettings");
        minRTPHeightEndConfig = this.builder
                .comment("The min height in the end that the /rtp commands could reach.\nDefault value: 40 Blocks")
                .defineInRange("TheEndMinHeight", 40, 0, 256);
        maxRTPHeightEndConfig = this.builder
                .comment("The max height in the end that the /rtp commands could reach.\nDefault value: 140 Blocks")
                .defineInRange("TheEndMaxHeight", 140, 0, 256);
        minRTPRadiusEndConfig = this.builder
                .comment("The min radius (centered on player) in the end that the /rtp commands could reach.\nDefault value: 1000 Blocks")
                .defineInRange("TheEndMinRadius", 1000, 0, Integer.MAX_VALUE);
        maxRTPRadiusEndConfig = this.builder
                .comment("The max radius (centered on player) in the end that the /rtp commands could reach.\nDefault value: 10000 Blocks")
                .defineInRange("TheEndMaxRadius", 10000, 0, Integer.MAX_VALUE);
        this.builder.pop();
        this.builder.push("DefaultSettings");
        minRTPHeightDefaultConfig = this.builder
                .comment("The min height in any other world that the /rtp commands could reach.\nDefault value: 40 Blocks")
                .defineInRange("DefaultMinHeight", 40, Integer.MIN_VALUE, Integer.MAX_VALUE);
        maxRTPHeightDefaultConfig = this.builder
                .comment("The max height in any other world that the /rtp commands could reach.\nDefault value: 120 Blocks")
                .defineInRange("DefaultMaxHeight", 120, Integer.MIN_VALUE, Integer.MAX_VALUE);
        minRTPRadiusDefaultConfig = this.builder
                .comment("The min radius (centered on player) in any other world that the /rtp commands could reach.\nDefault value: 1000 Blocks")
                .defineInRange("DefaultMinRadius", 1000, Integer.MIN_VALUE, Integer.MAX_VALUE);
        maxRTPRadiusDefaultConfig = this.builder
                .comment("The max radius (centered on player) in any other world that the /rtp commands could reach.\nDefault value: 10000 Blocks")
                .defineInRange("DefaultMaxRadius", 10000, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.builder.pop();
        this.builder.pop();

        this.builder.push("Fly");
        isFlyEnable = this.builder
                .comment("Set it to false to disable /fly command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsFlyEnable", true);
        flyAlias = this.builder
                .comment("How to trigger command to let a player flyable.",
                        "Default value: fly",
                        "Do not add \"/\"!")
                .define("FlyAlias", "fly", CmdConfig::isValidCommandAlias);
        datePattern = this.builder
                .comment("The date format used to display the deadline of flying.",
                        "A valid date format should follow the pattern described in JavaDoc: https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html",
                        "If you don't know what it is, please do not modify it.",
                        "Default value: \"hh:mm:ss MM/dd/yyyy\"")
                .define("DatePattern", "hh:mm:ss MM/dd/yyyy");
        this.builder.pop();

        this.builder.push("Hat");
        isHatEnable = this.builder
                .comment("Set it to false to disable /hat command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsHatEnable", true);
        hatAlias = this.builder
                .comment("How to trigger command hat.",
                        "Default value: hat",
                        "Do not add \"/\"!")
                .define("HatAlias", "hat", CmdConfig::isValidCommandAlias);
        this.builder.pop();

        this.builder.push("Trashcan");
        isTrashcanEnable = this.builder
                .comment("Set it to false to disable /trashcan command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("isTrashcanEnable", true);
        trashcanAlias = this.builder
                .comment("How to trigger command to open trashcan.",
                        "Default value: trashcan",
                        "Do not add \"/\"!")
                .define("TrashcanAlias", "trashcan", CmdConfig::isValidCommandAlias);
        cleanTrashcanIntervalSeconds = this.builder
                .comment("The interval between two actions of deleting items in the trashcan.",
                        "There is also a button available in the trashcan gui to clear the items.",
                        "Default value: 60 seconds.")
                .defineInRange("CleanTrashInterval", 60, 1, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("Invsee");
        isOpenInvEnable = this.builder
                .comment("Set it to false to disable /invsee (/openinv maybe in the future) command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("isInvseeEnable", true);
        invseeAlias = this.builder
                .comment("How to trigger command to open someone's inventory.",
                        "Default value: invsee",
                        "Do not add \"/\"!")
                .define("InvseeAlias", "invsee", CmdConfig::isValidCommandAlias);
        this.builder.pop();

        this.builder.push("Rank");
        isRankEnable = this.builder
                .comment("Set it to false to disable /rank command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("isRankEnable", true);
        rankAlias = this.builder
                .comment("How to trigger command to open rank gui.",
                        "Default value: rank",
                        "Do not add \"/\"!")
                .define("RankAlias", "rank", CmdConfig::isValidCommandAlias);
        this.builder.pop();

        // /essential ....
        this.builder.push("atomess");
        this.builder.push("GetRegistryName");
        // getRegistryName
        entitiesWithinRadius = this.builder
                .comment("The searching radius of command /atomess getRegistryName mob to get the registry names of nearby mobs in certain radius",
                        "The radius is specified here.",
                        "Default value: 3 blocks (a 7 * 7 * 7 cube)")
                .defineInRange("Radius", 3, 1, Integer.MAX_VALUE);
        this.builder.pop();
        this.builder.pop();

        this.builder.pop();
    }

    @Override
    public void get() {
        // Spawn
        SpawnCmd.isSpawnEnable = isSpawnEnableConfig.get();
        SpawnCmd.spawnAlias = spawnAlias.get();
        SpawnCmd.spawnCooldownSeconds = spawnCooldownSecondsConfig.get();

        // Back
        BackCmd.isBackEnable = isBackEnableConfig.get();
        BackCmd.backAlias = backAlias.get();
        BackCmd.backCooldownSeconds = backCooldownSecondsConfig.get();
        BackCmd.maxBacks = maxBacksConfig.get();

        // Home
        HomeCmd.isHomeEnable = isHomeEnableConfig.get();
        HomeCmd.setHomeAlias = setHomeAlias.get();
        HomeCmd.homeAlias = homeAlias.get();
        HomeCmd.homeOtherAlias = homeOtherAlias.get();
        HomeCmd.delHomeAlias = delHomeAlias.get();
        HomeCmd.listHomesAlias = listHomesAlias.get();
        HomeCmd.listOtherHomesAlias = listOtherHomesAlias.get();
        HomeCmd.delOtherHomeAlias = delOtherHomeAlias.get();
        HomeCmd.homeCooldownSeconds = homeCooldownSecondsConfig.get();
        HomeCmd.homeOtherCooldownSeconds = homeOtherCooldownSecondsConfig.get();
        HomeCmd.maxHomes = maxHomesConfig.get();

        // TPA
        TpaCmd.isTPAEnable = isTPAEnableConfig.get();
        TpaCmd.tpaAlias = tpaAlias.get();
        TpaCmd.tpaHereAlias = tpaHereAlias.get();
        TpaCmd.tpHereAlias = tpHereAlias.get();
        TpaCmd.tpAllHereAlias = tpAllHereAlias.get();
        TpaCmd.tpaCooldownSeconds = tpaCooldownSecondsConfig.get();
        TpaCmd.maxTPARequestTimeoutSeconds = maxTPARequestTimeoutSecondsConfig.get();

        // Warp
        WarpCmd.isWarpEnable = isWarpEnableConfig.get();
        WarpCmd.setWarpAlias = setWarpAlias.get();
        WarpCmd.warpAlias = warpAlias.get();
        WarpCmd.listWarpsAlias = listWarpsAlias.get();
        WarpCmd.delWarpAlias = delWarpAlias.get();
        WarpCmd.warpCooldownSeconds = warpCooldownSecondsConfig.get();

        // RTP
        RtpCmd.isRTPEnable = isRTPEnableConfig.get();
        RtpCmd.rtpAlias = rtpAlias.get();
        RtpCmd.rtpCooldownSeconds = rtpCooldownSecondsConfig.get();
        RtpCmd.maxRTPAttempts = maxRTPAttemptsConfig.get();
        RtpCmd.minRTPHeightDefault = minRTPHeightDefaultConfig.get();
        RtpCmd.maxRTPHeightDefault = maxRTPHeightDefaultConfig.get();
        RtpCmd.minRTPRadiusDefault = minRTPRadiusDefaultConfig.get();
        RtpCmd.maxRTPRadiusDefault = maxRTPRadiusDefaultConfig.get();
        RtpCmd.minRTPHeightOverworld = minRTPHeightOverworldConfig.get();
        RtpCmd.maxRTPHeightOverworld = maxRTPHeightOverworldConfig.get();
        RtpCmd.minRTPRadiusOverworld = minRTPRadiusOverworldConfig.get();
        RtpCmd.maxRTPRadiusOverworld = maxRTPRadiusOverworldConfig.get();
        RtpCmd.minRTPHeightNether = minRTPHeightNetherConfig.get();
        RtpCmd.maxRTPHeightNether = maxRTPHeightNetherConfig.get();
        RtpCmd.minRTPRadiusNether = minRTPRadiusNetherConfig.get();
        RtpCmd.maxRTPRadiusNether = maxRTPRadiusNetherConfig.get();
        RtpCmd.minRTPHeightEnd = minRTPHeightEndConfig.get();
        RtpCmd.maxRTPHeightEnd = maxRTPHeightEndConfig.get();
        RtpCmd.minRTPRadiusEnd = minRTPRadiusEndConfig.get();
        RtpCmd.maxRTPRadiusEnd = maxRTPRadiusEndConfig.get();

        // fly
        FlyCmd.isFlyEnable = isFlyEnable.get();
        FlyCmd.flyAlias = flyAlias.get();
        FlyCmd.datePattern = datePattern.get();

        // getRegistryName mob
        //CommandGetRegistryName.entitiesWithinRadius = entitiesWithinRadius.get();

        // invsee
        OpenInvCmd.isOpenInvEnable = isOpenInvEnable.get();
        OpenInvCmd.invseeAlias = invseeAlias.get();


        // Trashcan
        TrashcanCmd.isTrashcanEnable = isTrashcanEnable.get();
        TrashcanCmd.trashcanAlias = trashcanAlias.get();
        TrashcanCmd.cleanTrashcanIntervalSeconds = cleanTrashcanIntervalSeconds.get();

        // Hat
        HatCmd.canHat = isHatEnable.get();
        HatCmd.hatAlias = hatAlias.get();
    }

    private static boolean isValidCommandAlias(Object o) {
        if (o instanceof String) {
            return !o.toString().contains("/") && !o.toString().contains(" ");
        }
        return false;
    }
}
