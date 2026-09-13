package com.bwt.emi.recipes;

import com.bwt.emi.BwtEmiPlugin;
import com.bwt.recipes.soul_bottling.SoulBottlingRecipe;
import com.bwt.utils.Id;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class EmiSoulBottlingRecipe implements EmiRecipe {

    public static final EmiTexture FULL_GEAR = new EmiTexture(BwtEmiPlugin.WIDGETS, 14, 0, 14, 14);
    public static final ResourceLocation BACKGROUND = Id.of("textures/gui/container/hopper_recipe.png");

    private final ResourceLocation id;
    protected final EmiIngredient bottle;
    protected final int soulCount;
    protected final EmiStack result;

    public EmiSoulBottlingRecipe(RecipeHolder<SoulBottlingRecipe> recipeEntry) {
        this(recipeEntry.id(), recipeEntry.value());
    }

    public EmiSoulBottlingRecipe(ResourceLocation id, SoulBottlingRecipe recipe) {
        this(id, BwtEmiPlugin.from(recipe.bottle()), recipe.soulCount(), EmiStack.of(recipe.getResult()));
    }

    public EmiSoulBottlingRecipe(ResourceLocation id, EmiIngredient bottle, int soulCount, EmiStack result) {
        this.id = id;
        this.bottle = bottle;
        this.soulCount = soulCount;
        this.result = result;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return BwtEmiPlugin.HOPPER_FILTERING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "/" + id.getPath());
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(this.bottle);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(this.result);
    }

    @Override
    public int getDisplayWidth() {
        return 18 * 5;
    }

    @Override
    public int getDisplayHeight() {
        return 18 * 4;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(this.bottle, 9, 18*3).drawBack(true).recipeContext(this);
        widgets.addSlot(this.result, 9*7, 18*3).drawBack(true).recipeContext(this);
        widgets.addTexture(BACKGROUND, 28, 56, 34, 13, 0, 72);
        widgets.addTexture(BACKGROUND, 43, 51, 3, 13, 0, 85);
        widgets.addTexture(FULL_GEAR, 10, 20);
    }
}
