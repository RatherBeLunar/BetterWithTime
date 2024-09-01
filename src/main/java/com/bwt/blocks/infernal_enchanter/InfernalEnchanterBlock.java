package com.bwt.blocks.infernal_enchanter;

import com.bwt.block_entities.BwtBlockEntities;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.function.ToIntFunction;

public class InfernalEnchanterBlock extends BlockWithEntity {

    public static final ToIntFunction<BlockState> STATE_TO_LUMINANCE;


    public static final MapCodec<InfernalEnchanterBlock> CODEC = createCodec(InfernalEnchanterBlock::new);

    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    public static final BooleanProperty LIT;

    public InfernalEnchanterBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(LIT, false));
    }

    private static final ImmutableList<Vec3d> PARTICLE_OFFSETS;
    public static double P = 1 / 16.0;

    static {
        PARTICLE_OFFSETS = ImmutableList.of(new Vec3d(2 * P, 13 * P, 2 * P), new Vec3d(14 * P, 13 * P, 2 * P), new Vec3d(2 * P, 13 * P, 14 * P), new Vec3d(14 * P, 13 * P, 14 * P));
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        var entity = world.getBlockEntity(pos);
        if (entity instanceof InfernalEnchanterBlockEntity ie) {
            ie.spawnLetterParticles();
        }
    }

    protected static void spawnCandleParticles(BlockState state, World world, BlockPos pos, Random random) {
        PARTICLE_OFFSETS.forEach((offset) -> {
            spawnCandleParticle(world, offset.add(pos.getX(), pos.getY(), pos.getZ()), random);
        });
    }

    protected static void spawnCandleParticle(World world, Vec3d vec3d, Random random) {

        float f = random.nextFloat();
        if (f < 0.3F) {
            if(world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.SMOKE, vec3d.x, vec3d.y, vec3d.z, 1, 0.0, 0.0, 0.0, 0.0);
            } else {
                world.addParticle(ParticleTypes.SMOKE, vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
            }
            if (f < 0.17F) {
                world.playSound(vec3d.x + 0.5, vec3d.y + 0.5, vec3d.z + 0.5, SoundEvents.BLOCK_CANDLE_AMBIENT, SoundCategory.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
            }
        }
        if(world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SMALL_FLAME, vec3d.x, vec3d.y, vec3d.z, 1, 0.0, 0.0, 0.0, 0.0);
        } else {
            world.addParticle(ParticleTypes.SMALL_FLAME, vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
        super.appendProperties(builder);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InfernalEnchanterBlockEntity(pos, state);
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InfernalEnchanterBlockEntity infernalEnchanterBlockEntity) {
            player.openHandledScreen(infernalEnchanterBlockEntity);
        }
        return ActionResult.CONSUME;
    }

    @Nullable
    protected static <A extends BlockEntity, E extends InfernalEnchanterBlockEntity> BlockEntityTicker<A> validateTicker(World world, BlockEntityType<A> givenType, BlockEntityType<E> expectedType) {
        return world.isClient ? null : BlockWithEntity.validateTicker(givenType, expectedType, E::tick);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> givenType) {
        return validateTicker(world, givenType, BwtBlockEntities.infernalEnchanterBlockEntity);
    }


    static {
        LIT = RedstoneTorchBlock.LIT;


        STATE_TO_LUMINANCE = (state) -> (Boolean) state.get(LIT) ? 3 * 4 : 0;
    }


}
