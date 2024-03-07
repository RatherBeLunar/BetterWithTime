package com.bwt.entities;

import com.bwt.blocks.AnchorBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.RopeBlock;
import com.bwt.blocks.pulley.PulleyBlockEntity;
import com.bwt.items.BwtItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.*;
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

    public interface Factory {
        MovingAnchorEntity create(World world, BlockPos pulleyPos, BlockPos sourcePos, int movementDirection);
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

        if (!isAlive()) {
            return;
        }
        if (world.isClient) {
            setVelocity(0, GetCorseYMotion(), 0);
        }

        int i = MathHelper.floor(getPos().x);
        int k = MathHelper.floor(getPos().z);

        // The following is an increased radius version of the test to see whether an entity should be updated in world.java.
        // By extending the range, it ensures that an anchor won't update unless all the platforms that may be attached to it
        // are capable of updating as well.  This prevents Anchor/Platform assemblies from being broken apart during chunk save/load

        int checkChunksRange = 35;
        if (!world.isRegionLoaded(i - checkChunksRange, world.getBottomY(), k - checkChunksRange, i + checkChunksRange, world.getTopY() - 1, k + checkChunksRange)) {
            return;
        }

        BlockState blockAboveState = world.getBlockState(new BlockPos(i, oldBottomY + 1, k));
        boolean bForceValidation = false;

        BlockState pulleyState = world.getBlockState(pulleyPos);
        BlockEntity blockEntity = world.getBlockEntity(pulleyPos);
        if (!(blockEntity instanceof PulleyBlockEntity pulleyBlockEntity)) {
            return;
        }

        if (!world.isClient) {
            BlockState block2AboveState = world.getBlockState(new BlockPos(i, oldBottomY + 2, k));

            boolean bPauseMotion = false;
            double yVelocity = getVelocity().getY();

            if (pulleyState.isOf(BwtBlocks.pulleyBlock)) {

                // check for broken rope
                if (blockAboveState.isOf(BwtBlocks.pulleyBlock)
                        || blockAboveState.isOf(BwtBlocks.ropeBlock)
                        || block2AboveState.isOf(BwtBlocks.pulleyBlock)
                        || block2AboveState.isOf(BwtBlocks.ropeBlock)) {
                    if (pulleyRopeStateUpdateCounter != pulleyBlockEntity.updateRopeStateCounter ) {
                        if (yVelocity > 0.0F) {
                            // moving upwards
                            if (pulleyBlockEntity.isLowering(pulleyState)) {
                                // if the pulley has switched direction, change motion to match immediately
                                setVelocity(0, -yVelocity, 0);
                                bForceValidation = true;
                            }
                        }
                        else {
                            // moving downwards
                            if (pulleyBlockEntity.isRaising(pulleyState)) {
                                // if the pulley has switched direction, change motion to match immediately
                                setVelocity(0, -yVelocity, 0);
                                bForceValidation = true;
                            }
                        }

                        pulleyRopeStateUpdateCounter = pulleyBlockEntity.updateRopeStateCounter;
                    }
                    else {
                        // the Pulley hasn't updated, perhaps due to chunk load. Pause the anchor's motion until it updates again.
                        return;
                    }
                }

                SetCorseYMotion(yVelocity);
            }

            if (yVelocity <= 0.01 && yVelocity >= -0.01) {
                // we've stopped for some reason. Convert
                convertToBlock(new BlockPos(i, oldBottomY, k));
                return;
            }
        }

        move(MovementType.SELF, getPos().add(getVelocity()));

        // 0.5 is yOffset
        int newBottomJ = MathHelper.floor(getPos().getY() - 0.5);

        pushEntities(world);

        if (!world.isClient) {
            double yVelocity = getVelocity().getY();
            if (oldBottomY != newBottomJ || bForceValidation) {
                BlockState targetState = world.getBlockState(new BlockPos(i, newBottomJ + 1, k));
                BlockState belowTargetState = world.getBlockState(new BlockPos(i, newBottomJ, k));
                if (yVelocity > 0.0F) {
                    // moving upwards
                    if (belowTargetState.isOf(BwtBlocks.ropeBlock)) {
                        pulleyBlockEntity.attemptToRetractRope(world);
                    }

                    if (!targetState.isOf(BwtBlocks.ropeBlock) || !pulleyBlockEntity.isRaising(pulleyState) || newBottomJ + 1 >= pulleyPos.getY()) {
                        // we've reached the top of our rope or the pulley is no longer raising.
                        // Stop our movement.
                        convertToBlock(new BlockPos(i, newBottomJ, k));
                        return;
                    }
                }
                else {
                    // moving downwards
                    int iRopeRequiredToDescend = 2;

                    if (blockAboveState.isOf(BwtBlocks.pulleyBlock) || blockAboveState.isOf(BwtBlocks.ropeBlock)) {
                        iRopeRequiredToDescend = 1;
                        BlockState oldBlockState = world.getBlockState(new BlockPos(i, oldBottomY, k));
                        if (oldBlockState.isOf(BwtBlocks.pulleyBlock) || oldBlockState.isOf(BwtBlocks.ropeBlock)) {
                            iRopeRequiredToDescend = 0;
                        }
                    }

                    boolean bEnoughRope = pulleyBlockEntity.hasRope(iRopeRequiredToDescend);
                    boolean bStop = false;

                    if (!pulleyBlockEntity.isLowering(pulleyState) || !bEnoughRope) {
                        bStop = true;
                    }
                    else if (belowTargetState.isReplaceable()) {
                        if (!targetState.isSolid() || targetState.isOf(Blocks.COBWEB)) {
                            // we've collided with a non-solid block.  Destroy it.

                            if (targetState.isOf(BwtBlocks.ropeBlock)) {
                                if (!returnRopeToPulley()) {
                                    ItemScatterer.spawn(world, i, newBottomJ, k, BwtItems.ropeItem.getDefaultStack());
                                }
                            }
                            else {
//                                world.playSound(this, i, newBottomJ, k, )
//                                worldObj.playAuxSFX( FCBetterThanWolves.m_iBlockDestroyRespectParticleSettingsAuxFXID,
//                                        i, newBottomJ, k, iTargetBlockID + ( iTargetMetadata << 12 ) );
                                ItemScatterer.spawn(world, i, newBottomJ, k, targetState.getBlock().asItem().getDefaultStack());
                            }

                            world.breakBlock(new BlockPos(i, newBottomJ, k), false);
                        }
                        else {
                            bStop = true;
                        }
                    }

                    if (bStop) {
                        convertToBlock(new BlockPos(i, oldBottomY, k));
                        return;
                    }

                    if (!targetState.isOf(BwtBlocks.ropeBlock) && !targetState.isOf(BwtBlocks.pulleyBlock)) {
                        // make sure the pulley fills in the last block above us with rope
                        pulleyBlockEntity.attemptToDispenseRope(world);
                    }
                }

                oldBottomY = newBottomJ;
            }
        }
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

    public void ForceStopByPlatform()
    {
        if ( isDead )
        {
            return;
        }

        if ( motionY > 0.0F )
        {
            // the pulley is ascending.  Break any rope attached above

            int i = MathHelper.floor_double( posX );
            int jAbove = MathHelper.floor_double( posY ) + 1;
            int k = MathHelper.floor_double( posZ );

            int iBlockAboveID  = worldObj.getBlockId( i, jAbove, k );

            if ( iBlockAboveID == FCBetterThanWolves.fcRopeBlock.blockID )
            {
                ( (FCBlockRope)(FCBetterThanWolves.fcRopeBlock) ).BreakRope( worldObj, i, jAbove, k );
            }
        }

        int i = MathHelper.floor_double( posX );
        int j = MathHelper.floor_double( posY );
        int k = MathHelper.floor_double( posZ );

        convertToBlock( i, j, k );
    }

    private void convertToBlock(BlockPos pos) {
        boolean bCanPlace = true;

        int iTargetBlockID = worldObj.getBlockId( i, j, k );

        if ( !FCUtilsWorld.IsReplaceableBlock( worldObj, i, j, k ) )
        {
            if ( iTargetBlockID == FCBetterThanWolves.fcRopeBlock.blockID )
            {
                // this shouldn't happen, but if there is a rope at the destination,
                // send it back to the pulley above

                if ( !returnRopeToPulley() )
                {
                    FCUtilsItem.EjectSingleItemWithRandomOffset( worldObj, i, j, k,
                            FCBetterThanWolves.fcItemRope.itemID, 0 );
                }
            }
            else if ( !Block.blocksList[iTargetBlockID].blockMaterial.isSolid() || iTargetBlockID == Block.web.blockID ||
                    iTargetBlockID == FCBetterThanWolves.fcBlockWeb.blockID )
            {
                int iTargetMetadata = worldObj.getBlockMetadata( i, j, k );

                Block.blocksList[iTargetBlockID].dropBlockAsItem(
                        worldObj, i, j, k, iTargetMetadata, 0 );

                worldObj.playAuxSFX( FCBetterThanWolves.m_iBlockDestroyRespectParticleSettingsAuxFXID,
                        i, j, k, iTargetBlockID + ( iTargetMetadata << 12 ) );
            }
            else
            {
                bCanPlace = false;
            }
        }

        if ( bCanPlace )
        {
            worldObj.setBlockWithNotify( i, j, k, FCBetterThanWolves.fcAnchor.blockID );

            ( (FCBlockAnchor)( FCBetterThanWolves.fcAnchor ) ).SetFacing( worldObj, i, j, k, 1 );
        }
        else
        {
            // this shouldn't usually happen, but if the block is already occupied, eject the anchor
            // as an item

            FCUtilsItem.EjectSingleItemWithRandomOffset( worldObj, i, j, k,
                    FCBetterThanWolves.fcAnchor.blockID, 0 );
        }

        NotifyAssociatedPulleyOfLossOfAnchorEntity();

        setDead();
    }

    public boolean returnRopeToPulley()
    {
        int associatedPulleyBlockID = worldObj.getBlockId( pulleyPos.i,
                pulleyPos.j, pulleyPos.k );

        if ( associatedPulleyBlockID == FCBetterThanWolves.fcPulley.blockID )
        {
            // FCTODO: Check for the continuity of the rope here

            FCTileEntityPulley tileEntityPulley =
                    (FCTileEntityPulley)worldObj.getBlockTileEntity( pulleyPos.i,
                            pulleyPos.j, pulleyPos.k );

            if ( tileEntityPulley != null )
            {
                tileEntityPulley.AddRopeToInventory();

                return true;
            }
        }

        return false;
    }

    private void NotifyAssociatedPulleyOfLossOfAnchorEntity()
    {
        int associatedPulleyBlockID = worldObj.getBlockId( pulleyPos.i,
                pulleyPos.j, pulleyPos.k );

        if ( associatedPulleyBlockID == FCBetterThanWolves.fcPulley.blockID )
        {
            FCTileEntityPulley tileEntityPulley =
                    (FCTileEntityPulley)worldObj.getBlockTileEntity( pulleyPos.i,
                            pulleyPos.j, pulleyPos.k );

            tileEntityPulley.NotifyOfLossOfAnchorEntity();
        }
    }

}
