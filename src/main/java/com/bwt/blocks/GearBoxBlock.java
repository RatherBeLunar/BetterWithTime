package com.bwt.blocks;

import com.bwt.blocks.axles.AxleBlock;
import com.bwt.items.BwtItems;
import com.bwt.sounds.BwtSoundEvents;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class GearBoxBlock extends SimpleFacingBlock implements MechPowerBlockBase, RotateWithEmptyHand {
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;


    public GearBoxBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(MECH_POWERED, false));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        MechPowerBlockBase.super.appendProperties(builder);
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public Predicate<Direction> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return direction -> direction == blockState.getValue(FACING);
    }

    @Override
    public Predicate<Direction> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return direction -> false;
    }

    @Override
    public boolean isMechPowered(BlockState blockState) {
        return MechPowerBlockBase.super.isMechPowered(blockState);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (isMechPowered(state)) {
            emitGearBoxParticles(level, pos, random);
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockState updatedState = onUseRotate(state, level, pos, player);
        if (updatedState == state) {
            return InteractionResult.PASS;
        }
        // Prevent exploits by turning power off and wait for scheduled reload of power state
        updatedState = updatedState.setValue(MECH_POWERED, false);
        level.setBlockAndUpdate(pos, updatedState);
        schedulePowerUpdate(updatedState, level, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return withConnectionProperties(super.getStateForPlacement(ctx), ctx.getLevel(), ctx.getClickedPos());
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        schedulePowerUpdate(state, level, pos);
    }

    public static BlockState withConnectionProperties(BlockState state, BlockGetter level, BlockPos pos) {
        BlockState downBlock = level.getBlockState(pos.below());
        BlockState upBlock = level.getBlockState(pos.above());
        BlockState northBlock = level.getBlockState(pos.north());
        BlockState eastBlock = level.getBlockState(pos.east());
        BlockState southBlock = level.getBlockState(pos.south());
        BlockState westBlock= level.getBlockState(pos.west());
        return state
                .trySetValue(NORTH, (northBlock.is(BwtBlocks.axleBlock) || northBlock.is(BwtBlocks.axlePowerSourceBlock))
                        && northBlock.getValue(AxleBlock.AXIS).equals(Direction.Axis.Z))
                .trySetValue(EAST, (eastBlock.is(BwtBlocks.axleBlock) || eastBlock.is(BwtBlocks.axlePowerSourceBlock))
                        && eastBlock.getValue(AxleBlock.AXIS).equals(Direction.Axis.X))
                .trySetValue(SOUTH, (southBlock.is(BwtBlocks.axleBlock) || southBlock.is(BwtBlocks.axlePowerSourceBlock))
                        && southBlock.getValue(AxleBlock.AXIS).equals(Direction.Axis.Z))
                .trySetValue(WEST, (westBlock.is(BwtBlocks.axleBlock) || westBlock.is(BwtBlocks.axlePowerSourceBlock))
                        && westBlock.getValue(AxleBlock.AXIS).equals(Direction.Axis.X))
                .trySetValue(UP, (upBlock.is(BwtBlocks.axleBlock) || upBlock.is(BwtBlocks.axlePowerSourceBlock))
                        && upBlock.getValue(AxleBlock.AXIS).isVertical())
                .trySetValue(DOWN, (downBlock.is(BwtBlocks.axleBlock) || downBlock.is(BwtBlocks.axlePowerSourceBlock))
                        && downBlock.getValue(AxleBlock.AXIS).isVertical());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (pos.north().equals(neighborPos)) {
            return state.trySetValue(NORTH, (neighborState.is(BwtBlocks.axleBlock) || neighborState.is(BwtBlocks.axlePowerSourceBlock)) && neighborState.getValue(AxleBlock.AXIS).equals(Direction.Axis.Z));
        }
        if (pos.east().equals(neighborPos)) {
            return state.trySetValue(EAST, (neighborState.is(BwtBlocks.axleBlock) || neighborState.is(BwtBlocks.axlePowerSourceBlock)) && neighborState.getValue(AxleBlock.AXIS).equals(Direction.Axis.X));
        }
        if (pos.south().equals(neighborPos)) {
            return state.trySetValue(SOUTH, (neighborState.is(BwtBlocks.axleBlock) || neighborState.is(BwtBlocks.axlePowerSourceBlock)) &&neighborState.getValue(AxleBlock.AXIS).equals(Direction.Axis.Z));
        }
        if (pos.west().equals(neighborPos)) {
            return state.trySetValue(WEST, (neighborState.is(BwtBlocks.axleBlock) || neighborState.is(BwtBlocks.axlePowerSourceBlock)) &&neighborState.getValue(AxleBlock.AXIS).equals(Direction.Axis.X));
        }
        if (pos.above().equals(neighborPos)) {
            return state.trySetValue(UP, (neighborState.is(BwtBlocks.axleBlock) || neighborState.is(BwtBlocks.axlePowerSourceBlock)) &&neighborState.getValue(AxleBlock.AXIS).isVertical());
        }
        if (pos.below().equals(neighborPos)) {
            return state.trySetValue(DOWN, (neighborState.is(BwtBlocks.axleBlock) || neighborState.is(BwtBlocks.axlePowerSourceBlock)) &&neighborState.getValue(AxleBlock.AXIS).isVertical());
        }
        return state;
    }

    public BlockState getPowerStates(BlockState state, Level level, BlockPos pos) {
        return state.setValue(MECH_POWERED, isReceivingMechPower(level, state, pos));
    }

    public void schedulePowerUpdate(BlockState state, Level level, BlockPos pos) {
        // Compute new state but don't update yet
        BlockState newState = getPowerStates(state, level, pos);
        // If block just turned on
        if (isMechPowered(newState) && !isMechPowered(state)) {
            level.scheduleTick(pos, this, turnOnTickRate);
        }
        // If block just turned off
        else if (!isMechPowered(newState) && isMechPowered(state)) {
            level.scheduleTick(pos, this, turnOffTickRate);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (level.isClientSide) {
            return;
        }
        schedulePowerUpdate(state, level, pos);
    }

    public void updatePowerTransfer(Level level, BlockState blockState, BlockPos pos) {
        BlockState updatedState = getPowerStates(blockState, level, pos);
        if (isMechPowered(updatedState)) {
            this.playMechSound(level, pos);
        }
        level.setBlockAndUpdate(pos, updatedState);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.updatePowerTransfer(level, state, pos);
    }

    private void playMechSound(Level level, BlockPos pos) {
        level.playSound(null, pos, BwtSoundEvents.GEAR_BOX_ACTIVATE,
                SoundSource.BLOCKS, 0.25f, level.random.nextFloat() * 0.25F + 0.25F);
//        level.addSyncedBlockEvent(pos, this, 0, 0);
//        level.emitGameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
    }

    public static void breakGearBox(Level level, BlockPos pos) {
        level.removeBlock(pos, false);
        level.playSound(null, pos, BwtSoundEvents.MECH_EXPLODE, SoundSource.BLOCKS, 0.5f, 1);
        popResource(level, pos, Items.STICK.getDefaultInstance());
        popResource(level, pos, BwtItems.sawDustItem.getDefaultInstance());
        popResource(level, pos, BwtItems.gearItem.getDefaultInstance());
    }

    private void emitGearBoxParticles(Level level, BlockPos pos, RandomSource random) {
        for ( int iTempCount = 0; iTempCount < 5; iTempCount++ )
        {
            float smokeX = (float)pos.getX() + random.nextFloat();
            float smokeY = (float)pos.getY() + random.nextFloat() * 0.5F + 1.0F;
            float smokeZ = (float)pos.getZ() + random.nextFloat();
            level.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0D, 0D, 0D );
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        state = super.rotate(state, rotation);
        return switch (rotation) {
            case CLOCKWISE_180 -> state.setValue(NORTH, state.getValue(SOUTH))
                    .setValue(EAST, state.getValue(WEST))
                    .setValue(SOUTH, state.getValue(NORTH))
                    .setValue(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90 -> state.setValue(NORTH, state.getValue(EAST))
                    .setValue(EAST, state.getValue(SOUTH))
                    .setValue(SOUTH, state.getValue(WEST))
                    .setValue(WEST, state.getValue(NORTH));
            case CLOCKWISE_90 -> state.setValue(NORTH, state.getValue(WEST))
                    .setValue(EAST, state.getValue(NORTH))
                    .setValue(SOUTH, state.getValue(EAST))
                    .setValue(WEST, state.getValue(SOUTH));
            default -> state;
        };
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        state = super.mirror(state, mirror);
        return switch (mirror) {
            case LEFT_RIGHT -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
            case FRONT_BACK -> state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
            default -> state;
        };
    }
}
