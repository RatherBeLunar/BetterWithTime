package com.bwt.blocks;

import com.bwt.blocks.mining_charge.ICaughtFireBlock;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class StokedFireBlock extends BaseFireBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
    public static final BooleanProperty TWO_HIGH = BooleanProperty.create("two_high");
    public static final int tickRate = 42;

    public static final MapCodec<StokedFireBlock> CODEC = SoulFireBlock.simpleCodec(StokedFireBlock::new);

    public MapCodec<StokedFireBlock> codec() {
        return CODEC;
    }

    public StokedFireBlock(Properties settings) {
        super(settings, 4.0f);
        registerDefaultState(defaultBlockState().setValue(AGE, 0).setValue(TWO_HIGH, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, TWO_HIGH);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        return getPlacementState(ctx.getLevel(), ctx.getClickedPos(), state);
    }

    public BlockState getPlacementState(Level level, BlockPos pos) {
        return getPlacementState(level, pos, defaultBlockState());
    }

    public BlockState getPlacementState(Level level, BlockPos pos, BlockState state) {
        if (state == null || !state.is(this)) {
            return state;
        }
        return state.setValue(TWO_HIGH, level.getBlockState(pos.above()).isAir());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        state = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        if (!this.canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (direction == Direction.UP) {
            return state.setValue(TWO_HIGH, neighborState.isAir());
        }
        return state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(BwtBlocks.hibachiBlock);
    }

    @Override
    protected boolean canBurn(BlockState state) {
        return FlammableBlockRegistry.getDefaultInstance().get(state.getBlock()).getBurnChance() > 0;
    }


    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            return;
        }
        if (!state.canSurvive(level, pos)) {
            level.removeBlock(pos, false);
        }
        BlockState blockAboveState = level.getBlockState(pos.above());
        if (blockAboveState.is(Blocks.BRICKS)) {
            level.setBlockAndUpdate(pos.above(), BwtBlocks.kilnBlock.defaultBlockState());
        }

        int oldAge = state.getValue(AGE);

        // If aging out, replace with regular fire
        if (oldAge >= 2) {
            level.setBlockAndUpdate(pos, FireBlock.getState(level, pos));
            return;
        }

        int age = Math.min(2, oldAge + 1);
        level.setBlock(pos, state.setValue(AGE, age), Block.UPDATE_ALL);

        boolean extraBurn = level.getBiome(pos).is(BiomeTags.INCREASED_FIRE_BURNOUT);
        int k = extraBurn ? -50 : 0;
        this.trySpreadingFire(level, pos.east(), 300 + k, random, age);
        this.trySpreadingFire(level, pos.west(), 300 + k, random, age);
        this.trySpreadingFire(level, pos.below(), 250 + k, random, age);
        this.trySpreadingFire(level, pos.above(), 250 + k, random, age);
        this.trySpreadingFire(level, pos.north(), 300 + k, random, age);
        this.trySpreadingFire(level, pos.south(), 300 + k, random, age);

        level.scheduleTick(pos, this, tickRate + random.nextInt(10));
    }

    private int getSpreadChance(BlockState state) {
        if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
            return 0;
        }
        return FlammableBlockRegistry.getInstance(Blocks.FIRE).get(state.getBlock()).getSpreadChance();
    }

    private void trySpreadingFire(Level level, BlockPos pos, int spreadFactor, RandomSource random, int currentAge) {
        int i = this.getSpreadChance(level.getBlockState(pos));
        if (random.nextInt(spreadFactor) < i) {
            BlockState blockState = level.getBlockState(pos);
            if (random.nextInt(currentAge + 10) < 5 && !level.isRainingAt(pos)) {
                int j = Math.min(currentAge + random.nextInt(5) / 4, 15);
                level.setBlock(pos, this.getStateWithAge(j), Block.UPDATE_ALL);
            } else {
                level.removeBlock(pos, false);
            }
            Block block = blockState.getBlock();
            if (block instanceof TntBlock) {
                TntBlock.explode(level, pos);
            }
            if (block instanceof ICaughtFireBlock caught) {
                caught.onCaughtFire(blockState, level, pos, null, null);
            }
        }
    }

    private BlockState getStateWithAge(int age) {
        BlockState blockState = defaultBlockState();
        if (blockState.is(BwtBlocks.stokedFireBlock)) {
            return blockState.setValue(AGE, age);
        }
        return blockState;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        level.scheduleTick(pos, this, tickRate);
    }
}
