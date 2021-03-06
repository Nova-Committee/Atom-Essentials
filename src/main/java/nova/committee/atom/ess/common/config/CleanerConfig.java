package nova.committee.atom.ess.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import nova.committee.atom.ess.api.common.config.AbstractModConfig;
import nova.committee.atom.ess.init.handler.CleanerHandler;
import nova.committee.atom.ess.init.handler.ConfigHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 21:38
 * Version: 1.0
 */
public class CleanerConfig extends AbstractModConfig {

    private ForgeConfigSpec.BooleanValue
            isItemEntityCleanupEnable,
            isMobEntityCleanupEnable;
    private ForgeConfigSpec.BooleanValue
            isAnimalEntitiesCleanupEnable,
            isMonsterEntitiesCleanupEnable,
            isExperienceOrbEntityCleanupEnable,
            isFallingBlocksEntityCleanupEnable,
            isArrowEntityCleanupEnable,
            isTridentEntityCleanupEnable,
            isDamagingProjectileEntityCleanupEnable,
            isShulkerBulletEntityCleanupEnable,
            isFireworkRocketEntityCleanupEnable,
            isItemFrameEntityCleanupEnable,
            isPaintingEntityCleanupEnable,
            isBoatEntityCleanupEnable,
            isTNTEntityCleanupEnable;
    private ForgeConfigSpec.IntValue
            cleanupItemEntitiesIntervalSeconds,
            cleanupItemEntitiesCountdownSeconds,
            cleanupMobEntitiesIntervalSeconds,
            cleanupMobEntitiesCountdownSeconds,
            cleanupOtherEntitiesIntervalSeconds;
    private ForgeConfigSpec.BooleanValue
            itemEntitiesMatchMode,
            mobEntitiesMatchMode;
    private ForgeConfigSpec.ConfigValue<List<? extends String>>
            itemEntitiesWhitelist,
            itemEntitiesBlacklist,
            mobEntitiesWhitelist,
            mobEntitiesBlacklist;
    private ForgeConfigSpec.ConfigValue<? extends String>
            cleanedupItemEntitiesMessage,
            cleanupItemEntitiesCountdownMessage;
    private ForgeConfigSpec.ConfigValue<? extends String>
            cleanedupMobEntitiesMessage,
            cleanupMobEntitiesCountdownMessage;


    public CleanerConfig(ForgeConfigSpec.Builder builder) {
        super(builder);
    }

    @Override
    public void init() {
        builder.push("EntityCleanup");

        this.builder.push("ItemEntities");
        isItemEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up item entities in your server.",
                        "Default value: true")
                .define("IsItemEntityCleanupEnable", false);

        cleanupItemEntitiesIntervalSeconds = builder
                .comment("Time interval in seconds between two actions of cleaning item entities.",
                        "Default value: 300 seconds (5 minutes)")
                .defineInRange("CleanUpItemEntitiesInterval", 300, 1, Integer.MAX_VALUE);

        this.cleanedupItemEntitiesMessage = this.builder
                .comment("The customized message for notifying players that items were being cleaned.",
                        "If you left it blank, no notification will be sent to players.",
                        "If you set it to \"null\", default notifications from lang file will be sent.",
                        "You should put \"%d\" at somewhere in your customized message to display the quantity of cleaned entities.",
                        "Default value: \"null\"")
                .define("CleanedUpItemEntitiesMessage", "null");

        cleanupItemEntitiesCountdownSeconds = this.builder
                .comment("Seconds of warning message sent before next action of cleaning item entities.",
                        "Default value: 30 seconds")
                .defineInRange("CleanUpItemEntitiesCountdown", 30, 1, Integer.MAX_VALUE);

        this.cleanupItemEntitiesCountdownMessage = this.builder
                .comment("The customized message for notifying players that how many seconds left for next action of cleaning.",
                        "If you left it blank, no notification will be sent to players.",
                        "If you set it to \"null\", default notifications from lang file will be sent.",
                        "You should put \"%d\" at somewhere in your customized message to display how many seconds left. (Set in CleanUpItemEntitiesCountdown)",
                        "Default value: \"null\"")
                .define("CleanUpItemEntitiesCountdownMessage", "null");

        // Whitelist or Blacklist
        this.itemEntitiesMatchMode = this.builder
                .comment("Set it true to enable matching entities with whitelist.",
                        "False to enable matching entities with blacklist. (Only clean entities in blacklist)",
                        "Default value: true (Use whitelist)")
                .define("ItemEntitiesMatchMode", true);
        itemEntitiesWhitelist = builder
                .comment("List of item registry names (E.g: minecraft:stone) not being cleaned.",
                        "You could use /atomess getItemRegistryName item command with a item hold in your main hand to get it's registry name.",
                        "You could also use minecraft:* or rats:* to add all items of certain mod to the whitelist.")
                .define("ItemEntitiesWhitelist", Arrays.asList("minecraft:diamond", "minecraft:emerald"), ConfigHandler::isResourceLocationList);
        this.itemEntitiesBlacklist = this.builder
                .comment("Entities here will be cleaned!",
                        "Same format as the whitelist.")
                .define("ItemEntitiesBlacklist", Collections.emptyList(), ConfigHandler::isResourceLocationList);
        this.builder.pop();

        this.builder.comment("Mobs = Monsters + Animals basically.").push("MobEntities");
        isMobEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up mob entities in your server.",
                        "Default value: true")
                .define("IsMobEntityCleanupEnable", false);
        this.isAnimalEntitiesCleanupEnable = this.builder
                .comment("Set it to false to disable cleaning up all animal entities. (Sheep, cow, ...)",
                        "Default value: true",
                        "Note that even you enable this option, animal entities in whitelist will still be ignored.")
                .define("IsAnimalEntitiesCleanupEnable", true);
        this.isMonsterEntitiesCleanupEnable = this.builder
                .comment("Set it to false to disable cleaning up all monster entities. (Zombie, skeleton, ...)",
                        "Default value: true",
                        "Note that even you enable this option, monster entities in whitelist will still be ignored.")
                .define("IsMonsterEntitiesCleanupEnable", true);

        cleanupMobEntitiesIntervalSeconds = builder
                .comment("Time interval in seconds between two actions of cleaning mob entities.",
                        "Default value: 360 seconds (6 minutes)")
                .defineInRange("cleanMobEntitiesInterval", 360, 1, Integer.MAX_VALUE);

        this.cleanedupMobEntitiesMessage = this.builder
                .comment("The customized message for notifying players that living entities were being cleaned.",
                        "If you left it blank, no notification will be sent to players.",
                        "If you set it to \"null\", default notifications from lang file will be sent.",
                        "You should put \"%d\" at somewhere in your customized message to display the quantity of cleaned entities.",
                        "Default value: \"null\"")
                .define("CleanedUpMobEntitiesMessage", "null");

        this.cleanupMobEntitiesCountdownSeconds = this.builder
                .comment("Seconds of warning message sent before next action of cleaning mob entities.",
                        "Default value: 30 seconds")
                .defineInRange("CleanUpMobEntitiesCountdown", 30, 1, Integer.MAX_VALUE);

        this.cleanupMobEntitiesCountdownMessage = this.builder
                .comment("The customized message for notifying players that how many seconds left for next action of cleaning.",
                        "If you left it blank, no notification will be sent to players.",
                        "If you set it to \"null\", notifications from lang file will be sent.",
                        "You should put \"%d\" at somewhere in your customized message to display how many seconds left. (Set in CleanUpMobEntitiesCountdown)",
                        "Default value: \"null\"")
                .define("CleanUpMobEntitiesCountdownMessage", "null");

        // Whitelist or Blacklist
        this.mobEntitiesMatchMode = this.builder
                .comment("Set it true to enable matching entities with whitelist.",
                        "False to enable matching entities with blacklist. (Only clean entities in blacklist)",
                        "Default value: true (Use whitelist)")
                .define("MobEntitiesMatchMode", true);
        this.mobEntitiesWhitelist = this.builder
                .comment("List of mob resourcelocation names (E.g: minecraft:cow) not being cleaned.",
                        "You could use /atomess getItemRegistryName mob to get the registry names of nearby mobs. (radius specified in Commands section)",
                        "You could also use minecraft:* or minecolonies:* to add all living entities of certain mod to whitelist.")
                .define("MobEntitiesWhitelist", Arrays.asList("minecraft:cat", "minecraft:mule", "minecraft:wolf", "minecraft:horse",
                        "minecraft:donkey", "minecraft:wither", "minecraft:guardian", "minecraft:villager", "minecraft:iron_golem", "minecraft:snow_golem",
                        "minecraft:vindicator", "minecraft:ender_dragon", "minecraft:elder_guardian"), ConfigHandler::isResourceLocationList);
        this.mobEntitiesBlacklist = this.builder
                .comment("Entities here will be cleaned!",
                        "Same format as the whitelist.")
                .define("MobEntitiesBlacklist", Collections.emptyList(), ConfigHandler::isResourceLocationList);
        this.builder.pop();

        this.builder.push("OtherEntities");
        cleanupOtherEntitiesIntervalSeconds = builder
                .comment("Time interval in seconds between two actions of cleaning other entities.",
                        "Default value: 300 seconds (5 minutes)")
                .defineInRange("CleanOtherEntitiesInterval", 300, 1, Integer.MAX_VALUE);

        isExperienceOrbEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up experience orb entities in your server.",
                        "Default value: true")
                .define("IsExperienceOrbEntityCleanupEnable", true);

        isFallingBlocksEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up falling block entities in your server.",
                        "Default value: true")
                .define("IsFallingBlocksEntityCleanupEnable", true);

        isArrowEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up arrow entities in your server.",
                        "Default value: true")
                .define("IsArrowEntityCleanupEnable", true);

        isTridentEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up trident entities in your server.",
                        "Default value: false")
                .define("IsTridentEntityCleanupEnable", false);

        isDamagingProjectileEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up damaging projectile entities (E.g Fireballs and wither skulls) in your server.",
                        "Default value: false")
                .define("IsDamagingProjectileEntityCleanupEnable", false);

        isShulkerBulletEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up shulker bullet entities in your server.",
                        "Default value: true")
                .define("IsShulkerBulletEntityCleanupEnable", true);

        isFireworkRocketEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up firework rocket entities in your server.",
                        "Default value: false")
                .define("IsFireworkRocketEntityCleanupEnable", false);

        isItemFrameEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up item frame entities in your server.",
                        "Default value: false")
                .define("IsItemFrameEntityCleanupEnable", false);

        isPaintingEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up painting entities in your server.",
                        "Default value: false")
                .define("IsPaintingEntityCleanupEnable", false);

        isBoatEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up boat entities in your server.",
                        "Default value: false")
                .define("IsBoatEntityCleanupEnable", false);

        isTNTEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up TNT entities (should be the fired TNT) in your server.",
                        "Default value: true")
                .define("IsTNTEntityCleanupEnable", true);
        this.builder.pop();

        builder.pop();
    }

    @Override
    public void get() {
        CleanerHandler.cleanupItemEntitiesIntervalSeconds = this.cleanupItemEntitiesIntervalSeconds.get();
        CleanerHandler.cleanedUpItemEntitiesMessage = this.cleanedupItemEntitiesMessage.get();
        CleanerHandler.cleanupItemEntitiesCountdownSeconds = this.cleanupItemEntitiesCountdownSeconds.get();
        CleanerHandler.cleanupItemEntitiesCountdownMessage = this.cleanupItemEntitiesCountdownMessage.get();
        CleanerHandler.itemEntitiesMatchMode = this.itemEntitiesMatchMode.get();
        CleanerHandler.itemEntitiesWhitelist = this.itemEntitiesWhitelist.get();
        CleanerHandler.itemEntitiesBlacklist = this.itemEntitiesBlacklist.get();

        CleanerHandler.isMobEntityCleanupEnable = isMobEntityCleanupEnable.get();
        CleanerHandler.isAnimalEntitiesCleanupEnable = this.isAnimalEntitiesCleanupEnable.get();
        CleanerHandler.isMonsterEntitiesCleanupEnable = this.isMonsterEntitiesCleanupEnable.get();
        CleanerHandler.cleanupMobEntitiesIntervalSeconds = this.cleanupMobEntitiesIntervalSeconds.get();
        CleanerHandler.cleanedUpMobEntitiesMessage = this.cleanedupMobEntitiesMessage.get();
        CleanerHandler.cleanupMobEntitiesCountdownSeconds = this.cleanupMobEntitiesCountdownSeconds.get();
        CleanerHandler.cleanupMobEntitiesCountdownMessage = this.cleanupMobEntitiesCountdownMessage.get();
        CleanerHandler.mobEntitiesMatchMode = this.mobEntitiesMatchMode.get();
        CleanerHandler.mobEntitiesWhitelist = this.mobEntitiesWhitelist.get();
        CleanerHandler.mobEntitiesBlacklist = this.mobEntitiesBlacklist.get();

        CleanerHandler.cleanupOtherEntitiesIntervalSeconds = cleanupOtherEntitiesIntervalSeconds.get();
        CleanerHandler.isItemEntityCleanupEnable = isItemEntityCleanupEnable.get();
        CleanerHandler.isExperienceOrbEntityCleanupEnable = isExperienceOrbEntityCleanupEnable.get();
        CleanerHandler.isFallingBlocksEntityCleanupEnable = isFallingBlocksEntityCleanupEnable.get();
        CleanerHandler.isArrowEntityCleanupEnable = isArrowEntityCleanupEnable.get();
        CleanerHandler.isTridentEntityCleanupEnable = isTridentEntityCleanupEnable.get();
        CleanerHandler.isDamagingProjectileEntityCleanupEnable = isDamagingProjectileEntityCleanupEnable.get();
        CleanerHandler.isShulkerBulletEntityCleanupEnable = isShulkerBulletEntityCleanupEnable.get();
        CleanerHandler.isFireworkRocketEntityCleanupEnable = isFireworkRocketEntityCleanupEnable.get();
        CleanerHandler.isItemFrameEntityCleanupEnable = isItemFrameEntityCleanupEnable.get();
        CleanerHandler.isPaintingEntityCleanupEnable = isPaintingEntityCleanupEnable.get();
        CleanerHandler.isBoatEntityCleanupEnable = isBoatEntityCleanupEnable.get();
        CleanerHandler.isTNTEntityCleanupEnable = isTNTEntityCleanupEnable.get();
    }
}
