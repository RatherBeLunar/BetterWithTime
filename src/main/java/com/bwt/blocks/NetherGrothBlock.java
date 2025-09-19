package com.bwt.blocks;

import com.bwt.utils.BlockPosAndState;
import com.bwt.utils.RadiusAroundBlockStream;
import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

// TODO: Does not include logic for nether groth growing to its max age when a soul urn entity collides with it,
//  not sure if we even want that.
// TODO: Might be better to have a common block tag for blocks the groth can "eat" instead of just red and brown mushroom
//  that the original code has, considering the nether has new blocks since 1.16 -> in the spreadToBlock() method.
public class NetherGrothBlock extends Block {
    public static final IntProperty AGE = Properties.AGE_7;
    public static final int MAX_AGE = 7;

    public NetherGrothBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(AGE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int age = state.get(AGE);
        int maxY = (age + 1);
        return Block.createCuboidShape(0, 0, 0, 16, maxY, 16);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState stateBelow = world.getBlockState(pos.down());
        return stateBelow.isFullCube(world, pos.down());
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    // Set the block below to Grothed Netherrack block when placed
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.getBlockState(pos.down()).isOf(Blocks.NETHERRACK)) {
            world.setBlockState(pos.down(), BwtBlocks.netherrackGrothedBlock.getDefaultState());
        }
    }

    public int getAge(BlockState state) {
        return state.get(AGE);
    }

    public final boolean isMature(BlockState state) {
        return this.getAge(state) >= MAX_AGE;
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return !this.isMature(state);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int height = state.get(AGE);
        BlockState stateBelow = world.getBlockState(pos.down());
        // Added a separate block for Grothed Netherrack so we don't add a blockstate to the normal Netherrack
        // Might reconsider
        boolean isOnNetherrack = stateBelow.isOf(BwtBlocks.netherrackGrothedBlock) || stateBelow.isOf(Blocks.NETHERRACK);

        // Attempt to grow
        if (height < MAX_AGE) {
            boolean canGrow = false;

            if (isOnNetherrack) {
                canGrow = true;
            } else {
                if (getMaxHeightOfNeighbors(world, pos) > height + 1) {
                    canGrow = true;
                }
            }

            if (canGrow) {
                height++;

                world.setBlockState(pos, state.with(AGE, height));
                // Not sure what this does and/or if it's necessary in modern minecraft
                //world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
            }
        }

        if (height < 1) {
            return;
        }
        // Attempt to spread
        // Pick a random horizontal direction
        Direction direction = Direction.Type.HORIZONTAL.random(random);
        BlockPos targetPos = pos.offset(direction);
        BlockState targetState = world.getBlockState(targetPos);

        if (isBlockOpenToSpread(targetState)) {
            BlockPos belowTargetPos = targetPos.down();
            BlockState belowTargetState = world.getBlockState(belowTargetPos);
            // Check solid block below
            if (belowTargetState.isSideSolidFullSquare(world, belowTargetPos, Direction.UP)) {
                spreadToBlock(world, targetPos, targetState);
            } else if (isOnNetherrack) {
                // Try below that if we're on netherrack
                if (isBlockOpenToSpread(belowTargetState) && belowTargetState.isSideSolidFullSquare(world, belowTargetPos.down(), Direction.UP)) {
                    spreadToBlock(world, belowTargetPos, belowTargetState);
                }
            }
            return;
        }
        // The moss can only spread upwards onto a netherrack block and if there is empty space above where it's currently at
        if (world.isAir(pos.up()) && targetState.isOf(Blocks.NETHERRACK)) {
            BlockPos targetPosUp = targetPos.up();
            BlockState targetStateUp = world.getBlockState(targetPosUp);

            if (isBlockOpenToSpread(targetStateUp)) {
                spreadToBlock(world, targetPosUp, targetStateUp);
            }
        }

    }

    private void spreadToBlock(World world, BlockPos pos, BlockState state) {
        if (state.isOf(Blocks.FIRE)) {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS,
                    0.5F, 2.6F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.8F
            );
        } else if (state.isOf(Blocks.BROWN_MUSHROOM) || state.isOf(Blocks.RED_MUSHROOM)) {
            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.BLOCKS,
                    1.0F, world.getRandom().nextFloat() * 0.4F + 0.7F
            );
        }

        if (world.setBlockState(pos, state, Block.NOTIFY_ALL)) {
            world.playSound(null, pos, SoundEvents.ENTITY_GHAST_AMBIENT, SoundCategory.BLOCKS,
                    0.5F, 2.6F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.8F
            );
        }

        // Set the block that it spreads to Grothed Netherrack
        if (world.getBlockState(pos.down()).isOf(Blocks.NETHERRACK)) {
            world.setBlockState(pos.down(), BwtBlocks.netherrackGrothedBlock.getDefaultState());
        }
    }

    private boolean isBlockOpenToSpread(BlockState state) {
        return state.isAir() || state.isOf(Blocks.FIRE) || state.isOf(Blocks.RED_MUSHROOM) || state.isOf(Blocks.BROWN_MUSHROOM);
    }

    private int getMaxHeightOfNeighbors(World world, BlockPos pos) {
        return Direction.Type.HORIZONTAL.stream()
                .map(pos::offset)
                .map(world::getBlockState)
                .filter(neighborState -> neighborState.isOf(this))
                .mapToInt(neighborState -> neighborState.get(AGE))
                .max()
                .orElse(-1);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) {
            return;
        }

        int age = state.get(AGE);
        if (age < MAX_AGE) {
            return;
        }

        if (entity instanceof LivingEntity livingEntity) {
            if (entity instanceof PlayerEntity player && wearingPlateBoots(player)) {
                return;
            }
            if (livingEntity.damage(world.getDamageSources().magic(), 2)) {
                entity.setVelocity(entity.getVelocity().x, 0.84, entity.getVelocity().z);
                entity.velocityModified = true;
                world.playSound(null, pos, SoundEvents.ENTITY_GHAST_SCREAM, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        } else if (entity instanceof ItemEntity itemEntity) {
            if (itemEntity.cannotPickup()) {
                return;
            }
            ItemStack stack = itemEntity.getStack();
            if (stack.getComponents().contains(DataComponentTypes.FOOD) || stack.isOf(Items.RED_MUSHROOM) || stack.isOf(Items.BROWN_MUSHROOM)) {
                itemEntity.remove(Entity.RemovalReason.DISCARDED);
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }


    private boolean wearingPlateBoots(PlayerEntity player) {
        ItemStack boots = player.getInventory().getArmorStack(0);
        return boots.isOf(Items.NETHERITE_BOOTS);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        // Revert Grothed Netherrack back to normal when block is broken
        if (world.getBlockState(pos.down()).isOf(BwtBlocks.netherrackGrothedBlock)) {
            world.setBlockState(pos.down(), Blocks.NETHERRACK.getDefaultState(), Block.NOTIFY_ALL);
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        int height = state.get(AGE);

        if (height == MAX_AGE) {
            releaseSpores(world, pos);
        }

        super.onBroken(world, pos, state);
    }

    private void releaseSpores(WorldAccess world, BlockPos pos) {
        if (!(world instanceof ServerWorld server)) return;

        world.playSound(null, pos, SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.BLOCKS,
                2.0F, world.getRandom().nextFloat() * 0.4F + 1.5F
        );
        server.spawnParticles(
                ParticleTypes.EXPLOSION,
                pos.getX(), pos.getY(), pos.getZ(),
                10, 0.5, 0.5, 0.5, 0.1
        );
//        world.addParticle(
//                ParticleTypes.EXPLOSION,
//                pos.getX() + world.getRandom().nextDouble() * 10.0D - 5D,
//                pos.getY() + world.getRandom().nextDouble() * 10.0D - 5D,
//                pos.getZ() + world.getRandom().nextDouble() * 10.0D - 5D,
//                0.0D, 0.0D, 0.0D
//        );

        // spread growth to nearby blocks
        RadiusAroundBlockStream
                // Get all neighbors in range
                .neighboringBlocksInRadius(pos, 3)
                .map(neighborPos -> BlockPosAndState.of(world, neighborPos))
                // Filter to blocks open to spread
                .filter(neighbor -> isBlockOpenToSpread(neighbor.state()))
                .map(neighbor -> BlockPosAndState.of(world, neighbor.pos().down()))
                // Filter to blocks whose supporting block can support groth
                .filter(neighborSupportingBlock -> neighborSupportingBlock.state().isSideSolidFullSquare(world, neighborSupportingBlock.pos(), Direction.UP))
                // Random chance of spreading
                .filter(neighborSupportingBlock -> world.getRandom().nextInt(2) == 0)
                // Perform the spread
                .forEach(neighborSupportingBlock -> world.setBlockState(neighborSupportingBlock.pos().up(), this.getDefaultState(), Block.NOTIFY_ALL));

        // damage living entities nearby
        if (world.isClient()) {
            return;
        }
        Box box = new Box(
                pos.getX() - 5d, pos.getY() - 5d, pos.getZ() - 5d,
                pos.getX() + 5d, pos.getY() + 5d, pos.getZ() + 5d
        );
        server.getEntitiesByClass(LivingEntity.class, box, e -> true)
                .stream()
                .filter(target -> !(target instanceof PlayerEntity player) || !wearingFullNetherite(player))
                .forEach(target -> {
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 15 * 20, 0));
                    target.damage(server.getDamageSources().magic(), 15.0f);
                });
    }

    private boolean wearingFullNetherite(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        return IntStream
                .range(0, 3)
                .mapToObj(inventory::getArmorStack)
                .map(ItemStack::getItem)
                .filter(item -> item instanceof ArmorItem)
                .map(item -> ((ArmorItem) item))
                .map(ArmorItem::getMaterial)
                .allMatch(ArmorMaterials.NETHERITE::matches);
    }

}
