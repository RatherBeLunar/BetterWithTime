package com.bwt.blocks;

import com.bwt.items.BwtItems;
import com.bwt.mechanical.api.MechPowered;
import com.bwt.mechanical.api.NodeProvider;
import com.bwt.mechanical.api.digraph.Node;
import com.bwt.mechanical.impl.Gearbox;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GearBoxBlock extends SimpleFacingBlock implements RotateWithEmptyHand, NodeProvider {

    public static final int turnOnTickRate = 10;
    public static final int turnOffTickRate = 9;

    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    public static final BooleanProperty UP = ConnectingBlock.UP;
    public static final BooleanProperty DOWN = ConnectingBlock.DOWN;

    public static final BooleanProperty POWERED = Properties.POWERED;

    private final Gearbox gearbox = new Gearbox() {
        @Override
        public List<Direction> getInputFaces(World world, BlockPos pos, BlockState blockState) {
            return List.of(blockState.get(FACING));
        }
    };

    public GearBoxBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(MechPowered.MECH_POWERED, false).with(POWERED, false));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        MechPowered.appendProperties(builder);
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, POWERED);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if (this.gearbox.isPowered(state)) {
            emitGearBoxParticles(world, pos, random);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockState updatedState = onUseRotate(state, world, pos, player);
        if (updatedState == state) {
            return ActionResult.PASS;
        }
        //TODO  Prevent exploits by turning power off and wait for scheduled reload of power state
        updatedState = updatedState.with(MechPowered.MECH_POWERED, false);
        world.setBlockState(pos, updatedState);
        schedulePowerUpdate(updatedState, world, pos);
        return ActionResult.SUCCESS;
    }

    @Override
    public @NotNull BlockState getPlacementState(ItemPlacementContext ctx) {
        return withConnectionProperties(super.getPlacementState(ctx), ctx.getWorld(), ctx.getBlockPos());
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        schedulePowerUpdate(state, world, pos);
    }

    public static BlockState withConnectionProperties(BlockState state, BlockView world, BlockPos pos) {
        BlockState downBlock = world.getBlockState(pos.down());
        BlockState upBlock = world.getBlockState(pos.up());
        BlockState northBlock = world.getBlockState(pos.north());
        BlockState eastBlock = world.getBlockState(pos.east());
        BlockState southBlock = world.getBlockState(pos.south());
        BlockState westBlock = world.getBlockState(pos.west());
        return state
                .withIfExists(NORTH, (northBlock.isOf(BwtBlocks.axleBlock) || northBlock.isOf(BwtBlocks.axlePowerSourceBlock))
                        && northBlock.get(AxleBlock.AXIS).equals(Direction.Axis.Z))
                .withIfExists(EAST, (eastBlock.isOf(BwtBlocks.axleBlock) || eastBlock.isOf(BwtBlocks.axlePowerSourceBlock))
                        && eastBlock.get(AxleBlock.AXIS).equals(Direction.Axis.X))
                .withIfExists(SOUTH, (southBlock.isOf(BwtBlocks.axleBlock) || southBlock.isOf(BwtBlocks.axlePowerSourceBlock))
                        && southBlock.get(AxleBlock.AXIS).equals(Direction.Axis.Z))
                .withIfExists(WEST, (westBlock.isOf(BwtBlocks.axleBlock) || westBlock.isOf(BwtBlocks.axlePowerSourceBlock))
                        && westBlock.get(AxleBlock.AXIS).equals(Direction.Axis.X))
                .withIfExists(UP, (upBlock.isOf(BwtBlocks.axleBlock) || upBlock.isOf(BwtBlocks.axlePowerSourceBlock))
                        && upBlock.get(AxleBlock.AXIS).isVertical())
                .withIfExists(DOWN, (downBlock.isOf(BwtBlocks.axleBlock) || downBlock.isOf(BwtBlocks.axlePowerSourceBlock))
                        && downBlock.get(AxleBlock.AXIS).isVertical());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        BlockState updatedState = state;

        if (pos.north().equals(neighborPos)) {
            return updatedState.withIfExists(NORTH, (neighborState.isOf(BwtBlocks.axleBlock) || neighborState.isOf(BwtBlocks.axlePowerSourceBlock)) && neighborState.get(AxleBlock.AXIS).equals(Direction.Axis.Z));
        }
        if (pos.east().equals(neighborPos)) {
            return updatedState.withIfExists(EAST, (neighborState.isOf(BwtBlocks.axleBlock) || neighborState.isOf(BwtBlocks.axlePowerSourceBlock)) && neighborState.get(AxleBlock.AXIS).equals(Direction.Axis.X));
        }
        if (pos.south().equals(neighborPos)) {
            return updatedState.withIfExists(SOUTH, (neighborState.isOf(BwtBlocks.axleBlock) || neighborState.isOf(BwtBlocks.axlePowerSourceBlock)) && neighborState.get(AxleBlock.AXIS).equals(Direction.Axis.Z));
        }
        if (pos.west().equals(neighborPos)) {
            return updatedState.withIfExists(WEST, (neighborState.isOf(BwtBlocks.axleBlock) || neighborState.isOf(BwtBlocks.axlePowerSourceBlock)) && neighborState.get(AxleBlock.AXIS).equals(Direction.Axis.X));
        }
        if (pos.up().equals(neighborPos)) {
            return updatedState.withIfExists(UP, (neighborState.isOf(BwtBlocks.axleBlock) || neighborState.isOf(BwtBlocks.axlePowerSourceBlock)) && neighborState.get(AxleBlock.AXIS).isVertical());
        }
        if (pos.down().equals(neighborPos)) {
            return updatedState.withIfExists(DOWN, (neighborState.isOf(BwtBlocks.axleBlock) || neighborState.isOf(BwtBlocks.axlePowerSourceBlock)) && neighborState.get(AxleBlock.AXIS).isVertical());
        }
        return updatedState;
    }

    public BlockState getPowerStates(BlockState state, World world, BlockPos pos) {
        boolean redstonePowered = world.isReceivingRedstonePower(pos);
        boolean mechPowered = this.gearbox.isReceivingInput(world, state, pos);
        BlockState updatedState = state;
        updatedState = updatedState.with(POWERED, redstonePowered);
        updatedState = updatedState.with(MechPowered.MECH_POWERED, mechPowered);
        return updatedState;
    }

    public void schedulePowerUpdate(BlockState state, World world, BlockPos pos) {
        //Compute new state but don 't update yet
        BlockState newState = getPowerStates(state, world, pos);
        //If block just turned on

        if (this.gearbox.isPowered(newState) && !this.gearbox.isPowered(state)) {
            world.scheduleBlockTick(pos, this, turnOnTickRate);
        } else if (!this.gearbox.isPowered(newState) && this.gearbox.isPowered(state)) {
            world.scheduleBlockTick(pos, this, turnOffTickRate);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        schedulePowerUpdate(state, world, pos);
    }

    public void updatePowerTransfer(World world, BlockState blockState, BlockPos pos) {
        BlockState updatedState = getPowerStates(blockState, world, pos);
        if (this.gearbox.isPowered(updatedState)) {
            this.playMechSound(world, pos);
        }
        world.setBlockState(pos, updatedState);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.updatePowerTransfer(world, state, pos);
    }

    private void playMechSound(World world, BlockPos pos) {
        world.playSound(null, pos, BwtSoundEvents.GEAR_BOX_ACTIVATE,
                SoundCategory.BLOCKS, 0.25f, world.random.nextFloat() * 0.25F + 0.25F);
//TODO         world.addSyncedBlockEvent(pos, this, 0, 0);
//TODO         world.emitGameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
    }

    public static void breakGearBox(World world, BlockPos pos) {
        world.removeBlock(pos, false);
        world.playSound(null, pos, BwtSoundEvents.MECH_EXPLODE, SoundCategory.BLOCKS, 0.5f, 1);
        dropStack(world, pos, Items.STICK.getDefaultStack());
        dropStack(world, pos, BwtItems.sawDustItem.getDefaultStack());
        dropStack(world, pos, BwtItems.gearItem.getDefaultStack());
    }

    private void emitGearBoxParticles(World world, BlockPos pos, Random random) {
        for (int iTempCount = 0; iTempCount < 5; iTempCount++) {
            float smokeX = (float) pos.getX() + random.nextFloat();
            float smokeY = (float) pos.getY() + random.nextFloat() * 0.5F + 1.0F;
            float smokeZ = (float) pos.getZ() + random.nextFloat();
            world.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0D, 0D, 0D);
        }
    }


    @Override
    public Node getNode() {
        return gearbox;
    }
}
