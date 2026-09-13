package com.bwt.blocks;

import com.bwt.blocks.unfired_pottery.UnfiredPotteryBlock;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.kiln.KilnRecipe;
import com.bwt.recipes.kiln.KilnRecipeInput;
import com.bwt.utils.FireDataCluster;
import com.bwt.utils.kiln_block_cook_overlay.KilnBlockCookOverlay;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class KilnBlock extends Block {
    private static final int minFireFactorBaseTickRate = 20; // FC value 40
    private static final int maxFireFactorBaseTickRate = 80; // FC value 160

    public static final IntegerProperty COOK_TIME = IntegerProperty.create("cook_time", 0, 15);

    public KilnBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COOK_TIME);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);
        Optional<KilnRecipe> recipe = getRecipe(level, level.getBlockState(pos.above()));
        recipe.ifPresent(kilnRecipe -> scheduleUpdateBasedOnCookState(level, pos, kilnRecipe));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        super.onRemove(state, level, pos, newState, moved);
        if (!newState.is(this)) {
            resetBlockCookProgress(level, pos);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);

        int oldCookCounter = state.getValue(COOK_TIME);
        int newCookCounter = 0;

        BlockPos cookingBlockPos = pos.above();
        BlockState cookingBlockState = level.getBlockState(cookingBlockPos);
        Optional<KilnRecipe> recipe = getRecipe(level, cookingBlockState);
        if (recipe.isPresent()) {
            if (checkKilnIntegrity(level, pos)) {
                if (oldCookCounter >= 15) {
                    cookBlock(level, pos.above(), cookingBlockState, recipe.get());
                }
                else {
                    newCookCounter = oldCookCounter + 1;
                    scheduleUpdateBasedOnCookState(level, pos, recipe.get());
                }
            }
            else {
                // if we have a valid cook block above, we have to reschedule another tick
                // regardless of other factors, because the shape of the kiln can change without
                // an immediate neighbor changing, causing the cook process to restart
                scheduleUpdateBasedOnCookState(level, pos, recipe.get());
            }
        }

        if (oldCookCounter != newCookCounter) {
            level.setBlockAndUpdate(pos, state.setValue(COOK_TIME, newCookCounter));
            // The 10 here is the max block breaking progress
            KilnBlockCookOverlay.setKilnBlockCookingInfo(level, pos.above(), ((int) Mth.clampedLerp(-1, 9, ((float) newCookCounter) / 15f)));
            if (cookingBlockState.hasProperty(UnfiredPotteryBlock.COOKING) && cookingBlockState.getValue(UnfiredPotteryBlock.COOKING).equals(false)) {
                level.setBlockAndUpdate(cookingBlockPos, cookingBlockState.setValue(UnfiredPotteryBlock.COOKING, true));
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (neighborPos.equals(pos.below()) && !neighborState.is(BwtBlocks.stokedFireBlock)) {
            // we don't have a stoked fire beneath us, so revert to regular brick
            return Blocks.BRICKS.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborChanged(state, level, pos, sourceBlock, sourcePos, notify);
        Optional<KilnRecipe> recipe = getRecipe(level, level.getBlockState(pos.above()));
        if (recipe.isPresent()) {
            scheduleUpdateBasedOnCookState(level, pos, recipe.get());
        }
        else if (state.getValue(COOK_TIME) > 0) {
            // reset the cook counter so it doesn't get passed to another block on piston push
            resetBlockCookProgress(level, pos);
            level.setBlockAndUpdate(pos, state.setValue(COOK_TIME, 0));
        }
    }

    protected void scheduleUpdateBasedOnCookState(Level level, BlockPos pos, KilnRecipe recipe) {
        int iTickRate = computeTickRateBasedOnFireFactor(level, pos);

        iTickRate *= recipe.getCookingTime();

        level.scheduleTick(pos, this, iTickRate);
    }

    private Optional<KilnRecipe> getRecipe(Level level, BlockState cookingBlockState) {
        KilnRecipeInput recipeInput = new KilnRecipeInput(cookingBlockState.getBlock());
        return level.getRecipeManager().getRecipeFor(
                BwtRecipes.KILN_RECIPE_TYPE,
                recipeInput,
                level
        ).map(RecipeHolder::value);
    }

    private void cookBlock(Level level, BlockPos cookingBlockPos, BlockState cookingBlockState, KilnRecipe recipe) {
        DataComponentMap components = cookingBlockState.getBlock().getCloneItemStack(level, cookingBlockPos, cookingBlockState).getComponents();
        level.destroyBlock(cookingBlockPos, false);
        NonNullList<ItemStack> drops = NonNullList.of(ItemStack.EMPTY, recipe.getDrops().stream().map(ItemStack::copy).toArray(ItemStack[]::new));
        if (drops.isEmpty()) {
            return;
        }
        ItemStack firstDrop = drops.get(0);
        firstDrop.applyComponents(components.filter(componentType -> firstDrop.getComponents().has(componentType)));
        Containers.dropContents(level, cookingBlockPos, drops);
    }

    private void resetBlockCookProgress(Level level, BlockPos kilnPos) {
        BlockPos cookingBlockPos = kilnPos.above();
        BlockState cookingBlockState = level.getBlockState(cookingBlockPos);
        if (cookingBlockState.hasProperty(UnfiredPotteryBlock.COOKING) && cookingBlockState.getValue(UnfiredPotteryBlock.COOKING).equals(true)) {
            level.setBlockAndUpdate(cookingBlockPos, cookingBlockState.setValue(UnfiredPotteryBlock.COOKING, false));
        }
        KilnBlockCookOverlay.setKilnBlockCookingInfo(level, cookingBlockPos, -1);
    }

    private boolean checkKilnIntegrity(Level level, BlockPos pos) {
        BlockPos center = pos.above();
        return Arrays.stream(Direction.values())
                .map(center::relative)
                .filter(structurePos -> !structurePos.equals(pos))
                .filter(structurePos -> level.getBlockState(structurePos).is(Blocks.BRICKS))
                .count() >= 3;
    }

    private int computeTickRateBasedOnFireFactor(Level level, BlockPos pos) {
        FireDataCluster fireDataCluster = FireDataCluster.fromWorld(level, pos);
        int additionalFireCount = fireDataCluster.getStokedCount() - 1;
//        return maxFireFactorBaseTickRate - (additionalFireCount / 8) * (maxFireFactorBaseTickRate - minFireFactorBaseTickRate);
        return ( ( maxFireFactorBaseTickRate - minFireFactorBaseTickRate ) *
                ( 8 - additionalFireCount ) / 8 ) + minFireFactorBaseTickRate;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return Blocks.BRICKS.getCloneItemStack(level, pos, state);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        BlockState blockAboveState = level.getBlockState(pos.above());
        if (blockAboveState.is(BlockTags.AIR)) {
            return;
        }
        Optional<KilnRecipe> recipe = getRecipe(level, blockAboveState);
        if (recipe.isEmpty()) {
            return;
        }
        if (!checkKilnIntegrity(level, pos)) {
            return;
        }

        if (!blockAboveState.isSolidRender(level, pos)) {
            for (int count = 0; count < 2; count++) {
                double xPos = pos.getX() + random.nextDouble();
                double yPos = pos.getY() + 1d + (random.nextDouble() * 0.75d);
                double zPos = pos.getZ() + random.nextDouble();

                level.addParticle(ParticleTypes.WHITE_SMOKE, xPos, yPos, zPos, 0d, 0d, 0d);
            }
        }
        else {
            for (Direction direction : Direction.values()) {
                if (direction.getAxis().isVertical()) {
                    continue;
                }
                double xPos = pos.getX() + 0.5d;
                double yPos = pos.getY() + 1d + (random.nextDouble() * 0.75d);
                double zPos = pos.getZ() + 0.5d;

                double dFacingOffset = 0.75d;
                double dHorizontalOffset = -0.75d + (random.nextDouble() * 1.5d);

                // negative z
                if (direction == Direction.NORTH) {
                    xPos += dHorizontalOffset;
                    zPos -= dFacingOffset;
                }
                // positive z
                else if (direction == Direction.SOUTH) {
                    xPos += dHorizontalOffset;
                    zPos += dFacingOffset;
                }
                // negative x
                else if (direction == Direction.WEST) {
                    xPos -= dFacingOffset;
                    zPos += dHorizontalOffset;
                }
                // positive i
                else {
                    xPos += dFacingOffset;
                    zPos += dHorizontalOffset;
                }

                level.addParticle(ParticleTypes.WHITE_SMOKE, xPos, yPos, zPos, 0d, 0d, 0d);
            }
        }
    }
}
