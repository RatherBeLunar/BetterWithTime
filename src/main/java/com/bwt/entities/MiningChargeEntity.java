package com.bwt.entities;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.mining_charge.MiningChargeBlock;
import com.bwt.blocks.mining_charge.MiningChargeExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MiningChargeEntity extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(MiningChargeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockState> BLOCK_STATE = SynchedEntityData.defineId(MiningChargeEntity.class, EntityDataSerializers.BLOCK_STATE);
    private static final int DEFAULT_FUSE = 80;

    @Nullable
    private LivingEntity causingEntity;
    public boolean attachedToBlock;

    public MiningChargeEntity(EntityType<? extends MiningChargeEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
        this.attachedToBlock = true;
    }

    public MiningChargeEntity(Level level, Vec3 position, BlockState state, @Nullable LivingEntity igniter) {
        this(BwtEntities.miningChargeEntity, level);
        this.setFuse(DEFAULT_FUSE);
        this.setBlockState(state);
        this.xo = position.x;
        this.yo = position.y;
        this.zo = position.z;
        this.setPos(position);
//        setYaw(getYaw());
        this.causingEntity = igniter;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(FUSE, 80);
        builder.define(BLOCK_STATE, BwtBlocks.miningChargeBlock.defaultBlockState());
    }

    protected void setFacing(Direction direction) {
        setBlockState(MiningChargeBlock.withSurfaceOrientation(getBlockState(), direction));
    }

    public Direction getNearestViewDirection() {
        return MiningChargeBlock.getSurfaceOrientation(getBlockState());
    }

    public void setFuse(int fuse) {
        this.entityData.set(FUSE, fuse);
    }

    public int getFuse() {
        return this.entityData.get(FUSE);
    }

    public void setBlockState(BlockState state) {
        this.entityData.set(BLOCK_STATE, state);
    }

    public BlockState getBlockState() {
        return this.entityData.get(BLOCK_STATE);
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        if (attachedToBlock) {
            // make sure we're still attached
            BlockPos attachedBlockPos = blockPosition().relative(getNearestViewDirection().getOpposite());
            attachedToBlock = level().getBlockState(attachedBlockPos).isFaceSturdy(level(), attachedBlockPos, getNearestViewDirection());
        }
        if (!attachedToBlock) {
            if (getNearestViewDirection() == Direction.DOWN) {
                setFacing(Direction.UP);
            }
            tickMovement();
        }
        int i = this.getFuse() - 1;
        this.setFuse(i);
        if (i <= 0) {
            this.discard();
            if (!this.level().isClientSide) {
                this.explode();
            }
        } else {
            this.updateInWaterStateAndDoFluidPushing();
            if (this.level().isClientSide) {
                this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    private void tickMovement() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
        }
    }

    private void explode() {
//        this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 6.0f, World.ExplosionSourceType.NONE);
        createMiningChargeExplosion(6.0f);
    }

    public void createMiningChargeExplosion(float power) {
        Explosion.BlockInteraction destructionType = level().getGameRules().getBoolean(GameRules.RULE_TNT_EXPLOSION_DROP_DECAY) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
        Vec3 offsetPos = this.blockPosition().relative(getNearestViewDirection().getOpposite()).getCenter();
        Explosion explosion = new MiningChargeExplosion(
                level(),
                this,
                offsetPos,
                Explosion.getDefaultDamageSource(level(), this),
                power,
                false,
                destructionType,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                SoundEvents.GENERIC_EXPLODE
        );
        explosion.explode();
        explosion.finalizeExplosion(true);
        if (level() instanceof ServerLevel serverLevel) {
            if (!explosion.interactsWithBlocks()) {
                explosion.clearToBlow();
            }
            for (ServerPlayer serverPlayerEntity : serverLevel.players()) {
                if (!(serverPlayerEntity.distanceToSqr(getX(), getY(), getZ()) < 4096.0)) continue;
                serverPlayerEntity.connection.send(new ClientboundExplodePacket(getX(), getY(), getZ(), power, explosion.getToBlow(), explosion.getHitPlayers().get(serverPlayerEntity), explosion.getBlockInteraction(), explosion.getSmallExplosionParticles(), explosion.getLargeExplosionParticles(), explosion.getExplosionSound()));
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putShort("fuse", (short)this.getFuse());
        nbt.put("block_state", NbtUtils.writeBlockState(this.getBlockState()));
        nbt.putInt("facing", getNearestViewDirection().get3DDataValue());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        setFuse(nbt.getShort("fuse"));
        if (nbt.contains("block_state", Tag.TAG_COMPOUND)) {
            this.setBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), nbt.getCompound("block_state")));
        }
        setFacing(Direction.from3DDataValue(nbt.getInt("facing")));
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        return this.causingEntity;
    }

    @Override
    public void restoreFrom(Entity original) {
        super.restoreFrom(original);
        if (original instanceof MiningChargeEntity miningChargeEntity) {
            this.causingEntity = miningChargeEntity.causingEntity;
        }
    }

    @Override
    public double getEyeY() {
        return 0.15f;
    }
}
