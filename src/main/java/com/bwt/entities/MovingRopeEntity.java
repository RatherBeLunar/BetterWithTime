package com.bwt.entities;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.pulley.PulleyBlockEntity;
import com.bwt.mixin.MovableBlockEntityMixin;
import com.bwt.utils.TrackedDataHandlers;
import com.bwt.utils.VoxelShapedEntity;
import com.bwt.utils.rectangular_entity.EntityRectDimensions;
import com.bwt.utils.rectangular_entity.RectangularEntity;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.*;

public class MovingRopeEntity extends RectangularEntity implements VoxelShapedEntity {
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
        this.noCulling = true;
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
    protected AABB makeBoundingBox() {
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
        ).move(position()).move(-0.5, 0, -0.5);
    }


    @Override
    public double getEyeY() {
        return -1;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        setPulleyPos(new BlockPos(compound.getInt("PulleyX"), compound.getInt("PulleyY"), compound.getInt("PulleyZ")));
        setTargetY(compound.getInt("TargetY"));
        setIsMovingUp(compound.getBoolean("Up"));
        if (compound.contains("blocks", Tag.TAG_LIST)) {
            Map<Vec3i, BlockState> blocks = deserializeBlockMap(compound.getList("blocks", Tag.TAG_COMPOUND));
            setBlockMap(blocks);
            rebuildBlockBoundingBox();
        }
        if (compound.contains("blockEntities", Tag.TAG_LIST)) {
            setBlockEntityNbtMap(deserializeBlockEntities(compound.getList("blockEntities", Tag.TAG_COMPOUND)));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("PulleyX", getPulleyPos().getX());
        compound.putInt("PulleyY", getPulleyPos().getY());
        compound.putInt("PulleyZ", getPulleyPos().getZ());
        compound.putInt("TargetY", getTargetY());
        compound.putBoolean("Up", isMovingUp());
        compound.put("blocks", serializeBlockMap());
        compound.put("blockEntities", serializeBlockEntities());
    }

    private ListTag serializeBlockEntities() {
        ListTag entries = new ListTag();
        getBlockEntityNbtMap().forEach((offset, blockEntity) -> {
            CompoundTag entry = new CompoundTag();
            entry.putLong("offset", new BlockPos(offset).asLong());
            entry.put("blockEntity", blockEntity);
            entries.add(entry);
        });
        return entries;
    }

    private Map<Vec3i, CompoundTag> deserializeBlockEntities(ListTag blockEntities) {
        Map<Vec3i, CompoundTag> map = new HashMap<>();
        for (Tag entry : blockEntities) {
            CompoundTag compound = (CompoundTag) entry;
            map.put(
                    BlockPos.of(compound.getLong("offset")),
                    compound.getCompound("blockEntity")
            );
        }
        return map;
    }

    private ListTag serializeBlockMap() {
        ListTag entries = new ListTag();
        for (Map.Entry<Vec3i, BlockState> entry : getBlockMap().entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            CompoundTag listEntry = new CompoundTag();
            listEntry.putLong("pos", new BlockPos(entry.getKey()).asLong());
            listEntry.put("state", NbtUtils.writeBlockState(entry.getValue()));
            entries.add(listEntry);
        }
        return entries;
    }

    private Map<Vec3i, BlockState> deserializeBlockMap(ListTag blocks) {
        Map<Vec3i, BlockState> map = new HashMap<>();
        for (Tag entry : blocks) {
            CompoundTag entryCompound = ((CompoundTag) entry);
            Vec3i pos = BlockPos.of(entryCompound.getLong("pos"));
            BlockState state = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), entryCompound.getCompound("state"));
            map.put(pos, state);
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

    private void reconstruct() {
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
                    if (level().setBlock(blockPos, state, Block.UPDATE_ALL)) {
                        ((ServerLevel) this.level()).getChunkSource().broadcast(this, new ClientboundBlockUpdatePacket(blockPos, this.level().getBlockState(blockPos)));
                    }
                    if (blockEntities.containsKey(entry.getKey())) {
                        BlockEntity blockEntity = level().getBlockEntity(blockPos);
                        if (blockEntity != null) {
                            CompoundTag tag = blockEntities.get(entry.getKey());
                            blockEntity.loadWithComponents(tag, registryAccess());
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
            blocks.forEach((blockPos, state) -> this.spawnAtLocation(state.getBlock()));
        }
        setBlockMap(blocks);
    }

    private boolean done() {
        if (level().isClientSide) {
            return false;
        }
        BlockEntity blockEntity = level().getBlockEntity(getPulleyPos());
        if (blockEntity instanceof PulleyBlockEntity pulleyBlockEntity) {
            if (!pulleyBlockEntity.onJobCompleted(level(), getPulleyPos(), level().getBlockState(getPulleyPos()), isMovingUp(), getTargetY())) {
                reconstruct();
                return true;
            }
            return false;
        }
        // The block entity has been lost, abort
        reconstruct();
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
        if (!level.isClientSide) {
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

            BlockState placementState = getCommandSenderWorld().getBlockState(pos);

            if (!(placementState.is(BlockTags.AIR) || placementState.canBeReplaced())) {
                blocked.add(pos);
            }
        });
        return !blocked.isEmpty();
    }
}
