package com.bwt.blocks.mining_charge;

import com.bwt.blocks.SidingBlock;
import com.bwt.entities.MiningChargeEntity;
import com.bwt.sounds.BwtSoundEvents;
import com.bwt.utils.BlockUtils;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class MiningChargeBlock extends WallMountedBlock implements ICaughtFireBlock {
    protected static final Box BOTTOM_SHAPE = new Box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    protected static final List<VoxelShape> COLLISION_SHAPES = Arrays.stream(Direction.values())
            .map(direction -> BlockUtils.rotateCuboidFromUp(direction, BOTTOM_SHAPE))
            .toList();
    public static final MapCodec<MiningChargeBlock> CODEC = SidingBlock.createCodec(MiningChargeBlock::new);

    public MiningChargeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(FACE, BlockFace.WALL));
        FlammableBlockRegistry.getDefaultInstance().add(
                this, 15, 100
        );
    }

    public MapCodec<? extends MiningChargeBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, FACE);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPES.get(getSurfaceOrientation(state).getId());
    }

    public BlockState getDispenserPlacmentState(ItemPlacementContext ctx) {
        for (Direction direction : ctx.getPlacementDirections()) {
            BlockState blockState;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockState = this.getDefaultState()
                        .with(FACE, direction == Direction.UP ? BlockFace.CEILING : BlockFace.FLOOR)
                        .with(FACING, ctx.getHorizontalPlayerFacing());
            } else {
                blockState = this.getDefaultState().with(FACE, BlockFace.WALL).with(FACING, direction.getOpposite());
            }
            return blockState;
        }

        return null;
    }

    public static boolean isHorizontal(BlockState state) {
        return state.get(FACE) == BlockFace.WALL;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isReceivingRedstonePower(pos)) {
            world.scheduleBlockTick(pos, this, 1);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isReceivingRedstonePower(pos)) {
            world.scheduleBlockTick(pos, this, 1);
        }
        if (!world.getBlockState(pos).isOf(this)) {
            return;
        }
        if (canPlaceAt(state, world, pos)) {
            return;
        }
        dropStacks(state, world, pos);
        world.removeBlock(pos, notify);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved) {
            return;
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        prime(world, pos, state, null);
        world.removeBlock(pos, false);
    }

    @Deprecated
    public void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (state.isIn(BlockTags.AIR) || explosion.getDestructionType() == Explosion.DestructionType.TRIGGER_BLOCK) {
            return;
        }
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
        if (world.isClient) {
            return;
        }
        MiningChargeEntity miningChargeEntity = new MiningChargeEntity(world, pos.toCenterPos().subtract(0, 0.5, 0), state, explosion.getCausingEntity());
        miningChargeEntity.setFuse(1);
        world.spawnEntity(miningChargeEntity);
    }

    private static void prime(World world, BlockPos pos, BlockState state, @Nullable LivingEntity igniter) {
        if (world.isClient) {
            return;
        }
        MiningChargeEntity miningChargeEntity = new MiningChargeEntity(world, pos.toCenterPos().subtract(0, 0.5, 0), state, igniter);
        world.spawnEntity(miningChargeEntity);
        world.playSound(null, miningChargeEntity.getX(), miningChargeEntity.getY(), miningChargeEntity.getZ(), BwtSoundEvents.MINING_CHARGE_PRIME, SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.emitGameEvent(igniter, GameEvent.PRIME_FUSE, pos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(player.getActiveHand());
        if (!itemStack.isOf(Items.FLINT_AND_STEEL) && !itemStack.isOf(Items.FIRE_CHARGE)) {
            return super.onUse(state, world, pos, player, hit);
        }
        prime(world, pos, state, player);
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
        Item item = itemStack.getItem();
        if (!player.isCreative()) {
            if (itemStack.isOf(Items.FLINT_AND_STEEL)) {
                itemStack.damage(1, player, PlayerEntity.getSlotForHand(player.getActiveHand()));
            } else {
                itemStack.decrement(1);
            }
        }
        player.incrementStat(Stats.USED.getOrCreateStat(item));
        return ActionResult.success(world.isClient);
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (!world.isClient) {
            BlockPos blockPos = hit.getBlockPos();
            Entity entity = projectile.getOwner();
            if (projectile.isOnFire() && projectile.canModifyAt(world, blockPos)) {
                prime(world, blockPos, state, entity instanceof LivingEntity ? (LivingEntity)entity : null);
                world.removeBlock(blockPos, false);
            }
        }
    }

    @Override
    public boolean shouldDropItemsOnExplosion(Explosion explosion) {
        return false;
    }

    public static Direction getSurfaceOrientation(BlockState state) {
        return switch (state.get(FACE)) {
            case WALL -> state.get(FACING);
            case FLOOR -> Direction.UP;
            case CEILING -> Direction.DOWN;
        };
    }

    public static BlockState withSurfaceOrientation(BlockState state, Direction direction) {
        return switch (direction) {
            case UP -> state.with(FACE, BlockFace.FLOOR);
            case DOWN -> state.with(FACE, BlockFace.CEILING);
            case NORTH -> state.with(FACE, BlockFace.WALL).with(FACING, Direction.SOUTH);
            case EAST -> state.with(FACE, BlockFace.WALL).with(FACING, Direction.WEST);
            case SOUTH -> state.with(FACE, BlockFace.WALL).with(FACING, Direction.NORTH);
            case WEST -> state.with(FACE, BlockFace.WALL).with(FACING, Direction.EAST);
        };
    }
    @Override
    public boolean onCaughtFire(BlockState state, World world, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {
        prime(world, pos, state, igniter);
        return true;
    }
}
