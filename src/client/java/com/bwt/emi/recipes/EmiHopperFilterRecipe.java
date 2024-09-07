package com.bwt.emi.recipes;

import com.bwt.blocks.BwtBlocks;
import com.bwt.emi.BwtEmiPlugin;
import com.bwt.recipes.hopper_filter.HopperFilterRecipe;
import com.bwt.recipes.soul_bottling.SoulBottlingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.render.EmiTooltipComponents;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EmiHopperFilterRecipe implements EmiRecipe {

    public static final EmiTexture EMPTY_GEAR = new EmiTexture(BwtEmiPlugin.WIDGETS, 0, 0, 14, 14);
    public static final EmiTexture FULL_GEAR = new EmiTexture(BwtEmiPlugin.WIDGETS, 14, 0, 14, 14);
    public static final EmiTexture ARROW = new EmiTexture(BwtEmiPlugin.WIDGETS, 28, 0, 22, 15);
    public static final Identifier BACKGROUND = Identifier.of("bwt", "textures/gui/container/hopper_recipe.png");

    private final Identifier id;
    protected final EmiRecipeCategory category;
    protected final EmiIngredient ingredient;
    protected final EmiIngredient filter;
    protected final int soulCount;
    protected final EmiStack result;
    protected final EmiStack byproduct;
    protected EmiSoulBottlingRecipe soulBottlingRecipe;

    public EmiHopperFilterRecipe(EmiRecipeCategory category, Identifier id, HopperFilterRecipe recipe) {
        this(category, id, EmiIngredient.of(recipe.ingredient()), EmiIngredient.of(recipe.filter()), recipe.soulCount(), EmiStack.of(recipe.result()), EmiStack.of(recipe.byproduct()));
    }

    public EmiHopperFilterRecipe(EmiRecipeCategory category, Identifier id, EmiIngredient ingredient, EmiIngredient filter, int soulCount, EmiStack result, EmiStack byproduct) {
        this.category = category;
        this.id = id;
        this.ingredient = ingredient;
        this.filter = filter;
        this.soulCount = soulCount;
        this.result = result;
        this.byproduct = byproduct;
        this.soulBottlingRecipe = null;
    }

    public EmiHopperFilterRecipe withSoulBottlingRecipe(Identifier id, SoulBottlingRecipe recipe) {
        this.soulBottlingRecipe = new EmiSoulBottlingRecipe(id, recipe);
        return this;
    }


    @Override
    public EmiRecipeCategory getCategory() {
        return this.category;
    }

    @Override
    public @Nullable Identifier getId() {
        if(this.soulBottlingRecipe != null) {
            return Identifier.of("bwt", String.format("%s-%s", this.id.getPath(), this.soulBottlingRecipe.getId().getPath()));
        }
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        List<EmiIngredient> list = new ArrayList<>(List.of(ingredient));
        if(this.soulBottlingRecipe != null) {
            list.addAll(this.soulBottlingRecipe.getInputs());
        }
        return list;
    }

    @Override
    public List<EmiStack> getOutputs() {
        List<EmiStack> list = new ArrayList<>(List.of(this.result, this.byproduct));
        if(this.soulBottlingRecipe != null) {
            list.addAll(this.soulBottlingRecipe.getOutputs());
        }
        return list;
    }

    @Override
    public int getDisplayWidth() {
        return 18 * 5;
    }

    @Override
    public int getDisplayHeight() {
        return 18 * 4;
    }


    @SuppressWarnings("UnnecessaryLocalVariable")
    public EmiIngredient getIngredient() {

        if (this.soulBottlingRecipe != null) {
            var createdSoulCount = this.soulCount;
            var requiredSoulCount = this.soulBottlingRecipe.soulCount;
            var ingredientsRequired = (long) requiredSoulCount/createdSoulCount;
            var copy = this.ingredient.copy().setAmount(ingredientsRequired);
            return copy;
        }
        return this.ingredient;

    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public EmiStack getByproduct() {

        if (this.soulBottlingRecipe != null && this.byproduct != null) {
            var createdSoulCount = this.soulCount;
            var requiredSoulCount = this.soulBottlingRecipe.soulCount;
            var byproductsCreated = (long) requiredSoulCount/createdSoulCount;
            var copy = this.byproduct.copy().setAmount(byproductsCreated);
            return copy;
        }
        return this.byproduct;
    }

    public EmiStack getResult() {
        return this.result;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(BACKGROUND, 0,0, 18*5, 18*4, 0,0);
        widgets.addSlot(this.getIngredient(), 9, 0).drawBack(true).recipeContext(this);
        var byproduct = getByproduct();
        if (byproduct != null) {
            widgets.addSlot(byproduct, 9 * 7, 0).drawBack(true).recipeContext(this);
        }

        widgets.addSlot(this.filter, 18*2, 18).drawBack(false).recipeContext(this);
        widgets.addSlot(EmiStack.of(BwtBlocks.hopperBlock), 18*2, 18*2).drawBack(false).recipeContext(this);
        var result = this.getResult();
        if(result != null && !result.isEmpty()) {
            widgets.addSlot(this.getResult(), 9*7, 18*2).drawBack(true).recipeContext(this);
            widgets.addTexture(BACKGROUND, 52,38, 10, 13, 24,18*4);
        }


        if(this.soulBottlingRecipe != null) {
            this.soulBottlingRecipe.addWidgets(widgets);
        }
    }
}
