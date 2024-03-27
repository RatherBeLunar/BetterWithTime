package com.bwt.blocks;

import com.bwt.items.BwtItems;
import net.minecraft.block.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class HempCropBlock extends CropBlock {
    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D)
    };
    public static final BooleanProperty CONNECTED_UP = BooleanProperty.of("connected_up");

    public HempCropBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(CONNECTED_UP, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(CONNECTED_UP);
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    public ItemConvertible getSeedsItem() {
        return BwtItems.hempSeedsItem;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int age = state.get(AGE);

        double height = (age + 1) / 8d;
        double halfWidth = 0.2f;

        return VoxelShapes.cuboid(
                0.5D - halfWidth, 0D, 0.5D - halfWidth,
                0.5D + halfWidth, height, 0.5D + halfWidth
        );
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        // This does not account for planting on itself, so we can't place seeds on top directly
        return super.canPlantOnTop(floor, world, pos);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos) && !world.getBlockState(pos.down()).isOf(this)) {
            return Blocks.AIR.getDefaultState();
        }
        if (neighborPos.equals(pos.up())) {
            return state.with(CONNECTED_UP, neighborState.isOf(this));
        }
        return state;
    }

    @Override
    protected int getGrowthAmount(World world) {
        return 1;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockState upState = world.getBlockState(pos.up());
        BlockState up2State = world.getBlockState(pos.up(2));

        if (random.nextInt((int)(25.0f / (CropBlock.getAvailableMoisture(this, world, pos))) + 1) != 0) {
            return;
        }
        if (!canGrow(world, random, pos, state)) {
            return;
        }

        if (
                (world.isSkyVisible(pos)
                || (upState.isOf(BwtBlocks.lightBlockBlock) && upState.get(LightBlock.LIT))
                || (up2State.isOf(BwtBlocks.lightBlockBlock) && up2State.get(LightBlock.LIT)))
                && world.getBaseLightLevel(pos, 0) >= 9
        ) {
            grow(world, random, pos, state);
        }
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return !world.getBlockState(pos.down()).isOf(this) && world.getBlockState(pos.up()).isAir();
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return !isMature(state) || world.getBlockState(pos.up()).isAir();
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        if (canGrow(world, random, pos, state)) {
            applyGrowth(world, pos, state);
        }
    }

    @Override
    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        int currentAge = getAge(state);
        int newAge = currentAge + getGrowthAmount(world);
        int maxAge = getMaxAge();
        if (newAge > maxAge) {
            newAge = maxAge;
            if (world.getBlockState(pos.up()).isAir()) {
                world.setBlockState(pos.up(), getDefaultState().with(AGE, maxAge));
            }
        }
        if (newAge != currentAge) {
            world.setBlockState(pos, this.withAge(newAge), Block.NOTIFY_ALL);
        }
    }
}
