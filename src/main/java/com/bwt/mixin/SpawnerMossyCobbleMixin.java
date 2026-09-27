package com.bwt.mixin;

import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.mob_spawner_conversion.MobSpawnerConversionRecipe;
import com.bwt.recipes.mob_spawner_conversion.MobSpawnerConversionRecipeInput;
import com.google.common.collect.Maps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

@Mixin(BaseSpawner.class)
public class SpawnerMossyCobbleMixin {
    @Unique
    private static final Map<BlockState, BlockState> REGULAR_TO_MOSSY_STATE_MAP = Maps.newIdentityHashMap();

    @Unique
    private static <T extends Comparable<T>> BlockState copyProperty(Property<T> property, BlockState fromState, BlockState toState) {
        if (toState.hasProperty(property)) {
            return toState.setValue(property, fromState.getValue(property));
        }
        return toState;
    }

    @Unique
    private static BlockState copyProperties(BlockState fromState, Block toBlock) {
        return REGULAR_TO_MOSSY_STATE_MAP.computeIfAbsent(fromState, innerFromState -> {
            BlockState toState = toBlock.defaultBlockState();

            for (Property<?> property : innerFromState.getProperties()) {
                toState = copyProperty(property, innerFromState, toState);
            }

            return toState;
        });
    }

    @Inject(method = "serverTick", at = @At("HEAD"))
    protected void bwt$createMossyCobblestone(ServerLevel serverLevel, BlockPos blockPos, CallbackInfo ci) {
        if (serverLevel.random.nextInt(1200) != 0) {
            return;
        }
        BlockPos randomPos = new BlockPos(
                blockPos.getX() + serverLevel.random.nextIntBetweenInclusive(-4, 4),
                blockPos.getY() + serverLevel.random.nextIntBetweenInclusive(-1, 4),
                blockPos.getZ() + serverLevel.random.nextIntBetweenInclusive(-4, 4)
        );
        BlockState blockState = serverLevel.getBlockState(randomPos);
        if (blockState.isAir()) {
            return;
        }
        MobSpawnerConversionRecipeInput recipeInput = new MobSpawnerConversionRecipeInput(blockState.getBlock());
        Optional<MobSpawnerConversionRecipe> recipe = serverLevel.getRecipeManager().getRecipeFor(
                BwtRecipes.MOB_SPAWNER_CONVERSION_RECIPE_TYPE,
                recipeInput,
                serverLevel
        ).map(RecipeHolder::value);
        if (recipe.isEmpty()) {
            return;
        }

        Block blockToPlace = recipe.get().getResult();
        BlockState newState = copyProperties(blockState, blockToPlace);
        serverLevel.setBlockAndUpdate(randomPos, newState);
    }
}
