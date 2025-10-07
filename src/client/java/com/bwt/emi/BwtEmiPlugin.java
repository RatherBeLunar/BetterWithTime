package com.bwt.emi;

import com.bwt.BetterWithTime;
import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.mech_hopper.MechHopperBlock;
import com.bwt.emi.recipehandlers.EmiSoulForgeRecipeHandler;
import com.bwt.emi.recipes.*;
import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.IngredientWithCount;
import com.bwt.recipes.cooking_pots.AbstractCookingPotRecipe;
import com.bwt.recipes.hopper_filter.HopperFilterRecipe;
import com.bwt.recipes.soul_bottling.SoulBottlingRecipe;
import com.bwt.utils.Id;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.block.Blocks;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class BwtEmiPlugin implements EmiPlugin {
    public static final Identifier WIDGETS = Id.of("textures/gui/container/emiwidgets.png");


    public static EmiRecipeCategory CAULDRON = category("cauldron", EmiStack.of(BwtBlocks.cauldronBlock));
    public static EmiRecipeCategory STOKED_CAULDRON = category("stoked_cauldron", EmiStack.of(BwtBlocks.cauldronBlock));
    public static EmiRecipeCategory CRUCIBLE = category("crucible", EmiStack.of(BwtBlocks.crucibleBlock));
    public static EmiRecipeCategory STOKED_CRUCIBLE = category("stoked_crucible", EmiStack.of(BwtBlocks.crucibleBlock));
    public static EmiRecipeCategory STOKED_CRUCIBLE_RECLAIM = category("stoked_crucible_reclaim", EmiStack.of(BwtBlocks.crucibleBlock));

    public static EmiRecipeCategory MILL_STONE = category("mill_stone", EmiStack.of(BwtBlocks.millStoneBlock));
    public static EmiRecipeCategory SAW = category("saw", EmiStack.of(BwtBlocks.sawBlock));
    public static EmiRecipeCategory TURNTABLE = category("turntable", EmiStack.of(BwtBlocks.turntableBlock));
    public static EmiRecipeCategory KILN = category("kiln", EmiStack.of(Blocks.BRICKS));
    public static EmiRecipeCategory SOUL_FORGE = category("soul_forge", EmiStack.of(BwtBlocks.soulForgeBlock));
    public static EmiRecipeCategory HOPPER_SOULS = category("hopper_souls", EmiStack.of(BwtBlocks.hopperBlock));
    public static EmiRecipeCategory HOPPER_FILTERING = category("hopper_filtering", EmiStack.of(BwtBlocks.hopperBlock));

    public static EmiRenderable simplifiedEmiStack(EmiStack stack) {
        return stack;
    }

    public static EmiRecipeCategory category(String id, EmiStack icon) {
        return new EmiRecipeCategory(Id.of(id), icon);
    }

    public static EmiRecipeCategory category(String id, EmiStack icon, Comparator<EmiRecipe> comp) {
        return new EmiRecipeCategory(Identifier.of("btw", id), icon,
                new EmiTexture(Identifier.of("emi", "textures/simple_icons/" + id + ".png"), 0, 0, 16, 16, 16, 16, 16, 16), comp);
    }


    private static <C extends RecipeInput, T extends Recipe<C>> List<RecipeEntry<T>> getRecipes(EmiRegistry registry, RecipeType<T> type) {
        return registry.getRecipeManager().listAllOfType(type);
    }

    private static <C extends RecipeInput, T extends CraftingRecipe> List<RecipeEntry<T>> getRecipes(EmiRegistry registry, RecipeType<T> type, Predicate<CraftingRecipeCategory> category) {
        return registry.getRecipeManager().listAllOfType(type).stream().filter(r -> category.test(r.value().getCategory())).toList();
    }


    @Override
    public void register(EmiRegistry reg) {
        reg.addCategory(CAULDRON);
        reg.addCategory(STOKED_CAULDRON);
        reg.addCategory(CRUCIBLE);
        reg.addCategory(STOKED_CRUCIBLE);
        reg.addCategory(STOKED_CRUCIBLE_RECLAIM);
        reg.addCategory(MILL_STONE);
        reg.addCategory(SAW);
        reg.addCategory(TURNTABLE);
        reg.addCategory(KILN);
        reg.addCategory(SOUL_FORGE);
        reg.addCategory(HOPPER_SOULS);
        reg.addCategory(HOPPER_FILTERING);


//        reg.addRecipeHandler(BetterWithTime.cauldronScreenHandler, new EmiCookingPotRecipeHandler<>(CAULDRON));
//        reg.addRecipeHandler(BetterWithTime.cauldronScreenHandler, new EmiCookingPotRecipeHandler<>(STOKED_CAULDRON));
        reg.addRecipeHandler(BetterWithTime.soulForgeScreenHandler, new EmiSoulForgeRecipeHandler.FourByFour());
        reg.addRecipeHandler(BetterWithTime.soulForgeScreenHandler, new EmiSoulForgeRecipeHandler.ThreeByThree());

        reg.addWorkstation(CAULDRON, EmiStack.of(BwtBlocks.cauldronBlock));
        reg.addWorkstation(STOKED_CAULDRON, EmiStack.of(BwtBlocks.cauldronBlock));
        reg.addWorkstation(CRUCIBLE, EmiStack.of(BwtBlocks.crucibleBlock));
        reg.addWorkstation(STOKED_CRUCIBLE, EmiStack.of(BwtBlocks.crucibleBlock));
        reg.addWorkstation(MILL_STONE, EmiStack.of(BwtBlocks.millStoneBlock));
        reg.addWorkstation(SAW, EmiStack.of(BwtBlocks.sawBlock));
        reg.addWorkstation(TURNTABLE, EmiStack.of(BwtBlocks.turntableBlock));
        reg.addWorkstation(KILN, EmiStack.of(Blocks.BRICKS));
        reg.addWorkstation(SOUL_FORGE, EmiStack.of(BwtBlocks.soulForgeBlock));
        reg.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(BwtBlocks.soulForgeBlock));
        reg.addWorkstation(HOPPER_SOULS, EmiStack.of(BwtBlocks.hopperBlock));
        reg.addWorkstation(HOPPER_FILTERING, EmiStack.of(BwtBlocks.hopperBlock));

        getRecipes(reg, BwtRecipes.CAULDRON_RECIPE_TYPE).stream()
                .map(recipeEntry -> new EmiCookingPotRecipe<>(CAULDRON, recipeEntry))
                .forEach(reg::addRecipe);
        getRecipes(reg, BwtRecipes.STOKED_CAULDRON_RECIPE_TYPE).stream()
                .map(recipeEntry -> new EmiCookingPotRecipe<>(STOKED_CAULDRON, recipeEntry))
                .forEach(reg::addRecipe);
        getRecipes(reg, BwtRecipes.CRUCIBLE_RECIPE_TYPE).stream()
                .map(recipeEntry -> new EmiCookingPotRecipe<>(CRUCIBLE, recipeEntry))
                .forEach(reg::addRecipe);
        getRecipes(reg, BwtRecipes.STOKED_CRUCIBLE_RECIPE_TYPE).stream()
                .map(recipeEntry -> new EmiCookingPotRecipe<>(
                        recipeEntry.value().getCategory().equals(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM)
                                ? STOKED_CRUCIBLE_RECLAIM
                                : STOKED_CRUCIBLE,
                        recipeEntry)
                ).forEach(reg::addRecipe);
        getRecipes(reg, BwtRecipes.MILL_STONE_RECIPE_TYPE).stream()
                .map(recipeEntry -> new EmiMillstoneRecipe(MILL_STONE, recipeEntry))
                .forEach(reg::addRecipe);
        getRecipes(reg, BwtRecipes.SAW_RECIPE_TYPE).stream()
                .map(recipeEntry -> new EmiSawRecipe(SAW, recipeEntry))
                .forEach(reg::addRecipe);
        getRecipes(reg, BwtRecipes.TURNTABLE_RECIPE_TYPE).stream()
                .map(recipeEntry -> new EmiTurntableRecipe(TURNTABLE, recipeEntry))
                .forEach(reg::addRecipe);
        getRecipes(reg, BwtRecipes.KILN_RECIPE_TYPE).stream()
                .map(recipeEntry -> new EmiKilnRecipe(KILN, recipeEntry))
                .forEach(reg::addRecipe);
        getRecipes(reg, BwtRecipes.SOUL_FORGE_RECIPE_TYPE, c -> c != CraftingRecipeCategory.BUILDING).stream()
                .sorted(Comparator.comparingInt(r -> r.value().getCategory().ordinal()))
                .map(EmiSoulForgeRecipe::new)
                .forEach(reg::addRecipe);
        getRecipes(reg, BwtRecipes.SOUL_FORGE_RECIPE_TYPE, c -> c == CraftingRecipeCategory.BUILDING).stream()
                .map(EmiSoulForgeRecipe::new)
                .forEach(reg::addRecipe);
        List<RecipeEntry<HopperFilterRecipe>> hopperFilterRecipes = getRecipes(reg, BwtRecipes.HOPPER_FILTER_RECIPE_TYPE);
        List<RecipeEntry<SoulBottlingRecipe>> soulBottlingRecipes = getRecipes(reg, BwtRecipes.SOUL_BOTTLING_RECIPE_TYPE);

        Stream<RecipeEntry<HopperFilterRecipe>> hopperFilterRecipesNoSouls = hopperFilterRecipes.stream()
                .filter(r -> r.value().soulCount() == 0);

        hopperFilterRecipesNoSouls
                .map(r -> new EmiHopperFilterRecipe(HOPPER_FILTERING, r))
                .forEach(reg::addRecipe);

        hopperFilterRecipes.stream()
                .filter(r -> r.value().soulCount() >= 1)
                .forEach(hopperFilterRecipeEntry -> {
                    reg.addRecipe(new EmiHopperFilterRecipe(HOPPER_SOULS, hopperFilterRecipeEntry));
                    soulBottlingRecipes.stream()
                            .map(soulBottleRecipeEntry -> new EmiHopperFilterRecipe(HOPPER_SOULS, hopperFilterRecipeEntry).withSoulBottlingRecipe(soulBottleRecipeEntry))
                            .forEach(reg::addRecipe);
                });


        MechHopperBlock.filterMap.forEach((filter, permitted) -> {
            if (permitted instanceof MechHopperBlock.TagFilter f) {
                var emiPermitted = EmiIngredient.of(f.tagKey());
                Identifier id = Id.of(Registries.ITEM.getId(filter).getPath() + "_hopper_filter");
                reg.addRecipe(new EmiHopperFilterPermitList(id, EmiStack.of(filter), emiPermitted));
            }
        });
    }

    public static EmiIngredient from(IngredientWithCount ingredientWithCount) {
        return EmiIngredient.of(ingredientWithCount.ingredient(), ingredientWithCount.count());
    }

    public static EmiIngredient from(BlockIngredient blockIngredient) {
        List<EmiIngredient> ingredientList = new ArrayList<>();
        blockIngredient.dualStreamEntries()
                .map(either -> either
                        .mapBoth(BlockIngredient.BlockEntry::block, BlockIngredient.TagEntry::tag)
                        .map(EmiStack::of, EmiIngredient::of)
                ).forEach(ingredientList::add);
        return EmiIngredient.of(ingredientList);
    }

}

