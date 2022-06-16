package nova.committee.atom.ess.core.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import nova.committee.atom.ess.init.handler.CleanerHandler;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 15:03
 * Version: 1.0
 */
public class AESMobEntity {
    private final Mob entity;
    private final ResourceLocation registryName;

    public AESMobEntity(Mob entity) {
        this.entity = entity;
        this.registryName = EntityType.getKey(entity.getType());
    }

    public boolean filtrate() {
        int index;
        if (CleanerHandler.mobEntitiesMatchMode) {
            // Whitelist
            for (String s : CleanerHandler.mobEntitiesWhitelist) {
                if (s.equals(this.registryName.toString())) {
                    return false;
                } else if ((index = s.indexOf('*')) != -1) {
                    s = s.substring(0, index - 1);
                    if (this.registryName.getNamespace().equals(s)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            // Blacklist
            for (String s : CleanerHandler.mobEntitiesBlacklist) {
                if (s.equals(this.registryName.toString())) {
                    return true;
                } else if ((index = s.indexOf('*')) != -1) {
                    s = s.substring(0, index - 1);
                    if (this.registryName.getNamespace().equals(s)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public Mob getEntity() {
        return entity;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
