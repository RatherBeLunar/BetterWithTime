package com.bwt.entities;

import com.bwt.blocks.axles.AxleBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.utils.rectangular_entity.RectangularEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public abstract class HorizontalMechPowerSourceEntity extends RectangularEntity {
    protected float rotation = 0;
    protected float prevRotation = 0;

    protected int ticksBeforeNextFullUpdate = 20;

    protected static final EntityDataAccessor<Float> rotationSpeed = SynchedEntityData.defineId(HorizontalMechPowerSourceEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Integer> DAMAGE_WOBBLE_TICKS = SynchedEntityData.defineId(HorizontalMechPowerSourceEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DAMAGE_WOBBLE_SIDE = SynchedEntityData.defineId(HorizontalMechPowerSourceEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> DAMAGE_WOBBLE_STRENGTH = SynchedEntityData.defineId(HorizontalMechPowerSourceEntity.class, EntityDataSerializers.FLOAT);

    public HorizontalMechPowerSourceEntity(EntityType<? extends HorizontalMechPowerSourceEntity> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    public HorizontalMechPowerSourceEntity(EntityType<? extends HorizontalMechPowerSourceEntity> type, Level level, Vec3 pos, Direction facing) {
        this(type, level);
        setPos(pos);
        setYRot(facing.toYRot());
    }

    public interface Factory {
        HorizontalMechPowerSourceEntity create(Level level, Vec3 pos, Direction facing);
    }


    abstract public boolean tryToSpawn(Player player);
    abstract public Predicate<BlockPos> getBlockInterferencePredicate();
    abstract float computeRotation();
    abstract float getSpeedToPowerThreshold();

    @Override
    public double getEyeY() {
        return this.getBbHeight() / 2;
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(rotationSpeed, 0f);
        builder.define(DAMAGE_WOBBLE_TICKS, 0);
        builder.define(DAMAGE_WOBBLE_SIDE, 1);
        builder.define(DAMAGE_WOBBLE_STRENGTH, 0.0f);
    }

    public float getRotation() {
        return rotation;
    }

    protected void setRotation(float rotation) {
        rotation = (rotation + 360f) % 360f;
        this.prevRotation = this.rotation;
        this.rotation = rotation;
    }

    public float getPrevRotation() {
        return prevRotation;
    }

    public float getRotationSpeed() {
        return getEntityData().get(rotationSpeed);
    }

    public void setRotationSpeed(float speed) {
        getEntityData().set(rotationSpeed, speed);
    }

    public void setDamageWobbleTicks(int damageWobbleTicks) {
        this.entityData.set(DAMAGE_WOBBLE_TICKS, damageWobbleTicks);
    }

    public void setDamageWobbleSide(int damageWobbleSide) {
        this.entityData.set(DAMAGE_WOBBLE_SIDE, damageWobbleSide);
    }

    public void setDamageWobbleStrength(float damageWobbleStrength) {
        this.entityData.set(DAMAGE_WOBBLE_STRENGTH, damageWobbleStrength);
    }

    public float getDamageWobbleStrength() {
        return this.entityData.get(DAMAGE_WOBBLE_STRENGTH);
    }

    public int getDamageWobbleTicks() {
        return this.entityData.get(DAMAGE_WOBBLE_TICKS);
    }

    public int getDamageWobbleSide() {
        return this.entityData.get(DAMAGE_WOBBLE_SIDE);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.DESTROY;
    }

    public boolean tryToSpawn(Player player, Component blockBlockedErrorMessage, Component entityBlockedErrorMessage) {
        if (player instanceof ServerPlayer) {
            player = null;
        }

        if (placementBlockedByBlock()) {
            if(player != null) {
                player.sendSystemMessage(blockBlockedErrorMessage);
            }
            return false;
        }
        if (placementBlockedByEntity()) {
            if(player != null) {
                player.sendSystemMessage(entityBlockedErrorMessage);
            }
            return false;
        }

        if (placementHasBadAxleState()) {
            return false;
        }

        setRotationSpeed(computeRotation());
        Level level = level();
        level.addFreshEntity(this);
        return true;
    }

    public boolean placementBlockedByBlock() {
        Predicate<BlockPos> blockInterferencePredicate = getBlockInterferencePredicate();
        return BlockPos.betweenClosedStream(getBoundingBox())
                // Ignore the axle we're on
                .filter(blockPos -> !blockPos.equals(this.blockPosition()))
                .anyMatch(blockInterferencePredicate);
    }

    @Override
    protected void onInsideBlock(BlockState state) {
        destroyWithDrop();
    }

    public boolean placementBlockedByEntity() {
        ArrayList<Entity> anyEntities = new ArrayList<>();
        level().getEntities(
                EntityTypeTest.forClass(Entity.class),
                getBoundingBox(),
                entity -> entity != this && EntitySelector.NO_SPECTATORS.test(entity) && !(entity instanceof ItemEntity),
                anyEntities, 1);
        return !anyEntities.isEmpty();
    }

    public boolean placementHasBadAxleState() {
        Level level = level();

        BlockState axleBlock = level.getBlockState(blockPosition());

        // Bad block type
        if (!axleBlock.is(BwtBlocks.axleBlock) && !axleBlock.is(BwtBlocks.axlePowerSourceBlock)) {
            return true;
        }
        Direction.Axis axleAxis = axleBlock.getValue(AxleBlock.AXIS);
        float yaw = getYRot();

        // Misaligned
        return Direction.fromAxisAndDirection(axleAxis, Direction.AxisDirection.NEGATIVE).toYRot() != yaw
                && Direction.fromAxisAndDirection(axleAxis, Direction.AxisDirection.POSITIVE).toYRot() != yaw;
    }

    @Override
    public void tick() {
        super.tick();

        if (isRemoved()) {
            return;
        }
        if (this.getDamageWobbleTicks() > 0) {
            this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
        }
        if (this.getDamageWobbleStrength() > 0.0f) {
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0f);
        }

        if (level().isClientSide) {
            updateRotation();
        }
        else {
            ticksBeforeNextFullUpdate--;
            if (ticksBeforeNextFullUpdate <= 0) {
                ticksBeforeNextFullUpdate = 20;
                fullUpdate();
            }
        }
        level()
                .getEntities(this, this.getBoundingBox().inflate(0.01f, 0.01f, 0.01f), EntitySelector.pushableBy(this))
                .forEach(this::push);
    }

    @Override
    public void push(Entity entity) {
        if (entity.noPhysics || this.noPhysics) {
            return;
        }
        AABB thisBox = this.getBoundingBox().inflate(0.01f, 0.01f, 0.01f);
        AABB entityBox = entity.getBoundingBox();
        List<Tuple<Direction, Double>> intersections = new ArrayList<>();
        for (Direction.Axis axis : Direction.Axis.values()) {
            double thisMin = thisBox.min(axis);
            double thisMax = thisBox.max(axis);
            double entityMin = entityBox.min(axis);
            double entityMax = entityBox.max(axis);
            if (thisMax - entityMin > 0) {
                Direction intersectDirection = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
                intersections.add(new Tuple<>(intersectDirection, thisMax - entityMin));
            }
            if (entityMax - thisMin > 0) {
                Direction intersectDirection = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
                intersections.add(new Tuple<>(intersectDirection, entityMax - thisMin));
            }
        }
        intersections.stream()
                .min(Comparator.comparingDouble(pair -> Math.abs(pair.getB())))
                .filter(pair -> pair.getB() > 0.01f)
                .ifPresent(pair -> entity.push(
                        new Vec3(pair.getA().step().mul(pair.getB().floatValue() / 2)))
                );
    }

    protected void updateRotation() {
        setRotation(rotation + this.getEntityData().get(rotationSpeed));
    }

    protected void fullUpdate() {
        if (placementBlockedByBlock() || placementHasBadAxleState()) {
            destroyWithDrop();
            return;
        }

        setRotationSpeed(computeRotation());

        setHostAxlePower(Math.abs(getRotationSpeed()) > getSpeedToPowerThreshold());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide || this.isRemoved()) {
            return true;
        }
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.setDamageWobbleSide(-this.getDamageWobbleSide());
        this.setDamageWobbleTicks(10);
        this.markHurt();
        this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0f);
        this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
        boolean instantKill = source.getEntity() instanceof Player && ((Player)source.getEntity()).getAbilities().instabuild;
        if (instantKill) {
            discard();
            return true;
        }
        if (this.getDamageWobbleStrength() > 40.0f) {
            destroyWithDrop();
        }
        return true;
    }

    public void destroyWithDrop() {
        if (isRemoved()) return;
        spawnAtLocation(getPickResult(), 0.5f);
        kill();
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        setHostAxlePower(false);
    }

    protected void setHostAxlePower(boolean powered) {
        Level level = level();
        BlockPos pos = blockPosition();
        BlockState hostBlockState = level.getBlockState(pos);
        if (!powered && hostBlockState.is(BwtBlocks.axlePowerSourceBlock)) {
            level.removeBlock(pos, false);
            level.setBlockAndUpdate(pos, BwtBlocks.axleBlock.defaultBlockState()
                    .setValue(AxleBlock.AXIS, hostBlockState.getValue(AxleBlock.AXIS)));
        }
        if (powered && hostBlockState.is(BwtBlocks.axleBlock)) {
            level.setBlockAndUpdate(pos, BwtBlocks.axlePowerSourceBlock.defaultBlockState()
                    .setValue(AxleBlock.AXIS, hostBlockState.getValue(AxleBlock.AXIS)));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putFloat("rotationSpeed", entityData.get(rotationSpeed));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.entityData.set(rotationSpeed, nbt.getFloat("rotationSpeed"));
    }
}
