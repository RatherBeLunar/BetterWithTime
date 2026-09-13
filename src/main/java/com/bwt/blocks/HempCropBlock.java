package com.bwt.blocks;

import com.bwt.items.BwtItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HempCropBlock extends CropBlock {
    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D)
    };
    public static final BooleanProperty CONNECTED_UP = BooleanProperty.create("connected_up");

    public HempCropBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(CONNECTED_UP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTED_UP);
    }

    @Override
    public ItemLike getBaseSeedId() {
        return BwtItems.hempSeedsItem;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int age = state.getValue(AGE);
        boolean connectedUp = state.getValue(CONNECTED_UP);

        double height = (age + 1) / 8d;
        double halfWidth = 0.2f;

        if (age == getMaxAge() && !connectedUp) {
            height -= 2 / 16d;
        }

        return Shapes.box(
                0.5D - halfWidth, 0D, 0.5D - halfWidth,
                0.5D + halfWidth, height, 0.5D + halfWidth
        );
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos) && !level.getBlockState(pos.below()).is(this)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (neighborPos.equals(pos.above())) {
            return state.setValue(CONNECTED_UP, neighborState.is(this));
        }
        return state;
    }

    @Override
    protected int getBonemealAgeIncrease(Level level) {
        return 1;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState upState = level.getBlockState(pos.above());
        BlockState up2State = level.getBlockState(pos.above(2));

        if (random.nextInt((int)(25.0f / (CropBlock.getGrowthSpeed(this, level, pos))) + 1) != 0) {
            return;
        }
        if (!isBonemealSuccess(level, random, pos, state)) {
            return;
        }

        if (
                (level.canSeeSky(pos)
                || (upState.is(BwtBlocks.lightBlockBlock) && upState.getValue(LightBlock.LIT))
                || (up2State.is(BwtBlocks.lightBlockBlock) && up2State.getValue(LightBlock.LIT)))
                && level.getRawBrightness(pos, 0) >= 9
        ) {
            performBonemeal(level, random, pos, state);
        }
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return !level.getBlockState(pos.below()).is(this) && level.getBlockState(pos.above()).is(BlockTags.AIR);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (isBonemealSuccess(level, random, pos, state)) {
            growCrops(level, pos, state);
        }
    }

    @Override
    public void growCrops(Level level, BlockPos pos, BlockState state) {
        int currentAge = getAge(state);
        int newAge = currentAge + getBonemealAgeIncrease(level);
        int maxAge = getMaxAge();
        if (newAge > maxAge) {
            newAge = maxAge;
            if (level.getBlockState(pos.above()).is(BlockTags.AIR)) {
                level.setBlockAndUpdate(pos.above(), defaultBlockState().setValue(AGE, maxAge));
            }
        }
        if (newAge != currentAge) {
            level.setBlock(pos, this.getStateForAge(newAge), Block.UPDATE_ALL);
        }
    }
}
