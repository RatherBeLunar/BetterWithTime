package com.bwt.blocks;

import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class BellowsBlock extends Block implements MechPowerBlockBase {
    public static DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final float compressedHeight = 11;
    protected static final int tickRate = 37;

    protected static final VoxelShape COMPRESSED_SHAPE = Block.createCuboidShape(0f, 0f, 0f, 16f, compressedHeight, 16f);

    public BellowsBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(MECH_POWERED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return state.get(MECH_POWERED) ? COMPRESSED_SHAPE : VoxelShapes.fullCube();
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MECH_POWERED, FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public List<BlockPos> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return Arrays.stream(Direction.values())
                .filter(direction -> direction != blockState.get(FACING) && direction != Direction.UP)
                .map(pos::offset)
                .toList();
    }

    @Override
    public List<BlockPos> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return Arrays.stream(Direction.values()).filter(direction -> direction.getAxis().isHorizontal()).map(pos::offset).toList();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        schedulePowerUpdate(state, world, pos);
    }

    public void schedulePowerUpdate(BlockState state, World world, BlockPos pos) {
        if (isReceivingMechPower(world, state, pos) != isMechPowered(state)) {
            world.scheduleBlockTick(pos, this, tickRate);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        schedulePowerUpdate(state, world, pos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean isReceivingMechPower = isReceivingMechPower(world, state, pos);
        if (isReceivingMechPower == isMechPowered(state)) {
            return;
        }
        world.setBlockState(pos, state.with(MECH_POWERED, isReceivingMechPower));
        world.playSound(null, pos, BwtSoundEvents.BELLOWS_COMPRESS, SoundCategory.BLOCKS, 0.25f, random.nextFloat() * 0.1f + 0.2f);
        if (isReceivingMechPower) {
            stokeFire(world, pos, state);
        }
    }

    public void stokeFire(World world, BlockPos pos, BlockState state) {
        BlockPos center = pos.offset(state.get(FACING), 2);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos firePos = center.offset(Direction.Axis.X, x).offset(Direction.Axis.Z, z);
                BlockPos hibachiPos = firePos.down();
                BlockState fireState = world.getBlockState(firePos);
//                if (!fireState.isIn(BlockTags.FIRE)) {
//                    continue;
//                }
                BlockState hibachiState = world.getBlockState(hibachiPos);
                if (!hibachiState.isOf(BwtBlocks.hibachiBlock)) {
                    continue;
                }
                world.setBlockState(firePos, BwtBlocks.stokedFireBlock.getDefaultState(), Block.NOTIFY_ALL);
            }
        }
    }
}
