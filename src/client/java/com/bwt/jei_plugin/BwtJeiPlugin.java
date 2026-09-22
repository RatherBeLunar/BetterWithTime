package com.bwt.jei_plugin;

import com.bwt.jei_plugin.categories.*;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.cooking_pots.*;
import com.bwt.recipes.hopper_filter.HopperFilterRecipe;
import com.bwt.recipes.kiln.KilnRecipe;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JeiPlugin
public class BwtJeiPlugin implements IModPlugin {
    @Nullable
    private IRecipeCategory<RecipeHolder<KilnRecipe>> kilnCategory;
    @Nullable
    private IRecipeCategory<RecipeHolder<HopperFilterRecipe>> hopperSoulCategory;
    @Nullable
    private IRecipeCategory<RecipeHolder<HopperFilterRecipe>> hopperFilteringCategory;

    private static final Comparator<RecipeHolder<? extends Recipe<?>>> BY_ID = Comparator.comparing(RecipeHolder::id);
    private static final Comparator<RecipeHolder<? extends Recipe<?>>> BY_GROUP = Comparator.comparing(holder -> holder.value().getGroup());
    private static final Comparator<RecipeHolder<? extends CraftingRecipe>> BY_CATEGORY = Comparator.comparing(holder -> holder.value().category());
    private static final Comparator<RecipeHolder<? extends AbstractCookingPotRecipe>> BY_COOKING_POT_CATEGORY = Comparator.comparing(holder -> holder.value().getCategory());
    private static final List<CraftingBookCategory> CUSTOM_CATEGORY_ORDER = List.of(CraftingBookCategory.REDSTONE, CraftingBookCategory.EQUIPMENT, CraftingBookCategory.BUILDING, CraftingBookCategory.MISC);
    private static final Comparator<RecipeHolder<? extends CraftingRecipe>> BY_CATEGORY_REDSTONE_FIRST = Comparator.comparing(o -> {
        int index = CUSTOM_CATEGORY_ORDER.indexOf(o.value().category());
        if (index == -1) {
            index = 100;
        }
        return index;
    });

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
                new SoulForgeCategory(guiHelper),
                new MillStoneCategory(guiHelper),
                new SawCategory(guiHelper),
                new CauldronCategory(guiHelper),
                new StokedCauldronCategory(guiHelper),
                new CrucibleCategory(guiHelper),
                new StokedCrucibleCategory(guiHelper),
                new StokedCrucibleReclaimCategory(guiHelper),
                new TurntableCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(SoulForgeCategory.TYPE, sortRecipes(BwtRecipes.SOUL_FORGE_RECIPE_TYPE, BY_CATEGORY_REDSTONE_FIRST.thenComparing(BY_ID)).toList());
        registration.addRecipes(MillStoneCategory.TYPE, sortRecipes(BwtRecipes.MILL_STONE_RECIPE_TYPE, BY_ID).toList());
        registration.addRecipes(SawCategory.TYPE, sortRecipes(BwtRecipes.SAW_RECIPE_TYPE, BY_ID).toList());
        registration.addRecipes(CauldronCategory.TYPE, sortRecipes(BwtRecipes.CAULDRON_RECIPE_TYPE, BY_COOKING_POT_CATEGORY.thenComparing(BY_ID)).toList());
        registration.addRecipes(StokedCauldronCategory.TYPE, sortRecipes(BwtRecipes.STOKED_CAULDRON_RECIPE_TYPE, BY_COOKING_POT_CATEGORY.thenComparing(BY_ID)).toList());
        registration.addRecipes(CrucibleCategory.TYPE, sortRecipes(BwtRecipes.CRUCIBLE_RECIPE_TYPE, BY_COOKING_POT_CATEGORY.thenComparing(BY_ID)).toList());
        registration.addRecipes(
                StokedCrucibleCategory.TYPE,
                sortRecipes(BwtRecipes.STOKED_CRUCIBLE_RECIPE_TYPE, BY_COOKING_POT_CATEGORY.thenComparing(BY_ID))
                        .filter(stokedCrucibleRecipe -> !stokedCrucibleRecipe.getCategory().equals(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM))
                        .toList()
        );
        registration.addRecipes(
                StokedCrucibleReclaimCategory.TYPE,
                sortRecipes(BwtRecipes.STOKED_CRUCIBLE_RECIPE_TYPE, BY_COOKING_POT_CATEGORY.thenComparing(BY_ID))
                        .filter(stokedCrucibleRecipe -> stokedCrucibleRecipe.getCategory().equals(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM))
                        .toList()
        );
        registration.addRecipes(TurntableCategory.TYPE, getTurnTableRecipesSorted());
    }

    private static <T extends Recipe<C>, C extends RecipeInput> Stream<RecipeHolder<T>> streamRecipes(RecipeType<T> type) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return Stream.of();
        }
        return level.getRecipeManager().getAllRecipesFor(type).stream();
    }

    private static <T extends Recipe<C>, C extends RecipeInput> Stream<T> sortRecipes(RecipeType<T> type, Comparator<? super RecipeHolder<T>> comparator) {
        return streamRecipes(type).sorted(comparator).map(RecipeHolder::value);
    }

    private static List<TurntableRecipe> getTurnTableRecipesSorted() {
        ArrayList<TurntableRecipe> unsorted = streamRecipes(BwtRecipes.TURNTABLE_RECIPE_TYPE).map(RecipeHolder::value).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<TurntableRecipe> sorted = new ArrayList<>();
        while (!unsorted.isEmpty()) {
            TurntableRecipe chainOrigin = unsorted.removeFirst();
            sorted.add(chainOrigin);
            addPreviousInChain(unsorted, sorted, chainOrigin);
            addNextInChain(unsorted, sorted, chainOrigin);
        }
        return sorted;
    }

    private static void addNextInChain(ArrayList<TurntableRecipe> unsorted, ArrayList<TurntableRecipe> sorted, TurntableRecipe chainOrigin) {
        Optional<TurntableRecipe> optionalNextInChain = unsorted.stream().filter(r -> r.getIngredient().test(chainOrigin.getOutput())).findFirst();
        int index = sorted.indexOf(chainOrigin);
        if (optionalNextInChain.isEmpty()) {
            return;
        }
        TurntableRecipe nextInChain = optionalNextInChain.get();
        unsorted.remove(nextInChain);
        if (index > -1) {
            sorted.add(index + 1, nextInChain);
        }
        else {
            sorted.add(nextInChain);
        }
        addNextInChain(unsorted, sorted, nextInChain);
    }

    private static void addPreviousInChain(ArrayList<TurntableRecipe> unsorted, ArrayList<TurntableRecipe> sorted, TurntableRecipe chainOrigin) {
        Optional<TurntableRecipe> optionalPreviousInChain = unsorted.stream().filter(r -> chainOrigin.getIngredient().test(r.getOutput())).findFirst();
        int index = sorted.indexOf(chainOrigin);
        if (optionalPreviousInChain.isEmpty()) {
            return;
        }
        TurntableRecipe previousInChain = optionalPreviousInChain.get();
        unsorted.remove(previousInChain);
        if (index > -1) {
            sorted.add(index, previousInChain);
        }
        else {
            sorted.add(previousInChain);
        }
        addPreviousInChain(unsorted, sorted, previousInChain);
    }
}
