package com.bwt.blocks.mining_charge;

import com.bwt.blocks.SidingBlock;
import com.bwt.entities.MiningChargeEntity;
import com.bwt.sounds.BwtSoundEvents;
import com.bwt.utils.BlockUtils;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class MiningChargeBlock extends FaceAttachedHorizontalDirectionalBlock implements ICaughtFireBlock {
    protected static final AABB BOTTOM_SHAPE = new AABB(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    protected static final List<VoxelShape> COLLISION_SHAPES = Arrays.stream(Direction.values())
            .map(direction -> BlockUtils.rotateCuboidFromUp(direction, BOTTOM_SHAPE))
            .toList();
    public static final MapCodec<MiningChargeBlock> CODEC = SidingBlock.simpleCodec(MiningChargeBlock::new);

    public MiningChargeBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
        FlammableBlockRegistry.getDefaultInstance().add(
                this, 15, 100
        );
    }

    public MapCodec<? extends MiningChargeBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, FACE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPES.get(getSurfaceOrientation(state).get3DDataValue());
    }

    public BlockState getDispenserPlacmentState(BlockPlaceContext ctx) {
        for (Direction direction : ctx.getNearestLookingDirections()) {
            BlockState blockState;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockState = this.defaultBlockState()
                        .setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR)
                        .setValue(FACING, ctx.getHorizontalDirection());
            } else {
                blockState = this.defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, direction.getOpposite());
            }
            return blockState;
        }

        return null;
    }

    public static boolean isHorizontal(BlockState state) {
        return state.getValue(FACE) == AttachFace.WALL;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        if (level.hasNeighborSignal(pos)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (level.hasNeighborSignal(pos)) {
            level.scheduleTick(pos, this, 1);
        }
        if (level.isClientSide || !level.getBlockState(pos).is(this)) {
            return;
        }
        if (canSurvive(state, level, pos)) {
            return;
        }
        dropResources(state, level, pos);
        level.removeBlock(pos, notify);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (moved) {
            return;
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        prime(level, pos, state, null);
        level.removeBlock(pos, false);
    }

    @Deprecated
    public void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (state.is(BlockTags.AIR) || explosion.getBlockInteraction() == Explosion.BlockInteraction.TRIGGER_BLOCK) {
            return;
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        if (level.isClientSide) {
            return;
        }
        MiningChargeEntity miningChargeEntity = new MiningChargeEntity(level, pos.getCenter().subtract(0, 0.5, 0), state, explosion.getIndirectSourceEntity());
        miningChargeEntity.setFuse(1);
        level.addFreshEntity(miningChargeEntity);
    }

    private static void prime(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity igniter) {
        if (level.isClientSide) {
            return;
        }
        MiningChargeEntity miningChargeEntity = new MiningChargeEntity(level, pos.getCenter().subtract(0, 0.5, 0), state, igniter);
        level.addFreshEntity(miningChargeEntity);
        level.playSound(null, miningChargeEntity.getX(), miningChargeEntity.getY(), miningChargeEntity.getZ(), BwtSoundEvents.MINING_CHARGE_PRIME, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(player.getUsedItemHand());
        if (!itemStack.is(Items.FLINT_AND_STEEL) && !itemStack.is(Items.FIRE_CHARGE)) {
            return super.useWithoutItem(state, level, pos, player, hit);
        }
        prime(level, pos, state, player);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        Item item = itemStack.getItem();
        if (!player.isCreative()) {
            if (itemStack.is(Items.FLINT_AND_STEEL)) {
                itemStack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
            } else {
                itemStack.shrink(1);
            }
        }
        player.awardStat(Stats.ITEM_USED.get(item));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!level.isClientSide) {
            BlockPos blockPos = hit.getBlockPos();
            Entity entity = projectile.getOwner();
            if (projectile.isOnFire() && projectile.mayInteract(level, blockPos)) {
                prime(level, blockPos, state, entity instanceof LivingEntity ? (LivingEntity)entity : null);
                level.removeBlock(blockPos, false);
            }
        }
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }

    public static Direction getSurfaceOrientation(BlockState state) {
        return switch (state.getValue(FACE)) {
            case WALL -> state.getValue(FACING);
            case FLOOR -> Direction.UP;
            case CEILING -> Direction.DOWN;
        };
    }

    public static BlockState withSurfaceOrientation(BlockState state, Direction direction) {
        return switch (direction) {
            case UP -> state.setValue(FACE, AttachFace.FLOOR);
            case DOWN -> state.setValue(FACE, AttachFace.CEILING);
            case NORTH -> state.setValue(FACE, AttachFace.WALL).setValue(FACING, Direction.SOUTH);
            case EAST -> state.setValue(FACE, AttachFace.WALL).setValue(FACING, Direction.WEST);
            case SOUTH -> state.setValue(FACE, AttachFace.WALL).setValue(FACING, Direction.NORTH);
            case WEST -> state.setValue(FACE, AttachFace.WALL).setValue(FACING, Direction.EAST);
        };
    }
    @Override
    public boolean onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {
        prime(level, pos, state, igniter);
        return true;
    }
}
