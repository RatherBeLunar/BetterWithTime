package com.bwt.blocks;

import com.bwt.damage_types.BwtDamageTypes;
import com.bwt.items.BwtItems;
import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.saw.SawRecipe;
import com.bwt.recipes.saw.SawRecipeInput;
import com.bwt.sounds.BwtSoundEvents;
import com.bwt.tags.BwtBlockTags;
import com.bwt.utils.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SawBlock extends SimpleFacingBlock implements MechPowerBlockBase {
    private static final int powerChangeTickRate = 10;

    private static final int sawTimeBaseTickRate = 15;
    private static final int sawTimeTickRateVariance = 4;

    // This base height prevents chickens slipping through grinders, while allowing items to pass

    public static final float baseHeight = 16f - 4f;

    public static final float bladeLength = 10f;
    public static final float bladeHalfLength = bladeLength * 0.5F;

    public static final float bladeWidth = 0.25f;
    public static final float bladeHalfWidth = bladeWidth * 0.5F;

    public static final float bladeHeight = 16F - baseHeight;
    protected static final AABB UPWARD_BASE_BOX = new AABB(0f, 0f, 0f, 16f, baseHeight, 16F);
    protected static final AABB UPWARD_BLADE_BOX = new AABB(8f - bladeHalfLength, baseHeight, 8f - bladeHalfWidth, 8f + bladeHalfLength, baseHeight + bladeHeight, 8f + bladeHalfWidth);
    protected static final AABB DOWNWARD_BLADE_BOX = new AABB(8f - bladeHalfLength, 0, 8f - bladeHalfWidth, 8f + bladeHalfLength, bladeHeight, 8f + bladeHalfWidth);
    protected static final AABB NORTH_BLADE_BOX = new AABB(
            8f - bladeHalfLength, 8f - bladeHalfWidth, 16f - baseHeight,
            8f + bladeHalfLength, 8f + bladeHalfWidth, 16f - baseHeight - bladeHeight
    );
    protected static final AABB SOUTH_BLADE_BOX = new AABB(
            8f - bladeHalfLength, 8f - bladeHalfWidth, baseHeight,
            8f + bladeHalfLength, 8f + bladeHalfWidth, baseHeight + bladeHeight
    );
    protected static final AABB EAST_BLADE_BOX = new AABB(
            16f - baseHeight, 8f - bladeHalfWidth, 8f - bladeHalfLength,
            16f - baseHeight - bladeHeight, 8f + bladeHalfWidth, 8f + bladeHalfLength
    );
    protected static final AABB WEST_BLADE_BOX = new AABB(
            (baseHeight), 8f - bladeHalfWidth, 8f - bladeHalfLength,
            baseHeight + bladeHeight, 8f + bladeHalfWidth, 8f + bladeHalfLength
    );

    protected static final List<VoxelShape> COLLISION_SHAPES = Arrays.stream(Direction.values())
            .map(direction -> BlockUtils.rotateCuboidFromUp(direction, UPWARD_BASE_BOX))
            .toList();

    protected static final List<VoxelShape> BLADE_SHAPES = Stream.of(
            DOWNWARD_BLADE_BOX,
            UPWARD_BLADE_BOX,
            NORTH_BLADE_BOX,
            SOUTH_BLADE_BOX,
            EAST_BLADE_BOX,
            WEST_BLADE_BOX
    ).map(box -> Block.box(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)).toList();

    protected static final List<VoxelShape> OUTLINE_SHAPES = Arrays.stream(Direction.values())
            .map(direction -> BlockUtils.rotateCuboidFromUp(direction, UPWARD_BASE_BOX)).toList();

    public SawBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        MechPowerBlockBase.super.appendProperties(builder);
        builder.add(FACING);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite()).setValue(MECH_POWERED, false);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);
        // note that we can't validate if the update is required here as the block will have
        // its facing set after being added
        level.scheduleTick(pos, this, powerChangeTickRate);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPES.get(state.getValue(FACING).get3DDataValue());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return OUTLINE_SHAPES.get(state.getValue(FACING).get3DDataValue());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborChanged(state, level, pos, sourceBlock, sourcePos, notify);
        scheduleUpdateIfRequired(level, state, pos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
        super.tick(state, level, pos, random);

        boolean bReceivingPower = isReceivingMechPower(level, state, pos);
        boolean bOn = isMechPowered(state);

        if (bOn != bReceivingPower) {
            emitSawParticles(level, state, pos);

            level.setBlockAndUpdate(pos, state.setValue(MECH_POWERED, bReceivingPower));

            if (bReceivingPower) {
                playBangSound(level, pos);
                // the saw doesn't cut on the update in which it is powered, so check if another
                // update is required
                scheduleUpdateIfRequired(level, state, pos);
            }
        }
        else if (bOn) {
            sawBlockToFront(level, state, pos);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (!isMechPowered(state)) {
            return;
        }
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        // construct bounding box from saw
        if (BLADE_SHAPES.get(state.getValue(FACING).get3DDataValue()).bounds().move(pos)
                .intersects(livingEntity.getLocalBoundsForPose(entity.getPose()).move(livingEntity.position()))) {

            DamageSource damageSource = BwtDamageTypes.of(level, BwtDamageTypes.SAW_DAMAGE_TYPE);
            livingEntity.hurt(damageSource, 4.0f);
        }
    }

    public void dropItemsOnBreak(Level level, BlockPos pos) {
        Containers.dropContents(level, pos, NonNullList.of(
                ItemStack.EMPTY,
                new ItemStack(BwtItems.gearItem, 1),
                new ItemStack(Items.STICK, 2),
                new ItemStack(BwtItems.sawDustItem, 2),
                new ItemStack(Items.IRON_INGOT, 2),
                new ItemStack(BwtItems.strapItem, 2)
        ));
    }

    protected void scheduleUpdateIfRequired(Level level, BlockState state, BlockPos pos) {
        if (isMechPowered(state) != isReceivingMechPower(level, state, pos)) {
            level.scheduleTick(pos, this, powerChangeTickRate);
            return;
        }
        if (!isMechPowered(state)) {
            return;
        }

        // check if we have something to cut in front of us
        BlockPos targetPos = pos.relative(state.getValue(FACING));
        BlockState targetState = level.getBlockState(targetPos);
        if (!targetState.is(BlockTags.AIR)) {
            level.scheduleTick(pos, this, sawTimeBaseTickRate + level.random.nextInt(sawTimeTickRateVariance));
        }
    }


    void emitSawParticles(Level level, BlockState state, BlockPos pos) {
        // compute position of saw blade
        Direction facing = state.getValue(FACING);
        VoxelShape bladeFace = BLADE_SHAPES.get(facing.get3DDataValue()).singleEncompassing().move(pos.getX(), pos.getY(), pos.getZ());
        double bladeMaxX = bladeFace.max(Direction.Axis.X);
        double bladeMaxY = bladeFace.max(Direction.Axis.Y);
        double bladeMaxZ = bladeFace.max(Direction.Axis.Z);
        double bladeMinX = bladeFace.min(Direction.Axis.X);
        double bladeMinY = bladeFace.min(Direction.Axis.Y);
        double bladeMinZ = bladeFace.min(Direction.Axis.Z);
        double fBladeXPos = (bladeMaxX + bladeMinX) / 2;
        double fBladeYPos = (bladeMaxY + bladeMinY) / 2;
        double fBladeZPos = (bladeMaxZ + bladeMinZ) / 2;

        for (int counter = 0; counter < 5; counter++) {
            double smokeX = fBladeXPos + ((level.random.nextFloat() - 0.5f) * (bladeMaxX - bladeMinX));
            double smokeY = fBladeYPos + ((level.random.nextFloat() * 0.10f) * (bladeMaxY - bladeMinY));
            double smokeZ = fBladeZPos + ((level.random.nextFloat() - 0.5f) * (bladeMaxZ - bladeMinZ));
            level.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0d, 0d, 0d);
        }
    }

    protected void sawBlockToFront(Level level, BlockState state, BlockPos pos) {
        BlockPos targetPos = pos.relative(state.getValue(FACING));
        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.is(BlockTags.AIR)) {
            return;
        }

        SawRecipeInput recipeInput = new SawRecipeInput(targetState.getBlock());
        Optional<SawRecipe> recipe = level.getRecipeManager().getRecipeFor(
                BwtRecipes.SAW_RECIPE_TYPE,
                recipeInput,
                level
        ).map(RecipeHolder::value);
        // Cutting
        if (recipe.isEmpty()) {
            if (targetState.getBlock() instanceof LiquidBlock) {
                return;
            }
            if (targetState.is(BwtBlockTags.SAW_BREAKS_NO_DROPS)) {
                level.destroyBlock(targetPos, false);
                playBangSound(level, pos);
                return;
            }
            if (targetState.is(BwtBlockTags.SAW_BREAKS_DROPS_LOOT)) {
                level.destroyBlock(targetPos, true);
                playBangSound(level, pos);
                return;
            }
            if (!targetState.is(BwtBlockTags.SURVIVES_SAW_BLOCK)) {
                breakSaw(level, pos);
            }
            return;
        }

        List<ItemStack> results = recipe.get().getResults();
        BlockIngredient blockIngredient = recipe.get().getIngredient();

        // The companion slab is the only partial block that doesn't just get cut regardless of collision
        if (blockIngredient.test(BwtBlocks.companionSlabBlock) && state.getValue(FACING).getAxis().isHorizontal()) {
            return;
        }

        if (blockIngredient.test(BwtBlocks.companionCubeBlock)) {
            level.playSound(null, pos, BwtSoundEvents.COMPANION_CUBE_DEATH, SoundSource.BLOCKS, 1, 1);
            if (state.getValue(FACING).getAxis().isHorizontal()) {
                results.get(0).setCount(1);
                level.setBlockAndUpdate(targetPos, BwtBlocks.companionSlabBlock.defaultBlockState());
            }
            else {
                level.destroyBlock(targetPos, false);
            }
        }
        else {
            level.destroyBlock(targetPos, false);
        }
        playBangSound(level, pos);

        targetState.getOptionalValue(BlockStateProperties.SLAB_TYPE).filter(property -> property.equals(SlabType.DOUBLE)).ifPresent((property) ->
            results.forEach(stack -> stack.setCount(stack.getCount() * 2))
        );

        Containers.dropContents(level, targetPos, NonNullList.of(ItemStack.EMPTY, results.toArray(new ItemStack[0])));
    }

    public void breakSaw(Level level, BlockPos pos) {
        dropItemsOnBreak(level, pos);
        level.destroyBlock(pos, false);
        playBangSound(level, pos, 1);
    }

    @Override
    public Predicate<Direction> getValidAxleInputFaces(BlockState state, BlockPos pos) {
        return direction -> !direction.equals(state.getValue(FACING));
    }

    @Override
    public Predicate<Direction> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return direction -> false;
    }

//    @Override
//    public void overpower(level level, BlockPos pos) {
//        breakSaw(level, pos);
//    }

    //----------- Client Side Functionality -----------//


    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (isMechPowered(state)) {
            emitSawParticles(level, state, pos);
        }
    }
}