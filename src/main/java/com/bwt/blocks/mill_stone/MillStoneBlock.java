package com.bwt.blocks.mill_stone;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.mechanical.impl.DirectionTools;
import com.bwt.mechanical.impl.MachineBlockWithEntity;
import com.bwt.sounds.BwtSoundEvents;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MillStoneBlock extends MachineBlockWithEntity {

    public MillStoneBlock(Settings settings) {
        super(settings, 10, 9);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
         super.randomDisplayTick(state, world, pos, random);
         if (!isPowered(state)) {
             return;
         }
         emitGearBoxParticles(world, pos, random);
         if (random.nextInt(4) == 0) {
             playMechSound(world, pos);
         }
    }
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MillStoneBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MillStoneBlockEntity millStoneBlockEntity) {
            player.openHandledScreen(millStoneBlockEntity);
        }
        return ActionResult.CONSUME;
    }

    @Nullable
    protected static <A extends BlockEntity> BlockEntityTicker<A> validateTicker(World world, BlockEntityType<A> givenType) {
        return world.isClient ? null : BlockWithEntity.validateTicker(givenType, BwtBlockEntities.millStoneBlockEntity, MillStoneBlockEntity::tick);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> givenType) {
        return MillStoneBlock.validateTicker(world, givenType);
    }
    
    private void playMechSound(World world, BlockPos pos) {
        world.playSoundAtBlockCenter(pos, BwtSoundEvents.MILL_STONE_GRIND, SoundCategory.BLOCKS, 0.125f,  1.25F, false);
    }

    private void emitGearBoxParticles(World world, BlockPos pos, Random random) {
        for ( int iTempCount = 0; iTempCount < 5; iTempCount++ )
        {
            float smokeX = (float)pos.getX() + random.nextFloat();
            float smokeY = (float)pos.getY() + random.nextFloat() * 0.5F + 1.0F;
            float smokeZ = (float)pos.getZ() + random.nextFloat();
            world.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0D, 0D, 0D );
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public List<Direction> getInputFaces(World world, BlockPos pos, BlockState blockState) {
        return DirectionTools.fromAxis(Direction.Axis.Y);
    }
}
