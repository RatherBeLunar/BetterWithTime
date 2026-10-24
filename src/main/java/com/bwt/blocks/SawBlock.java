package com.bwt.blocks;

import com.bwt.damage_types.BwtDamageTypes;
import com.bwt.items.BwtItems;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.saw.SawRecipe;
import com.bwt.recipes.saw.SawRecipeInput;
import com.bwt.recipes.saw.StateDependentSawRecipeHelper;
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
import net.minecraft.world.entity.InsideBlockEffectApplier;
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
import net.minecraft.world.level.redstone.Orientation;
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
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean bl) {
        scheduleUpdateIfRequired(level, state, pos);
        BlockPos targetPos = pos.relative(state.getValue(FACING));
        BlockState targetState = level.getBlockState(targetPos);

        if (!targetState.is(BlockTags.AIR) && isMechPowered(state)) {
            level.playSound(null, pos, BwtSoundEvents.SAW_CUT, SoundSource.BLOCKS, 1f, 1f);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel serverLevel, BlockPos pos, net.minecraft.util.RandomSource random) {
        super.tick(state, serverLevel, pos, random);

        boolean receivingPower = isReceivingMechPower(serverLevel, state, pos);
        boolean isOn = isMechPowered(state);

        if (isOn != receivingPower) {
            emitSawParticles(serverLevel, state, pos);

            serverLevel.setBlockAndUpdate(pos, state.setValue(MECH_POWERED, receivingPower));

            if (receivingPower) {
                serverLevel.playSound(null, pos, BwtSoundEvents.SAW_INITIALIZE, SoundSource.BLOCKS, 1f, 1);
                // the saw doesn't cut on the update in which it is powered, so check if another
                // update is required
                scheduleUpdateIfRequired(serverLevel, state, pos);
            }
            else {
                serverLevel.playSound(null, pos, BwtSoundEvents.SAW_DEPOWERED, SoundSource.BLOCKS, 1f, 1);
            }
        }
        else if (isOn) {
            sawBlockToFront(serverLevel, state, pos);
        }
    }

    @Override
    protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier, boolean bl) {
        super.entityInside(blockState, level, blockPos, entity, insideBlockEffectApplier, bl);
        if (!isMechPowered(blockState)) {
            return;
        }
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        // construct bounding box from saw
        if (BLADE_SHAPES.get(blockState.getValue(FACING).get3DDataValue()).bounds().move(blockPos)
                .intersects(livingEntity.getLocalBoundsForPose(entity.getPose()).move(livingEntity.position()))) {

            DamageSource damageSource = BwtDamageTypes.of(level, BwtDamageTypes.SAW_DAMAGE_TYPE);
            if (livingEntity.hurtOrSimulate(damageSource, 4.0f)) {
                level.playSound(null, blockPos, BwtSoundEvents.SAW_CUT, SoundSource.BLOCKS, 1f, 1);
            }
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

    protected void sawBlockToFront(ServerLevel serverLevel, BlockState sawState, BlockPos pos) {
        BlockPos targetPos = pos.relative(sawState.getValue(FACING));
        BlockState targetState = serverLevel.getBlockState(targetPos);

        if (targetState.is(BlockTags.AIR)) {
            return;
        }

        SawRecipeInput recipeInput = new SawRecipeInput(targetState.getBlock());

        Optional<SawRecipe> recipe = serverLevel.recipeAccess().getRecipeFor(
                BwtRecipes.SAW_RECIPE_TYPE,
                recipeInput,
                serverLevel
        ).map(RecipeHolder::value);
        // Cutting
        if (recipe.isEmpty()) {
            if (targetState.getBlock() instanceof LiquidBlock) {
                return;
            }
            if (targetState.is(BwtBlockTags.SAW_BREAKS_NO_DROPS)) {
                serverLevel.destroyBlock(targetPos, false);
                return;
            }
            if (targetState.is(BwtBlockTags.SAW_BREAKS_DROPS_LOOT)) {
                serverLevel.destroyBlock(targetPos, true);
                return;
            }
            if (!targetState.is(BwtBlockTags.SURVIVES_SAW_BLOCK)) {
                breakSaw(serverLevel, pos);
            }
            return;
        }

        // The companion slab is the only partial block that doesn't just get cut regardless of collision
        if (targetState.is(BwtBlocks.companionSlabBlock) && sawState.getValue(FACING).getAxis().isHorizontal()) {
            return;
        }

        if (targetState.is(BwtBlocks.companionCubeBlock)) {
            serverLevel.playSound(null, pos, BwtSoundEvents.COMPANION_CUBE_DEATH, SoundSource.BLOCKS, 1, 1);
            if (sawState.getValue(FACING).getAxis().isHorizontal()) {
                serverLevel.setBlockAndUpdate(targetPos, BwtBlocks.companionSlabBlock.defaultBlockState());
            }
            else {
                serverLevel.destroyBlock(targetPos, false);
            }
        }
        else {
            serverLevel.destroyBlock(targetPos, false);
        }

        List<ItemStack> results = StateDependentSawRecipeHelper.getResults(serverLevel, targetState, targetPos, sawState, recipe.get());

        Containers.dropContents(serverLevel, targetPos, NonNullList.of(ItemStack.EMPTY, results.toArray(new ItemStack[0])));
    }

    public void breakSaw(Level level, BlockPos pos) {
        dropItemsOnBreak(level, pos);
        level.destroyBlock(pos, false);
        level.playSound(null, pos, BwtSoundEvents.MECH_EXPLODE, SoundSource.BLOCKS, 1f, 1f);
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