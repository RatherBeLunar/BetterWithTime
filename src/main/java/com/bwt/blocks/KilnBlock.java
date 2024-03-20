package com.bwt.blocks;

import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.KilnRecipe;
import com.bwt.utils.FireData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public class KilnBlock extends Block {
    private static final int minFireFactorBaseTickRate = 20; // FC value 40
    private static final int maxFireFactorBaseTickRate = 80; // FC value 160

    public static final IntProperty COOK_TIME = IntProperty.of("cook_time", 0, 15);

    public KilnBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(COOK_TIME);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        Optional<KilnRecipe> recipe = getRecipe(world, world.getBlockState(pos.up()));
        recipe.ifPresent(kilnRecipe -> scheduleUpdateBasedOnCookState(world, pos, kilnRecipe));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (!newState.isOf(this)) {
            resetBlockCookProgress(world, pos);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        int oldCookCounter = state.get(COOK_TIME);
        int newCookCounter = 0;

        BlockPos cookingBlockPos = pos.up();
        BlockState cookingBlockState = world.getBlockState(cookingBlockPos);
        Optional<KilnRecipe> recipe = getRecipe(world, cookingBlockState);
        if (recipe.isPresent()) {
            if (checkKilnIntegrity(world, pos)) {
                if (oldCookCounter >= 15) {
                    cookBlock(world, pos.up(), recipe.get());
                }
                else {
                    newCookCounter = oldCookCounter + 1;
                    scheduleUpdateBasedOnCookState(world, pos, recipe.get());
                }
            }
            else {
                // if we have a valid cook block above, we have to reschedule another tick
                // regardless of other factors, because the shape of the kiln can change without
                // an immediate neighbor changing, causing the cook process to restart
                scheduleUpdateBasedOnCookState(world, pos, recipe.get());
            }
        }

        if (oldCookCounter != newCookCounter) {
            world.setBlockState(pos, state.with(COOK_TIME, newCookCounter));
            // The 10 here is the max block breaking progress
            world.setBlockBreakingInfo(0, pos.up(), ((int) MathHelper.clampedLerp(-1, 9, ((float) newCookCounter) / 15f)));
            if (cookingBlockState.contains(UnfiredPotteryBlock.COOKING) && cookingBlockState.get(UnfiredPotteryBlock.COOKING).equals(false)) {
                world.setBlockState(cookingBlockPos, cookingBlockState.with(UnfiredPotteryBlock.COOKING, true));
            }
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (neighborPos.equals(pos.down()) && !neighborState.isOf(BwtBlocks.stokedFireBlock)) {
            // we don't have a stoked fire beneath us, so revert to regular brick
            return Blocks.BRICKS.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        Optional<KilnRecipe> recipe = getRecipe(world, world.getBlockState(pos.up()));
        if (recipe.isPresent()) {
            scheduleUpdateBasedOnCookState(world, pos, recipe.get());
        }
        else if (state.get(COOK_TIME) > 0) {
            // reset the cook counter so it doesn't get passed to another block on piston push
            resetBlockCookProgress(world, pos);
            world.setBlockState(pos, state.with(COOK_TIME, 0));
        }
    }

    protected void scheduleUpdateBasedOnCookState(World world, BlockPos pos, KilnRecipe recipe) {
        int iTickRate = computeTickRateBasedOnFireFactor(world, pos);

        iTickRate *= recipe.getCookingTime();

        world.scheduleBlockTick(pos, this, iTickRate);
    }

    private Optional<KilnRecipe> getRecipe(World world, BlockState cookingBlockState) {
        RecipeManager recipeManager = world.getRecipeManager();
        return recipeManager.listAllOfType(BwtRecipes.KILN_RECIPE_TYPE).stream()
                .map(RecipeEntry::value)
                .filter(kilnRecipe -> kilnRecipe.matches(cookingBlockState.getBlock()))
                .findFirst();
    }

    private void cookBlock(World world, BlockPos cookingBlockPos, KilnRecipe recipe) {
        world.breakBlock(cookingBlockPos, false);
        ItemScatterer.spawn(world, cookingBlockPos, recipe.getDrops());
    }

    private void resetBlockCookProgress(World world, BlockPos kilnPos) {
        BlockPos cookingBlockPos = kilnPos.up();
        BlockState cookingBlockState = world.getBlockState(cookingBlockPos);
        if (cookingBlockState.contains(UnfiredPotteryBlock.COOKING) && cookingBlockState.get(UnfiredPotteryBlock.COOKING).equals(true)) {
            world.setBlockState(cookingBlockPos, cookingBlockState.with(UnfiredPotteryBlock.COOKING, false));
        }
        world.setBlockBreakingInfo(0, cookingBlockPos, -1);
    }

    private boolean checkKilnIntegrity(World world, BlockPos pos) {
        BlockPos center = pos.up();
        return Arrays.stream(Direction.values())
                .map(center::offset)
                .filter(structurePos -> !structurePos.equals(pos))
                .filter(structurePos -> world.getBlockState(structurePos).isOf(Blocks.BRICKS))
                .count() >= 3;
    }

    private int computeTickRateBasedOnFireFactor(World world, BlockPos pos) {
        FireData fireData = FireData.fromWorld(world, pos);
        int additionalFireCount = (fireData.fireFactor() - FireData.primaryFireFactor) / FireData.secondaryFireFactor;
//        return maxFireFactorBaseTickRate - (additionalFireCount / 8) * (maxFireFactorBaseTickRate - minFireFactorBaseTickRate);
        return ( ( maxFireFactorBaseTickRate - minFireFactorBaseTickRate ) *
                ( 8 - additionalFireCount ) / 8 ) + minFireFactorBaseTickRate;
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return Blocks.BRICKS.getPickStack(world, pos, state);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        BlockState blockAboveState = world.getBlockState(pos.up());
        if (blockAboveState.isAir()) {
            return;
        }
        Optional<KilnRecipe> recipe = getRecipe(world, blockAboveState);
        if (recipe.isEmpty()) {
            return;
        }
        if (!checkKilnIntegrity(world, pos)) {
            return;
        }

        if (!blockAboveState.isOpaqueFullCube(world, pos)) {
            for (int count = 0; count < 2; count++) {
                double xPos = pos.getX() + random.nextDouble();
                double yPos = pos.getY() + 1d + (random.nextDouble() * 0.75d);
                double zPos = pos.getZ() + random.nextDouble();

                world.addParticle(ParticleTypes.WHITE_SMOKE, xPos, yPos, zPos, 0d, 0d, 0d);
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

                world.addParticle(ParticleTypes.WHITE_SMOKE, xPos, yPos, zPos, 0d, 0d, 0d);
            }
        }
    }
}
