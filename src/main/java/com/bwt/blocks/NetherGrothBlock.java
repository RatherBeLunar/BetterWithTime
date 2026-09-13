package com.bwt.blocks;

import com.bwt.tags.BwtBlockTags;
import com.bwt.tags.BwtItemTags;
import com.bwt.utils.BlockPosAndState;
import com.bwt.utils.RadiusAroundBlockStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;
import java.util.stream.IntStream;

// TODO: Does not include logic for nether groth growing to its max age when a soul urn entity collides with it,
//  not sure if we even want that.
public class NetherGrothBlock extends Block {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    public static final int MAX_AGE = 7;
    public static final VoxelShape FLAT_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    public NetherGrothBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(AGE, 0));
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int age = state.getValue(AGE);
        int maxY = (age + 1);
        return Block.box(0, 0, 0, 16, maxY, 16);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return FLAT_SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());
        return stateBelow.isCollisionShapeFullBlock(level, pos.below());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    // Set the block below to Grothed Netherrack block when placed
    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, level, pos, oldState, notify);
        if (level.getBlockState(pos.below()).is(Blocks.NETHERRACK)) {
            level.setBlockAndUpdate(pos.below(), BwtBlocks.grothedNetherrackBlock.defaultBlockState());
        }
    }

    public int getAge(BlockState state) {
        return state.getValue(AGE);
    }

    public final boolean isMature(BlockState state) {
        return this.getAge(state) >= MAX_AGE;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Only grow in the nether
        if (!level.dimensionType().ultraWarm()) {
            return;
        }

        int height = state.getValue(AGE);
        BlockState stateBelow = level.getBlockState(pos.below());
        // TODO consider if we wanna do other blocks in a block tag
        // Adding nether quartz, nether gold ore, and then even more questionably: soul sand, magma, basalt, blackstone
        boolean isOnNetherrack = stateBelow.is(BwtBlocks.grothedNetherrackBlock) || stateBelow.is(Blocks.NETHERRACK);

        // Attempt to grow
        if (height < MAX_AGE) {
            boolean canGrow = isOnNetherrack || getMaxHeightOfNeighbors(level, pos) > height + 1;

            if (canGrow) {
                height++;

                level.setBlockAndUpdate(pos, state.setValue(AGE, height));
                // Not sure what this does and/or if it's necessary in modern minecraft
                //world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
            }
        }

        if (height < 1) {
            return;
        }
        // Attempt to spread
        // Pick a random horizontal direction
        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos targetPos = pos.relative(direction);

        for (BlockPos potentialOpenSpace : new BlockPos[]{targetPos, targetPos.below(), targetPos.above()}) {
            BlockState targetState = level.getBlockState(potentialOpenSpace);
            // Check open air - first next to the source block, then above that, then below that
            if (isBlockOpenToSpread(targetState)) {
                BlockPos belowPotentialOpenSpace = potentialOpenSpace.below();
                BlockState belowTargetState = level.getBlockState(belowPotentialOpenSpace);
                // Check if the block below can support growth
                if (belowTargetState.isFaceSturdy(level, belowPotentialOpenSpace, Direction.UP)) {
                    spreadToBlock(level, potentialOpenSpace, targetState);
                }
            }

            // Only spread up or down if originating on netherrack.
            if (!isOnNetherrack) {
                break;
            }
        }
    }

    private void spreadToBlock(Level level, BlockPos pos, BlockState state) {
        if (state.is(Blocks.FIRE)) {
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,
                    0.5F, 2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F
            );
        } else if (state.is(BwtBlockTags.NETHER_GROTH_CAN_EAT)) {
            level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS,
                    1.0F, level.getRandom().nextFloat() * 0.4F + 0.7F
            );
        }

        if (level.setBlock(pos, this.defaultBlockState(), Block.UPDATE_ALL)) {
            level.playSound(null, pos, SoundEvents.GHAST_AMBIENT, SoundSource.BLOCKS,
                    0.5F, 2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F
            );
        }

        // Set the block that it spreads to Grothed Netherrack
        if (level.getBlockState(pos.below()).is(Blocks.NETHERRACK)) {
            level.setBlockAndUpdate(pos.below(), BwtBlocks.grothedNetherrackBlock.defaultBlockState());
        }
    }

    private boolean isBlockOpenToSpread(BlockState state) {
        return state.isAir() || state.is(Blocks.FIRE) || state.is(BwtBlockTags.NETHER_GROTH_CAN_EAT);
    }

    private int getMaxHeightOfNeighbors(Level level, BlockPos pos) {
        return Direction.Plane.HORIZONTAL.stream()
                .map(pos::relative)
                .map(level::getBlockState)
                .filter(neighborState -> neighborState.is(this))
                .mapToInt(neighborState -> neighborState.getValue(AGE))
                .max()
                .orElse(-1);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) {
            return;
        }

        int age = state.getValue(AGE);
        if (age < MAX_AGE) {
            return;
        }

        if (entity instanceof LivingEntity livingEntity) {
            if (entity instanceof Player player && wearingPlateBoots(player)) {
                return;
            }
            if (livingEntity.hurt(level.damageSources().magic(), 2)) {
                entity.setDeltaMovement(entity.getDeltaMovement().x, 0.84, entity.getDeltaMovement().z);
                entity.hurtMarked = true;
                level.playSound(null, pos, SoundEvents.GHAST_SCREAM, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        } else if (entity instanceof ItemEntity itemEntity) {
            if (itemEntity.hasPickUpDelay()) {
                return;
            }
            ItemStack stack = itemEntity.getItem();
            if (stack.getComponents().has(DataComponents.FOOD) || stack.is(BwtItemTags.NETHER_GROTH_CAN_EAT)) {
                itemEntity.remove(Entity.RemovalReason.DISCARDED);
                level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }


    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        // Revert Grothed Netherrack back to normal when block is broken
        if (level.getBlockState(pos.below()).is(BwtBlocks.grothedNetherrackBlock)) {
            level.setBlock(pos.below(), Blocks.NETHERRACK.defaultBlockState(), Block.UPDATE_ALL);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        int height = state.getValue(AGE);

        if (height == MAX_AGE) {
            releaseSpores(level, pos);
        }

        super.destroy(level, pos, state);
    }

    private void releaseSpores(LevelAccessor level, BlockPos pos) {
        if (!(level instanceof ServerLevel server)) return;

        level.playSound(null, pos, SoundEvents.CREEPER_PRIMED, SoundSource.BLOCKS,
                2.0F, level.getRandom().nextFloat() * 0.4F + 1.5F
        );
        server.sendParticles(
                ParticleTypes.EXPLOSION,
                pos.getX(), pos.getY(), pos.getZ(),
                10, 0.5, 0.5, 0.5, 0.1
        );
//        level.addParticle(
//                ParticleTypes.EXPLOSION,
//                pos.getX() + level.getRandom().nextDouble() * 10.0D - 5D,
//                pos.getY() + level.getRandom().nextDouble() * 10.0D - 5D,
//                pos.getZ() + level.getRandom().nextDouble() * 10.0D - 5D,
//                0.0D, 0.0D, 0.0D
//        );

        // spread growth to nearby blocks
        RadiusAroundBlockStream
                // Get all neighbors in range
                .neighboringBlocksInRadius(pos, 3)
                .map(neighborPos -> BlockPosAndState.of(level, neighborPos))
                // Filter to blocks open to spread
                .filter(neighbor -> isBlockOpenToSpread(neighbor.state()))
                .map(neighbor -> BlockPosAndState.of(level, neighbor.pos().below()))
                // Filter to blocks whose supporting block can support groth
                .filter(neighborSupportingBlock -> neighborSupportingBlock.state().isFaceSturdy(level, neighborSupportingBlock.pos(), Direction.UP))
                // Random chance of spreading
                .filter(neighborSupportingBlock -> level.getRandom().nextInt(2) == 0)
                // Perform the spread
                .forEach(neighborSupportingBlock -> level.setBlock(neighborSupportingBlock.pos().above(), this.defaultBlockState(), Block.UPDATE_ALL));

        // damage living entities nearby
        if (level.isClientSide()) {
            return;
        }
        AABB box = new AABB(
                pos.getX() - 5d, pos.getY() - 5d, pos.getZ() - 5d,
                pos.getX() + 5d, pos.getY() + 5d, pos.getZ() + 5d
        );
        server.getEntitiesOfClass(LivingEntity.class, box, e -> true)
                .stream()
                .filter(target -> !(target instanceof Player player) || !wearingFullNetherite(player))
                .forEach(target -> {
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 15 * 20, 0));
                    target.hurt(server.damageSources().magic(), 15.0f);
                });
    }

    private boolean wearingPlateBoots(Player player) {
        ItemStack boots = player.getInventory().getArmor(0);
        return boots.is(Items.NETHERITE_BOOTS);
    }

    private boolean wearingFullNetherite(Player player) {
        Inventory inventory = player.getInventory();
        return IntStream
                .range(0, 3)
                .mapToObj(inventory::getArmor)
                .map(ItemStack::getItem)
                .map(item -> Optional.ofNullable(item instanceof ArmorItem armorItem ? armorItem : null))
                .map(optionalArmor -> optionalArmor.map(ArmorItem::getMaterial))
                .allMatch(optionalMaterial -> optionalMaterial.isPresent() && ArmorMaterials.NETHERITE.is(optionalMaterial.get()));
    }

}
