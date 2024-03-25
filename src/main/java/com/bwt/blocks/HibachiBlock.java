package com.bwt.blocks;

import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HibachiBlock extends Block {
    public static final BooleanProperty LIT = Properties.LIT;

    public HibachiBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LIT);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(LIT, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        world.scheduleBlockTick(pos, this, 4);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        boolean lit = world.isReceivingRedstonePower(pos);
        if (state.get(LIT) != lit) {
            world.setBlockState(pos, state.cycle(LIT), Block.NOTIFY_ALL);
        }
        if (lit) {
            if (world.getBlockState(pos.up()).isAir()) {
                world.playSound(null, pos, BwtSoundEvents.HIBACHI_IGNITE,
                        SoundCategory.BLOCKS, 1F, world.random.nextFloat() * 0.4F + 1F);
                world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
            }
        }
        else {
            if (world.getBlockState(pos.up()).isIn(BlockTags.FIRE)) {
                world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH,
                        SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
                world.removeBlock(pos.up(), false);
            }
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        world.scheduleBlockTick(pos, this, 4);
    }
}
