package nova.committee.atom.ess.core.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import nova.committee.atom.ess.init.handler.CleanerHandler;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 14:58
 * Version: 1.0
 */
public class AESItemEntity {
    private final ItemEntity entity;
    private final ResourceLocation registryName;

    public AESItemEntity(ItemEntity entity) {
        this.entity = entity;
        this.registryName = this.entity.getItem().getItem().getRegistryName();
    }

    /**
     * @return 清理完毕
     */
    public boolean filtrate() {
        int index;
        if (CleanerHandler.itemEntitiesMatchMode) {
            // Whitelist
            for (String s : CleanerHandler.itemEntitiesWhitelist) {
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
            for (String s : CleanerHandler.itemEntitiesBlacklist) {
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

    public Entity getEntity() {
        return entity;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
