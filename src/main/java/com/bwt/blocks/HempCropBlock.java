package com.bwt.blocks;

import com.bwt.items.BwtItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
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
    public static final int MAX_AGE = 8;
    public static final IntProperty AGE = IntProperty.of("age", 0, MAX_AGE);

    public HempCropBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected IntProperty getAgeProperty() {
        return AGE;
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
        int age = Math.min(state.get(AGE), 7);

        double height = (age + 1) / 8d;
        double halfWidth = 0.2f;

        return VoxelShapes.cuboid(
                0.5D - halfWidth, 0D, 0.5D - halfWidth,
                0.5D + halfWidth, height, 0.5D + halfWidth
        );
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isOf(this) || super.canPlaceAt(state, world, pos);
    }

    @Override
    protected int getGrowthAmount(World world) {
        return 1;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockState upState = world.getBlockState(pos.up());
        BlockState up2State = world.getBlockState(pos.up(2));;

        if (
            world.isSkyVisible(pos)
            || (upState.isOf(BwtBlocks.lightBlockBlock) && upState.get(LightBlock.LIT))
            || (up2State.isOf(BwtBlocks.lightBlockBlock) && up2State.get(LightBlock.LIT))
        ) {
            if (world.getBaseLightLevel(pos, 0) >= 9
                && (this.getAge(state)) < this.getMaxAge()
                && random.nextInt((int)(25.0f / (CropBlock.getAvailableMoisture(this, world, pos))) + 1) == 0
            ) {
                applyGrowth(world, pos, state);
            }
        }
    }

    @Override
    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        int currentAge = getAge(state);
        int newAge = currentAge + getGrowthAmount(world);
        int maxAge = getMaxAge();
        if (newAge > maxAge - 1) {
            newAge = maxAge - 1;
            if (world.getBlockState(pos.up()).isAir()) {
                world.setBlockState(pos.up(), getDefaultState().with(AGE, maxAge));
            }
        }
        if (newAge != currentAge) {
            world.setBlockState(pos, this.withAge(newAge), Block.NOTIFY_ALL);
        }
    }
}
