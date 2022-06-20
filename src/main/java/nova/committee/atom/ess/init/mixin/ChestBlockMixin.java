package nova.committee.atom.ess.init.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nova.committee.atom.ess.core.lock.ILockHolder;
import nova.committee.atom.ess.core.lock.LockProfile;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/20 21:46
 * Version: 1.0
 */
@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockMixin extends RandomizableContainerBlockEntity implements ILockHolder {


    private final LockProfile profile = new LockProfile();

    protected ChestBlockMixin(BlockEntityType<?> p_155327_, BlockPos p_155328_, BlockState p_155329_) {
        super(p_155327_, p_155328_, p_155329_);
    }

    @Override
    public LockProfile getLockProfile() {
        return profile;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        profile.saveToNBT(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        profile.loadFromNBT(pTag);
    }
}
