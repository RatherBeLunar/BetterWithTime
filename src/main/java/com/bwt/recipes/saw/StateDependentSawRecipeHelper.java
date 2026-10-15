package com.bwt.recipes.saw;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.SawBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;

import javax.swing.text.html.Option;
import java.util.*;

public interface StateDependentSawRecipeHelper {
    interface StateDependentRecipeResultsGetter {
        StateDependentRecipeResultsGetter DEFAULT = (serverLevel, state, pos, sawState, recipe) -> recipe.getResults();

        List<ItemStack> getResults(ServerLevel serverLevel, BlockState state, BlockPos pos, BlockState sawState, SawRecipe recipe);
    }

    interface StatePropertyDependentRecipeResultsGetter<T extends Comparable<T>> {
        List<ItemStack> getResults(ServerLevel serverLevel, BlockState state, BlockPos pos, BlockState sawState, SawRecipe recipe, T propertyValue);
    }

    class PropertyResultsGetterWrapper<T extends Comparable<T>> implements StateDependentRecipeResultsGetter {
        private final Property<T> property;
        StatePropertyDependentRecipeResultsGetter<T> recipeResultsGetter;

        public PropertyResultsGetterWrapper(Property<T> property, StatePropertyDependentRecipeResultsGetter<T> recipeResultsGetter) {
            this.property = property;
            this.recipeResultsGetter = recipeResultsGetter;
        }

        @Override
        public List<ItemStack> getResults(ServerLevel serverLevel, BlockState state, BlockPos pos, BlockState sawState, SawRecipe recipe) {
            return recipeResultsGetter.getResults(serverLevel, state, pos, sawState, recipe, state.getValue(property));
        }

        boolean shouldTest(BlockState state) {
            return state.hasProperty(property);
        }
    }

    HashMap<Class<? extends Block>, StateDependentRecipeResultsGetter> classResultsGetters = new HashMap<>();
    HashMap<Block, StateDependentRecipeResultsGetter> blockResultsGetters = new HashMap<>();
    ArrayList<PropertyResultsGetterWrapper<?>> hasPropertyResultsGetters = new ArrayList<>();

    static void register(Class<? extends Block> blockClass, StateDependentRecipeResultsGetter classResultGetter) {
        classResultsGetters.put(blockClass, classResultGetter);
    }

    static void register(Block block, StateDependentRecipeResultsGetter blockResultsGetter) {
        blockResultsGetters.put(block, blockResultsGetter);
    }

    static <T extends Comparable<T>> void register(Property<T> property, StatePropertyDependentRecipeResultsGetter<T> propertyResultsGetter) {
        hasPropertyResultsGetters.add(new PropertyResultsGetterWrapper<>(property, propertyResultsGetter));
    }

    static List<ItemStack> getResults(ServerLevel serverLevel, BlockState state, BlockPos pos, BlockState sawState, SawRecipe recipe) {
        Block block = state.getBlock();

        Optional<StateDependentRecipeResultsGetter> optional = blockResultsGetters.entrySet().stream()
                .filter(entry -> entry.getKey().equals(block))
                .findAny()
                .map(Map.Entry::getValue);

        if (optional.isEmpty()) {
            optional = classResultsGetters.entrySet().stream()
                    .filter(entry -> entry.getKey().isInstance(block))
                    .findAny()
                    .map(Map.Entry::getValue);
        }
        if (optional.isEmpty()) {
            for (PropertyResultsGetterWrapper<?> propertyResultsGetter : hasPropertyResultsGetters) {
                if (propertyResultsGetter.shouldTest(state)) {
                    optional = Optional.of(propertyResultsGetter);
                    break;
                }
            }
        }
        return optional.orElse(StateDependentRecipeResultsGetter.DEFAULT).getResults(serverLevel, state, pos, sawState, recipe);
    }

    static void registerDefaults() {
        register(BlockStateProperties.SLAB_TYPE, (serverLevel, state, pos, sawState, recipe, slabType) -> {
            List<ItemStack> results = recipe.getResults();
            if (slabType.equals(SlabType.DOUBLE)) {
                results.forEach(stack -> stack.setCount(stack.getCount() * 2));
            }
            return results;
        });

        register(BwtBlocks.companionCubeBlock, (serverLevel, state, pos, sawState, recipe) -> {
            List<ItemStack> results = recipe.getResults();
            if (sawState.getValue(SawBlock.FACING).getAxis().isHorizontal()) {
                results.getFirst().setCount(1);
            }
            return results;
        });
    }
}
