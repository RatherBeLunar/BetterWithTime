package com.bwt.jei_plugin;

import com.bwt.jei_plugin.categories.SoulForgeCategory;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.cooking_pots.CauldronRecipe;
import com.bwt.recipes.cooking_pots.CrucibleRecipe;
import com.bwt.recipes.cooking_pots.StokedCauldronRecipe;
import com.bwt.recipes.cooking_pots.StokedCrucibleRecipe;
import com.bwt.recipes.hopper_filter.HopperFilterRecipe;
import com.bwt.recipes.kiln.KilnRecipe;
import com.bwt.recipes.mill_stone.MillStoneRecipe;
import com.bwt.recipes.saw.SawRecipe;
import com.bwt.recipes.turntable.TurntableRecipe;
import com.bwt.utils.Id;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IModInfoRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

@JeiPlugin
public class BwtJeiPlugin implements IModPlugin {
    @Nullable
    private IRecipeCategory<RecipeHolder<CauldronRecipe>> cauldronCategory;
    @Nullable
    private IRecipeCategory<RecipeHolder<StokedCauldronRecipe>> stokeCauldronCategory;
    @Nullable
    private IRecipeCategory<RecipeHolder<CrucibleRecipe>> crucibleCategory;
    @Nullable
    private IRecipeCategory<RecipeHolder<StokedCrucibleRecipe>> stokedCrucibleCategory;
    @Nullable
    private IRecipeCategory<RecipeHolder<StokedCrucibleRecipe>> stokedCrucibleReclaimCategory;

    @Nullable
    private IRecipeCategory<RecipeHolder<MillStoneRecipe>> millStoneCategory;
    @Nullable
    private IRecipeCategory<RecipeHolder<SawRecipe>> sawCategory;
    @Nullable
    private IRecipeCategory<RecipeHolder<TurntableRecipe>> turntableCategory;
    @Nullable
    private IRecipeCategory<RecipeHolder<KilnRecipe>> kilnCategory;
    @Nullable
    private SoulForgeCategory soulForgeCategory;
    @Nullable
    private IRecipeCategory<RecipeHolder<HopperFilterRecipe>> hopperSoulCategory;
    @Nullable
    private IRecipeCategory<RecipeHolder<HopperFilterRecipe>> hopperFilteringCategory;

    private static final Comparator<RecipeHolder<? extends Recipe<?>>> BY_ID = Comparator.comparing(RecipeHolder::id);
    private static final Comparator<RecipeHolder<? extends Recipe<?>>> BY_GROUP = Comparator.comparing(holder -> holder.value().getGroup());
    private static final Comparator<RecipeHolder<? extends CraftingRecipe>> BY_CATEGORY = Comparator.comparing(holder -> holder.value().category());
    private static final Comparator<RecipeHolder<? extends CraftingRecipe>> BY_CATEGORY_REDSTONE_FIRST = (o1, o2) -> {
        if (o1.value().category().equals(CraftingBookCategory.REDSTONE) && !o2.value().category().equals(CraftingBookCategory.REDSTONE)) {
            return -1;
        }
        if (o1.value().category().equals(CraftingBookCategory.EQUIPMENT) && !o2.value().category().equals(CraftingBookCategory.EQUIPMENT)) {
            return -1;
        }
        if (o1.value().category().equals(CraftingBookCategory.BUILDING) && !o2.value().category().equals(CraftingBookCategory.BUILDING)) {
            return -1;
        }
        return BY_ID.compare(o1, o2);
    };

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return Id.of("jei_plugin");
    }

    @Override
    public void registerModInfo(IModInfoRegistration modAliasRegistration) {
        modAliasRegistration.addModAliases(Id.MOD_ID, "betterwithtime", "better with time");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(
                new SoulForgeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(SoulForgeCategory.TYPE, sortRecipes(BwtRecipes.SOUL_FORGE_RECIPE_TYPE, BY_CATEGORY.thenComparing(BY_ID)));
    }

    private static <T extends Recipe<C>, C extends RecipeInput> List<T> sortRecipes(RecipeType<T> type, Comparator<? super RecipeHolder<T>> comparator) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        return level.getRecipeManager().getAllRecipesFor(type)
                .stream().sorted(comparator).map(RecipeHolder::value).toList();
    }
}
