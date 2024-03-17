package com.bwt.blocks;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;

public class StokedFireBlock extends AbstractFireBlock {
    public static final IntProperty AGE = Properties.AGE_2;
    public static final int tickRate = 42;

    public static final MapCodec<StokedFireBlock> CODEC = SoulFireBlock.createCodec(StokedFireBlock::new);

    public MapCodec<StokedFireBlock> getCodec() {
        return CODEC;
    }

    public StokedFireBlock(AbstractBlock.Settings settings) {
        super(settings, 4.0f);
        setDefaultState(getDefaultState().with(AGE, 0));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state;
//        if (this.canPlaceAt(state, world, pos)) {
//            return this.getStateWithAge(state.get(AGE));
//        }
//        return Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isOf(BwtBlocks.hibachiBlock);
    }

    @Override
    protected boolean isFlammable(BlockState state) {
        return true;
    }


    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            return;
        }
        if (!state.canPlaceAt(world, pos)) {
            world.removeBlock(pos, false);
        }
//        BlockState blockAboveState = world.getBlockState(pos.up());
//        if (blockAboveState.isOf(Blocks.BRICKS)) {
//            world.setBlockState(pos.up(), BwtBlocks.kilnBlock);
//        }

        int oldAge = state.get(AGE);

        // If aging out, replace with regular fire
        if (oldAge >= 2) {
            world.setBlockState(pos, FireBlock.getState(world, pos));
            return;
        }

        int age = Math.min(2, oldAge + 1);
        world.setBlockState(pos, state.with(AGE, age), Block.NOTIFY_ALL);

        boolean extraBurn = world.getBiome(pos).isIn(BiomeTags.INCREASED_FIRE_BURNOUT);
        int k = extraBurn ? -50 : 0;
        this.trySpreadingFire(world, pos.east(), 300 + k, random, age);
        this.trySpreadingFire(world, pos.west(), 300 + k, random, age);
        this.trySpreadingFire(world, pos.down(), 250 + k, random, age);
        this.trySpreadingFire(world, pos.up(), 250 + k, random, age);
        this.trySpreadingFire(world, pos.north(), 300 + k, random, age);
        this.trySpreadingFire(world, pos.south(), 300 + k, random, age);

        world.scheduleBlockTick(pos, this, tickRate + random.nextInt(10));
    }

    private int getSpreadChance(BlockState state) {
        if (state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED).booleanValue()) {
            return 0;
        }
        return FlammableBlockRegistry.getInstance(Blocks.FIRE).get(state.getBlock()).getSpreadChance();
    }

    private void trySpreadingFire(World world, BlockPos pos, int spreadFactor, Random random, int currentAge) {
        int i = this.getSpreadChance(world.getBlockState(pos));
        if (random.nextInt(spreadFactor) < i) {
            BlockState blockState = world.getBlockState(pos);
            if (random.nextInt(currentAge + 10) < 5 && !world.hasRain(pos)) {
                int j = Math.min(currentAge + random.nextInt(5) / 4, 15);
                world.setBlockState(pos, this.getStateWithAge(j), Block.NOTIFY_ALL);
            } else {
                world.removeBlock(pos, false);
            }
            Block block = blockState.getBlock();
            if (block instanceof TntBlock) {
                TntBlock.primeTnt(world, pos);
            }
        }
    }

    private BlockState getStateWithAge(int age) {
        BlockState blockState = getDefaultState();
        if (blockState.isOf(BwtBlocks.stokedFireBlock)) {
            return blockState.with(AGE, age);
        }
        return blockState;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, tickRate);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}
