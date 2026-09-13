package com.bwt.blocks;

import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public class HibachiBlock extends Block {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public HibachiBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(LIT, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);
        level.scheduleTick(pos, this, 4);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);

        boolean lit = level.hasNeighborSignal(pos);
        if (state.getValue(LIT) != lit) {
            level.setBlock(pos, state.cycle(LIT), Block.UPDATE_ALL);
        }
        if (lit) {
            BlockState aboveState = level.getBlockState(pos.above());
            if (!aboveState.is(BlockTags.FIRE)) {
                if (aboveState.is(BlockTags.AIR) || BwtBlocks.stokedFireBlock.canBurn(aboveState)) {
                    level.playSound(null, pos, BwtSoundEvents.HIBACHI_IGNITE,
                            SoundSource.BLOCKS, 1F, level.random.nextFloat() * 0.4F + 1F);
                    level.setBlockAndUpdate(pos.above(), Blocks.FIRE.defaultBlockState());
                } else {
                    level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
                }
            }
        }
        else {
            if (level.getBlockState(pos.above()).is(BlockTags.FIRE)) {
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH,
                        SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
                level.removeBlock(pos.above(), false);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (level.isClientSide) {
            return;
        }
        level.scheduleTick(pos, this, 4);
    }
}
