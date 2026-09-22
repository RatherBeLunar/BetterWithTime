package com.bwt.jei_plugin.ingredients;

import com.bwt.recipes.BlockIngredient;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BwtIngredientTypes {
    public static final IIngredientTypeWithSubtypes<Block, BlockIngredient> BLOCK_INGREDIENT = new IIngredientTypeWithSubtypes<>() {
        @Override
        public @NotNull String getUid() {
            return "block_ingredient";
        }

        @Override
        public @NotNull Class<? extends BlockIngredient> getIngredientClass() {
            return BlockIngredient.class;
        }

        @Override
        public @NotNull Optional<BlockIngredient> getRepresentativeIngredient() {
            return Optional.of(BlockIngredient.fromBlock(Blocks.OAK_LOG));
        }

        @Override
        public @NotNull Class<? extends Block> getIngredientBaseClass() {
            return Block.class;
        }

        @Override
        public @NotNull Block getBase(BlockIngredient ingredient) {
            return ingredient.optionalBlock().orElse(Blocks.AIR);
        }

        @Override
        public @NotNull BlockIngredient getDefaultIngredient(Block base) {
            return BlockIngredient.fromBlock(base);
        }
    };
}
