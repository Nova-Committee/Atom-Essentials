package nova.committee.atom.ess.core.model;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 10:31
 * Version: 1.0
 */
public class TeleportPos implements INBTSerializable<CompoundTag> {

    private ResourceKey<Level> dimension;
    private BlockPos pos;

    public TeleportPos(Player player) {
        this.dimension = player.level.dimension();
        this.pos = player.getOnPos();
    }

    public TeleportPos(ResourceKey<Level> dimension, BlockPos pos) {
        this.dimension = dimension;
        this.pos = pos;
    }

    public TeleportPos(BlockPos pos) {
        this.dimension = Level.OVERWORLD;
        this.pos = pos;
    }

    public TeleportPos() {}

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setDimension(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("dimension", dimension.getRegistryName().getNamespace());
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("dimension")));
        this.pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }

    @Override
    public String toString() {
        String dimension = this.dimension.getRegistryName().getPath();
        String blockpos = "x: " + this.pos.getX() + " y: " + this.pos.getY() + " z: " + this.pos.getZ();
        return "World: " + dimension + "\nPosition: " + blockpos;
    }
}
