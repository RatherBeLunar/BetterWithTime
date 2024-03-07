package com.bwt.entities;

import com.bwt.blocks.AnchorBlock;
import com.bwt.blocks.pulley.PulleyBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class MovingAnchorEntity extends Entity {
    public static final float width = 0.98F;
    public static final float height = 1.98F;
    static final public float movementSpeed = 1.0F / 20.0F;

    protected static final TrackedData<Float> yMotion = DataTracker.registerData(MovingAnchorEntity.class, TrackedDataHandlerRegistry.FLOAT);

    static final private int m_iVehicleSpawnPacketType = 102;

    protected BlockPos pulleyPos;
    protected int pulleyRopeStateUpdateCounter;
    protected int oldBottomY;

    public MovingAnchorEntity(EntityType<? extends MovingAnchorEntity> entityType, World world) {
        super(entityType, world);
    }

    public MovingAnchorEntity(World world, BlockPos pulleyPos, BlockPos sourcePos, int movementDirection) {
        this(BwtEntities.movingAnchorEntity, world);
        setPosition(sourcePos.getX(), sourcePos.getY(), sourcePos.getZ());

        this.pulleyPos = pulleyPos;
        float yOffset = 0.5f;
        pulleyRopeStateUpdateCounter = -1;

        int x = sourcePos.getX();
        int y = sourcePos.getY();
        int z = sourcePos.getZ();

        setPosition(x, y, z);
        this.lastRenderX = this.prevX = x;
        this.lastRenderY = this.prevY = y;
        this.lastRenderZ = this.prevZ = z;

        oldBottomY = MathHelper.floor(y -  yOffset);

        if (movementDirection > 0 )
        {
            this.setVelocity(0, movementSpeed, 0);
        }
        else
        {
            this.setVelocity(0, -movementSpeed, 0);
        }

        BlockEntity blockEntity = world.getBlockEntity(pulleyPos);
        if (blockEntity instanceof PulleyBlockEntity pulleyBlockEntity) {
            pulleyRopeStateUpdateCounter = pulleyBlockEntity.updateRopeStateCounter;
        }
    }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(yMotion, 0f);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("pulleyPosX", pulleyPos.getX());
        nbt.putInt("pulleyPosY", pulleyPos.getY());
        nbt.putInt("pulleyPosZ", pulleyPos.getZ());
        nbt.putInt("pulleyRopeStateUpdateCounter", pulleyRopeStateUpdateCounter);
        nbt.putInt("oldBottomY", oldBottomY);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        int pulleyPosX = nbt.getInt("pulleyPosX");
        int pulleyPosY = nbt.getInt("pulleyPosY");
        int pulleyPosZ = nbt.getInt("pulleyPosZ");
        pulleyPos = new BlockPos(pulleyPosX, pulleyPosY, pulleyPosZ);
        pulleyRopeStateUpdateCounter = nbt.getInt("pulleyRopeStateUpdateCounter");
        oldBottomY = nbt.getInt("oldBottomY");
    }

    @Override
    protected Box calculateBoundingBox() {
        return AnchorBlock.baseBox.offset(getPos());
    }

    @Override
    public void tick() {
        super.tick();
        World world = getWorld();
    }

    public void pushEntities(World world) {
        Box box = calculateBoundingBox();
        List<Entity> list = world.getOtherEntities(null, box);
        if (list.isEmpty()) {
            return;
        }
        for (Entity entity : list) {
            if(entity.isPushable() || (entity instanceof ItemEntity) || (entity instanceof ExperienceOrbEntity)) {
                entity.move(MovementType.PISTON, getVelocity());
            }
            else if (entity.isAlive() && entity instanceof HorizontalMechPowerSourceEntity horizontalMechPowerSourceEntity) {
                horizontalMechPowerSourceEntity.destroyWithDrop();
            }
        }
    }

    private double GetCorseYMotion()
    {
        return dataTracker.get(yMotion);
    }

    private void SetCorseYMotion( double yMotion )
    {
        dataTracker.set(MovingAnchorEntity.yMotion, ((float) yMotion));
    }
}
