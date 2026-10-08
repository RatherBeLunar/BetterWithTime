package com.bwt.recipes.soul_forge;

import com.bwt.recipes.BwtRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.*;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;


public class SoulForgeShapelessRecipe implements SoulForgeRecipe {
    final String group;
    final CraftingBookCategory category;
    final ItemStack result;
    final List<Ingredient> ingredients;
    final boolean showNotification;
    @Nullable
    private PlacementInfo placementInfo;

    public SoulForgeShapelessRecipe(String string, CraftingBookCategory craftingBookCategory, ItemStack itemStack, List<Ingredient> list, boolean showNotification) {
        this.group = string;
        this.category = craftingBookCategory;
        this.result = itemStack;
        this.ingredients = list;
        this.showNotification = showNotification;
    }

    @Override
    public RecipeSerializer<SoulForgeShapelessRecipe> getSerializer() {
        return BwtRecipes.SOUL_FORGE_SHAPELESS_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<SoulForgeRecipe> getType() {
        return BwtRecipes.SOUL_FORGE_RECIPE_TYPE;
    }

    @Override
    public String group() {
        return this.group;
    }

    public List<Ingredient> ingredients() {
        return this.ingredients;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.ingredients);
        }

        return this.placementInfo;
    }

    @Override
    public boolean showNotification() {
        return this.showNotification;
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        if (craftingInput.ingredientCount() != this.ingredients.size()) {
            return false;
        } else {
            return craftingInput.size() == 1 && this.ingredients.size() == 1
                    ? this.ingredients.getFirst().test(craftingInput.getItem(0))
                    : craftingInput.stackedContents().canCraft(this, null);
        }
    }

    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
        return this.result.copy();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
                new ShapelessCraftingRecipeDisplay(
                        this.ingredients.stream().map(Ingredient::display).toList(),
                        new SlotDisplay.ItemStackSlotDisplay(this.result),
                        new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
                )
        );
    }

    public ItemStack getResult() {
        return result;
    }

    public static class Serializer implements RecipeSerializer<SoulForgeShapelessRecipe> {
        private static final MapCodec<SoulForgeShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codec.STRING.optionalFieldOf("group", "").forGetter(shapelessRecipe -> shapelessRecipe.group),
                                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(shapelessRecipe -> shapelessRecipe.category),
                                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(shapelessRecipe -> shapelessRecipe.result),
                                Ingredient.CODEC.listOf(1, 16).fieldOf("ingredients").forGetter(shapelessRecipe -> shapelessRecipe.ingredients),
                                Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(SoulForgeShapelessRecipe::showNotification)
                        )
                        .apply(instance, SoulForgeShapelessRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SoulForgeShapelessRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                SoulForgeShapelessRecipe::group,
                CraftingBookCategory.STREAM_CODEC,
                SoulForgeShapelessRecipe::category,
                ItemStack.STREAM_CODEC,
                SoulForgeShapelessRecipe::getResult,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                SoulForgeShapelessRecipe::ingredients,
                ByteBufCodecs.BOOL,
                SoulForgeShapelessRecipe::showNotification,
                SoulForgeShapelessRecipe::new
        );

        @Override
        public MapCodec<SoulForgeShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SoulForgeShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public static class JsonBuilder extends SoulForgeRecipeBuilder {
        private final List<Ingredient> ingredients = new ArrayList<>();

        private JsonBuilder(HolderGetter<Item> holderGetter, RecipeCategory recipeCategory, ItemStack itemStack) {
            super(holderGetter, recipeCategory, itemStack);
        }

        public static JsonBuilder shapeless(HolderGetter<Item> holderGetter, RecipeCategory recipeCategory, ItemStack itemStack) {
            return new JsonBuilder(holderGetter, recipeCategory, itemStack);
        }

        public static JsonBuilder shapeless(HolderGetter<Item> holderGetter, RecipeCategory recipeCategory, ItemLike itemLike) {
            return shapeless(holderGetter, recipeCategory, itemLike, 1);
        }

        public static JsonBuilder shapeless(HolderGetter<Item> holderGetter, RecipeCategory recipeCategory, ItemLike itemLike, int i) {
            return new JsonBuilder(holderGetter, recipeCategory, itemLike.asItem().getDefaultInstance().copyWithCount(i));
        }

        public JsonBuilder requires(ItemLike itemLike) {
            return this.requires(itemLike, 1);
        }

        public JsonBuilder requires(ItemLike itemLike, int i) {
            for (int j = 0; j < i; j++) {
                this.requires(Ingredient.of(itemLike));
            }

            return this;
        }

        public JsonBuilder requires(Ingredient ingredient) {
            return this.requires(ingredient, 1);
        }

        public JsonBuilder requires(Ingredient ingredient, int i) {
            for (int j = 0; j < i; j++) {
                this.ingredients.add(ingredient);
            }

            return this;
        }


        @Override
        public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> resourceKey) {
            this.ensureValid(resourceKey);
            Advancement.Builder builder = recipeOutput.advancement()
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                    .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                    .requirements(AdvancementRequirements.Strategy.OR);
            this.criteria.forEach(builder::addCriterion);
            SoulForgeShapelessRecipe shapelessRecipe = new SoulForgeShapelessRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    RecipeBuilder.determineBookCategory(this.category),
                    this.result,
                    this.ingredients,
                    this.showNotification
            );
            recipeOutput.accept(resourceKey, shapelessRecipe, builder.build(resourceKey.location().withPrefix("recipes/" + this.category.getFolderName() + "/")));
        }
    }
}
