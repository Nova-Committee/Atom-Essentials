package nova.committee.atom.ess.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.common.util.FakePlayer;

import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/20 21:35
 * Version: 1.0
 */
public class Location {
    public final Level level;
    public int x, y, z;
    private BlockPos blockPos;

    /**
     * @param level The current Level of the Location.
     * @param pos   The current position of the Location.
     */
    public Location(Level level, BlockPos pos) {
        this(level, pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * @param level The current Level of the Location.
     * @param x     The current x position of the Location.
     * @param y     The current y position of the Location.
     * @param z     The current z position of the Location.
     */
    public Location(Level level, int x, int y, int z) {

        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;

        blockPos = new BlockPos(x, y, z);
    }

    /**
     * Gets a new Location from a Block Entity.
     */
    public Location(BlockEntity blockEntity) {
        this(blockEntity.getLevel(), blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ());
    }

    /**
     * Gets a new Location from an Entity.
     */
    public Location(Entity entity) {
        this(entity.level, entity.getBlockX(), entity.getBlockY(), entity.getBlockZ());
    }

    /**
     * Gets a new Location from an offset of 1.
     *
     * @param location The Location origin.
     * @param dir      The offset direction.
     */
    public Location(Location location, Direction dir) {
        this(location, dir, 1);
    }

    /**
     * Gets a new Location from a specified offset.
     *
     * @param location The Location origin.
     * @param dir      The offset direction.
     * @param distance The distance of the offset.
     */
    public Location(Location location, Direction dir, int distance) {

        this.level = location.level;
        this.x = location.x + (dir.getStepX() * distance);
        this.y = location.y + (dir.getStepY() * distance);
        this.z = location.z + (dir.getStepZ() * distance);

        blockPos = new BlockPos(x, y, z);
    }

    /**
     * @param level The Level to construct the Location.
     * @param tag   The tag that stored the Location.
     * @return A location constructed from an NBT Tag.
     */
    public static Location readFromNBT(Level level, CompoundTag tag) {

        //Checks if the tag is missing a crucial value. If so, doesn't read the Location.
        if (!tag.contains("locX") || !tag.contains("locY") || !tag.contains("locZ")) {
            return null;
        }

        int x = tag.getInt("locX");
        int y = tag.getInt("locY");
        int z = tag.getInt("locZ");

        return new Location(level, x, y, z);
    }

    /**
     * Moves the Location in a direction.
     *
     * @param dir      The direction to move.
     * @param distance The distance to move.
     */
    public void translate(Direction dir, int distance) {

        this.x += (dir.getStepX() * distance);
        this.y += (dir.getStepY() * distance);
        this.z += (dir.getStepZ() * distance);
        blockPos = new BlockPos(x, y, z);
    }

    /**
     * @return A new Location with the same values.
     */
    public Location copy() {
        return new Location(this.level, this.x, this.y, this.z);
    }

    /**
     * @return The position in Block coordinates.
     */
    public BlockPos getBlockPos() {
        return blockPos;
    }

    /**
     * @return The BlockState at the Location.
     */
    public BlockState getBlockState() {
        return level.getBlockState(blockPos);
    }

    /**
     * @return The Block at the Location.
     */
    public Block getBlock() {
        return getBlockState().getBlock();
    }

    /**
     * Sets the Block at the location.
     *
     * @param block The new Block.
     */
    public void setBlock(Block block) {
        level.setBlock(getBlockPos(), block.defaultBlockState(), 2);
    }

    /**
     * Sets the Block at the location.
     *
     * @param state The new BlockState.
     */
    public void setBlock(BlockState state) {
        setBlock(state.getBlock());
        level.setBlock(getBlockPos(), state, 2);
    }

    /**
     * @return The Block's material at the Location.
     */
    public Material getBlockMaterial() {
        return getBlockState().getMaterial();
    }

    /**
     * @return The Block's drops at the Location.
     */
    public List<ItemStack> getDrops(Player player, ItemStack heldStack) {
        return Block.getDrops(getBlockState(), (ServerLevel) level, getBlockPos(), null, player, heldStack);
    }

    /**
     * @return The current light value at the Location.
     */
    public int getLightValue() {
        return level.getLightEmission(getBlockPos());
    }

    /**
     * @return The BlockEntity at the Location.
     */
    public BlockEntity getBlockEntity() {
        return level.getBlockEntity(getBlockPos());
    }

    /**
     * @param location Reference Location.
     * @return The distance (in Blocks) between the Location and another.
     */
    public double getDistance(Location location) {

        int dx = x - location.x;
        int dy = y - location.y;
        int dz = z - location.z;

        return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
    }

    /**
     * @return A centered Vector at the Location.
     */
    public Vec3 getVector() {
        return new Vec3(x + 0.5D, y + 0.5D, z + 0.5D);
    }

    /**
     * Sets the Block at the location.
     *
     * @param state  The new BlockState.
     * @param placer The Player who created this change.
     */
    public void setBlock(BlockState state, Player placer) {
        level.setBlock(getBlockPos(), state, 2);
        state.getBlock().setPlacedBy(level, getBlockPos(), state, placer, new ItemStack(state.getBlock()));
    }

    /**
     * Sets the Block at the location.
     *
     * @param context The context.
     * @param block   The new Block.
     */
    public void setBlock(BlockPlaceContext context, Block block) {
        setBlock(block.getStateForPlacement(context));
    }

    /**
     * Sets the Block at the location to Air.
     */
    public void setBlockToAir() {
        setBlock(Blocks.AIR);
    }

    /**
     * Breaks the Block at the location.
     *
     * @param breaker The Player who broke the block.
     */
    public void breakBlock(Player breaker) {

        //Prevents FakePlayers from breaking blocks.
        if (breaker instanceof FakePlayer) {
            return;
        }

        SoundHelper.playBlockBreak(this, getBlockState());

        if (breaker instanceof ServerPlayer) {
            ((ServerPlayer) breaker).gameMode.destroyBlock(blockPos);
        }
    }

    /**
     * @return true if the Location is at 0, 0, 0.
     */
    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    /**
     * @return true if the Block at the Location is Air.
     */
    public boolean isAirBlock() {
        return getBlock() == Blocks.AIR;
    }

    /**
     * @return true if a Block could be placed at the Location.
     */
    public boolean isBlockValidForPlacing() {
        return getBlockMaterial().isReplaceable() || isAirBlock();
    }

    /**
     * @return true if the Block at the Location is a full cube.
     */
    public boolean isFullCube() {
        return getBlockState().isCollisionShapeFullBlock(level, getBlockPos());
    }

    /**
     * @param entity The Entity to check for.
     * @return true if the Entity exists at the Location
     */
    public boolean isEntityAtLocation(Entity entity) {

        int entityX = entity.getBlockX();
        int entityY = entity.getBlockY();
        int entityZ = entity.getBlockZ();

        return entityX == x && entityZ == z && (entityY == y || entityY + 1 == y);
    }

    /**
     * @return true if the Block at the Location as collision.
     */
    public boolean doesBlockHaveCollision() {
        return getBlock().getCollisionShape(getBlockState(), level, getBlockPos(), CollisionContext.empty()) != Shapes.empty();
    }

    /**
     * Stores the Location in an NBT Tag.
     *
     * @param tag The tag to store the Location.
     */
    public void writeToNBT(CompoundTag tag) {
        tag.putInt("locX", x);
        tag.putInt("locY", y);
        tag.putInt("locZ", z);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Location newLoc) {
            return level == newLoc.level && x == newLoc.x && y == newLoc.y && z == newLoc.z;
        }

        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }
}
