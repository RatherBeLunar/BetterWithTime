package com.bwt.entities;

import com.bwt.items.BwtItems;
import com.bwt.utils.rectangular_entity.RectangularEntity;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WindmillEntity extends RectangularEntity {
    public static float height = 12.8f;
    public static float width = 12.8f;
    public static float length = 0.8f;


    public WindmillEntity(EntityType<? extends RectangularEntity> entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
        this.endCrystalAge = this.random.nextInt(100000);
    }

    public static WindmillEntity spawnAtPos(World world, BlockPos pos) {
        WindmillEntity entity = new WindmillEntity(BwtEntities.windmillEntity, world);
        entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        world.spawnEntity(entity);
        return entity;
    }

    private static final TrackedData<Optional<BlockPos>> BEAM_TARGET = DataTracker.registerData(EndCrystalEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);
    private static final TrackedData<Boolean> SHOW_BOTTOM = DataTracker.registerData(EndCrystalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public int endCrystalAge;

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(BEAM_TARGET, Optional.empty());
        this.getDataTracker().startTracking(SHOW_BOTTOM, true);
    }

    @Override
    public void tick() {
        ++this.endCrystalAge;
        if (this.getWorld() instanceof ServerWorld) {
            BlockPos blockPos = this.getBlockPos();
            if (((ServerWorld)this.getWorld()).getEnderDragonFight() != null && this.getWorld().getBlockState(blockPos).isAir()) {
                this.getWorld().setBlockState(blockPos, AbstractFireBlock.getState(this.getWorld(), blockPos));
            }
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.getBeamTarget() != null) {
            nbt.put("BeamTarget", NbtHelper.fromBlockPos(this.getBeamTarget()));
        }
        nbt.putBoolean("ShowBottom", this.shouldShowBottom());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("BeamTarget", NbtElement.COMPOUND_TYPE)) {
            this.setBeamTarget(NbtHelper.toBlockPos(nbt.getCompound("BeamTarget")));
        }
        if (nbt.contains("ShowBottom", NbtElement.BYTE_TYPE)) {
            this.setShowBottom(nbt.getBoolean("ShowBottom"));
        }
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.isRemoved() && !this.getWorld().isClient) {
            this.remove(Entity.RemovalReason.KILLED);
        }
        return true;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public void kill() {
        super.kill();
    }

    public void setBeamTarget(@Nullable BlockPos beamTarget) {
        this.getDataTracker().set(BEAM_TARGET, Optional.ofNullable(beamTarget));
    }

    @Nullable
    public BlockPos getBeamTarget() {
        return this.getDataTracker().get(BEAM_TARGET).orElse(null);
    }

    public void setShowBottom(boolean showBottom) {
        this.getDataTracker().set(SHOW_BOTTOM, showBottom);
    }

    public boolean shouldShowBottom() {
        return this.getDataTracker().get(SHOW_BOTTOM);
    }

    @Override
    public boolean shouldRender(double distance) {
        return super.shouldRender(distance) || this.getBeamTarget() != null;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(BwtItems.windmillItem);
    }

}
