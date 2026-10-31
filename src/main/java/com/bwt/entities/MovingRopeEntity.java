package com.bwt.entities;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.pulley.PulleyBlockEntity;
import com.bwt.mixin.MovableBlockEntityMixin;
import com.bwt.utils.TrackedDataHandlers;
import com.bwt.utils.VoxelShapedEntity;
import com.bwt.utils.rectangular_entity.EntityRectDimensions;
import com.bwt.utils.rectangular_entity.RectangularEntity;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

import java.util.*;

public class MovingRopeEntity extends RectangularEntity implements VoxelShapedEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    protected static final EntityDataAccessor<BlockPos> pulleyPos = SynchedEntityData.defineId(MovingRopeEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Integer> targetY = SynchedEntityData.defineId(MovingRopeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> up = SynchedEntityData.defineId(MovingRopeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Map<Vec3i, BlockState>> blockMap = SynchedEntityData.defineId(MovingRopeEntity.class, TrackedDataHandlers.blockStateMapHandler);
    private static final EntityDataAccessor<Map<Vec3i, CompoundTag>> blockEntityNbtMap = SynchedEntityData.defineId(MovingRopeEntity.class, TrackedDataHandlers.blockEntityMapHandler);
    private VoxelShape voxelShape = Shapes.block();
    private final float flyDist = 1f / 20f;

    public MovingRopeEntity(EntityType<? extends MovingRopeEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }


    public MovingRopeEntity(Level level, BlockPos pulley, BlockPos source, int targetY) {
        this(BwtEntities.movingRopeEntity, level);
        setTargetY(targetY);
        if (source != null) {
            setIsMovingUp(source.getY() < targetY);
            setPos(source.getX() + 0.5, source.getY(), source.getZ() + 0.5);
        }
        setPulleyPos(pulley);
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(pulleyPos, BlockPos.ZERO);
        builder.define(up, false);
        builder.define(targetY, 0);
        builder.define(blockMap, Maps.newHashMap());
        builder.define(blockEntityNbtMap, Maps.newHashMap());
    }

    public void setPulleyPos(BlockPos pos) {
        this.entityData.set(pulleyPos, pos);
    }

    public BlockPos getPulleyPos() {
        return this.entityData.get(pulleyPos);
    }

    public boolean isMovingUp() {
        return this.entityData.get(up);
    }

    public void setIsMovingUp(boolean value) {
        this.entityData.set(up, value);
    }

    public int getTargetY() {
        return this.entityData.get(targetY);
    }

    public void setTargetY(int value) {
        this.entityData.set(targetY, value);
    }

    public void setBlockMap(Map<Vec3i, BlockState> map) {
        this.entityData.set(blockMap, map);
    }

    public HashMap<Vec3i, BlockState> getBlockMap() {
        Map<Vec3i, BlockState> blockMap = this.entityData.get(MovingRopeEntity.blockMap);
        return new HashMap<>(blockMap);
    }

    public void setBlockEntityNbtMap(Map<Vec3i, CompoundTag> map) {
        this.entityData.set(blockEntityNbtMap, map);
    }

    public HashMap<Vec3i, CompoundTag> getBlockEntityNbtMap() {
        Map<Vec3i, CompoundTag> nbtMap = this.entityData.get(blockEntityNbtMap);
        return new HashMap<>(nbtMap);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
    }

    @Override
    public VoxelShape getVoxelShape() {
        return voxelShape;
    }

    public void setVoxelShape(VoxelShape voxelShape) {
        this.voxelShape = voxelShape;
        setBoundingBox(voxelShape.bounds());
        refreshDimensions();
        makeBoundingBox();
    }

    @Override
    public EntityRectDimensions getRectDimensions() {
        AABB boundingBox = getBoundingBox();
        return EntityRectDimensions.fixed((float) boundingBox.getXsize(), (float) boundingBox.getYsize(), (float) boundingBox.getZsize());
    }

    @Override
    protected AABB makeBoundingBox(Vec3 position) {
        return AABB.encapsulatingFullBlocks(
                new BlockPos(
                    getBlockMap().keySet().stream().mapToInt(Vec3i::getX).min().orElse(0),
                    getBlockMap().keySet().stream().mapToInt(Vec3i::getY).min().orElse(0),
                    getBlockMap().keySet().stream().mapToInt(Vec3i::getZ).min().orElse(0)
                ),
                new BlockPos(
                    getBlockMap().keySet().stream().mapToInt(Vec3i::getX).max().orElse(0),
                    getBlockMap().keySet().stream().mapToInt(Vec3i::getY).max().orElse(0),
                    getBlockMap().keySet().stream().mapToInt(Vec3i::getZ).max().orElse(0)
                )
        ).move(position).move(-0.5, 0, -0.5);
    }


    @Override
    public double getEyeY() {
        return -1;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        setPulleyPos(new BlockPos(valueInput.getIntOr("PulleyX", 0), valueInput.getIntOr("PulleyY", 0), valueInput.getIntOr("PulleyZ", 0)));
        setTargetY(valueInput.getIntOr("TargetY", 0));
        setIsMovingUp(valueInput.getBooleanOr("Up", false));
        valueInput.childrenList("blocks").ifPresent(list -> {
            Map<Vec3i, BlockState> blocks = deserializeBlockMap(list);
            setBlockMap(blocks);
            rebuildBlockBoundingBox();
        });
        valueInput.childrenList("blockEntities").ifPresent(list ->
                setBlockEntityNbtMap(deserializeBlockEntities(list))
        );
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        valueOutput.putInt("PulleyX", getPulleyPos().getX());
        valueOutput.putInt("PulleyY", getPulleyPos().getY());
        valueOutput.putInt("PulleyZ", getPulleyPos().getZ());
        valueOutput.putInt("TargetY", getTargetY());
        valueOutput.putBoolean("Up", isMovingUp());
        serializeBlockMap(valueOutput.childrenList("blocks"));
        serializeBlockEntities(valueOutput.childrenList("blockEntities"));
    }

    private void serializeBlockEntities(ValueOutput.ValueOutputList outputList) {
        getBlockEntityNbtMap().forEach((offset, blockEntity) -> {
            ValueOutput child = outputList.addChild();
            child.putLong("offset", new BlockPos(offset).asLong());
            child.store("blockEntity", CompoundTag.CODEC, blockEntity);
        });
    }

    private Map<Vec3i, CompoundTag> deserializeBlockEntities(ValueInput.ValueInputList blockEntities) {
        Map<Vec3i, CompoundTag> map = new HashMap<>();
        for (ValueInput entry : blockEntities) {
            map.put(
                    entry.getLong("offset").map(BlockPos::of).orElse(BlockPos.ZERO),
                    entry.read("blockEntity", CompoundTag.CODEC).orElse(new CompoundTag())
            );
        }
        return map;
    }

    private void serializeBlockMap(ValueOutput.ValueOutputList outputList) {
        for (Map.Entry<Vec3i, BlockState> entry : getBlockMap().entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            ValueOutput child = outputList.addChild();
            child.putLong("pos", new BlockPos(entry.getKey()).asLong());
            child.store("state", BlockState.CODEC, entry.getValue());
        }
    }

    private Map<Vec3i, BlockState> deserializeBlockMap(ValueInput.ValueInputList blocks) {
        Map<Vec3i, BlockState> map = new HashMap<>();
        for (ValueInput entry : blocks) {
            Vec3i pos = entry.getLong("pos").map(BlockPos::of).orElse(BlockPos.ZERO);
            entry.read("state", BlockState.CODEC).ifPresent(state -> map.put(pos, state));
        }
        return map;
    }

    public boolean isColliding(BlockPos pos, BlockState state) {
        VoxelShape voxelShape2 = state.getCollisionShape(this.level(), pos, CollisionContext.of(this)).move(pos.getX(), pos.getY(), pos.getZ());
        return Shapes.joinIsNotEmpty(voxelShape2, Shapes.create(this.getBoundingBox()), BooleanOp.AND);
    }

    public void rebuildBlockBoundingBox() {
        HashMap<Vec3i, BlockState> blocks = getBlockMap();
        if (blocks == null || blocks.isEmpty()) {
            setVoxelShape(Shapes.block());
            return;
        }
        VoxelShape newShape = Shapes.empty();
        for (Map.Entry<Vec3i, BlockState> entry : blocks.entrySet()) {
            newShape = Shapes.or(newShape, entry.getValue().getCollisionShape(level(), blockPosition().offset(entry.getKey())).move(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ()));
        }
        setVoxelShape(newShape);
    }

    private double getSpeed() {
        return isMovingUp() ? flyDist : -flyDist;
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return other instanceof MovingRopeEntity;
    }

    @Override
    public void tick() {
        if (getPulleyPos() == null) {
            return;
        }
        rebuildBlockBoundingBox();
        if (isMovingUp()) {
            if (getY() > getTargetY()) {
                if (done())
                    return;
            }
        } else {
            if (getY() < getTargetY()) {
                if (done())
                    return;
            }
        }

        HashMap<Entity, AABB> riders = getRiders();
        moveRiders(riders);
        setPos(
                getPulleyPos().getX() + 0.5,
                this.getY() + getSpeed(),
                getPulleyPos().getZ() + 0.5
        );
        riders = getRiders();
        moveRiders(riders);
    }

    public HashMap<Entity, AABB> getRiders() {
        HashMap<Entity, AABB> entities = new HashMap<>();
        for (AABB box : this.getVoxelShape().move(getX() - 0.5, getY(), getZ() - 0.5).toAabbs()) {
            for (Entity entity1 : level().getEntities(this, box.inflate(1.0E-7, 0.15, 1.0E-7), EntitySelector.NO_SPECTATORS.and(entity -> !(entity instanceof HangingEntity)))) {
                entities.put(entity1, box);
            }
        }
        return entities;
    }

    public void moveRiders(HashMap<Entity, AABB> riders) {
        riders.forEach((entity, box) -> {
            if (entity.getY() < box.minY) {
                return;
            }
            double thisFrameIntersectingY = entity.getY() - box.maxY;
            if (thisFrameIntersectingY < 0) {
                entity.setPos(entity.getX(), box.maxY + 1.0e-7, entity.getZ());
            }
            // If entity is moving downwards, next frame it will intersect the platform by this much
            double nextFrameIntersectingY = entity.getDeltaMovement().y;
            if (nextFrameIntersectingY < 0) {
                entity.push(0, -nextFrameIntersectingY, 0);
            }
            entity.push(0, getSpeed() - entity.getDeltaMovement().y, 0);
            entity.setOnGround(true);
        });
    }


    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    private void reconstruct(ServerLevel serverLevel) {
        BlockPos pos = getPulleyPos().below(getPulleyPos().getY() - getTargetY());

        int retries = 0;
        Map<Vec3i, BlockState> blocks = getBlockMap();
        Map<Vec3i, CompoundTag> blockEntities = getBlockEntityNbtMap();
        while (!blocks.isEmpty() && retries < 10) {
            retries++;
            int skipped = 0;
            for (Map.Entry<Vec3i, BlockState> entry : blocks.entrySet()) {
                BlockPos blockPos = pos.offset(entry.getKey());
                BlockState state = entry.getValue();
                if (state.canSurvive(level(), blockPos)) {
                    if (serverLevel.setBlock(blockPos, state, Block.UPDATE_ALL)) {
                        serverLevel.getChunkSource().sendToTrackingPlayers(this, new ClientboundBlockUpdatePacket(blockPos, serverLevel.getBlockState(blockPos)));
                    }
                    if (blockEntities.containsKey(entry.getKey())) {
                        BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
                        if (blockEntity != null) {
                            CompoundTag tag = blockEntities.get(entry.getKey());
                            try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
                                ValueInput valueInput = TagValueInput.create(scopedCollector, registryAccess(), tag);
                                blockEntity.loadWithComponents(valueInput);
                            }
                            ((MovableBlockEntityMixin) blockEntity).setPos(blockPos);
                        }
                    }
                    blocks.remove(entry.getKey());
                    blockEntities.remove(entry.getKey());
                    skipped = 0;
                    break;
                }
                skipped++;
            }
            if (skipped == 0) {
                retries = 0;
            }
        }

        if (retries > 0) {
            blocks.forEach((blockPos, state) -> this.spawnAtLocation(serverLevel, state.getBlock()));
        }
        setBlockMap(blocks);
    }

    private boolean done() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        BlockEntity blockEntity = serverLevel.getBlockEntity(getPulleyPos());
        if (blockEntity instanceof PulleyBlockEntity pulleyBlockEntity) {
            if (!pulleyBlockEntity.onJobCompleted(serverLevel, getPulleyPos(), serverLevel.getBlockState(getPulleyPos()), isMovingUp(), getTargetY())) {
                reconstruct(serverLevel);
                return true;
            }
            return false;
        }
        // The block entity has been lost, abort
        reconstruct(serverLevel);
        this.discard();
        return true;
    }

    public Map<Vec3i, BlockState> addBlock(Vec3i offset, Level level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        Map<Vec3i, BlockState> blocks = getBlockMap();
        Map<Vec3i, CompoundTag> blockEntities = getBlockEntityNbtMap();
        blocks.put(offset, state);
        if (blockEntity != null) {
            CompoundTag tag = blockEntity.saveWithFullMetadata(registryAccess());
            blockEntities.put(offset, tag);
            level.removeBlockEntity(pos);
            setBlockEntityNbtMap(blockEntities);
        }
        if (!level.isClientSide()) {
            setBlockMap(blocks);
        }
        return blocks;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return false;
    }

    public boolean isPathBlocked() {
        HashSet<BlockPos> blocked = new HashSet<>();
        getBlockMap().forEach((offset, state) -> {
            if (state.is(BwtBlocks.anchorBlock) && (!blocked.isEmpty() || isMovingUp())) {
                return;
            }
            BlockPos pos = this.getPulleyPos().below(this.getPulleyPos().getY() - getTargetY()).offset(offset);
            pos = isMovingUp() ? pos.above() : pos.below();

            BlockState placementState = level().getBlockState(pos);

            if (!(placementState.is(BlockTags.AIR) || placementState.canBeReplaced())) {
                blocked.add(pos);
            }
        });
        return !blocked.isEmpty();
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float f) {
        if (!this.isInvulnerableToBase(damageSource)) {
            this.markHurt();
        }

        return false;
    }
}
