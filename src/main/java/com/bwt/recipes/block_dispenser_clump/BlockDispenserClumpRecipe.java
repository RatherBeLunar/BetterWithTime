package com.bwt.recipes.block_dispenser_clump;

import com.bwt.blocks.block_dispenser.BlockDispenserBlockEntity;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.IngredientWithCount;
import com.bwt.utils.Id;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public record BlockDispenserClumpRecipe(IngredientWithCount item, Block block, CraftingBookCategory category) implements Recipe<BlockDispenserClumpRecipeInput> {
    public Ingredient getItem() {
        return item.ingredient();
    }

    public int getItemCount() {
        return item.count();
    }

    @Override
    public boolean matches(BlockDispenserClumpRecipeInput input, Level level) {
        // We could make this >= itemCount, but we want to match any number of ingredients
        // That way, if you have < itemCount, the ingredient doesn't get spit out
        return input.items().stream().filter(getItem()).mapToInt(ItemStack::getCount).sum() > 0;
    }

    public boolean canAfford(BlockDispenserBlockEntity inventory) {
        // This function is similar to matches, but is called manually after matching
        return inventory.getItems().stream().filter(getItem()).mapToInt(ItemStack::getCount).sum() >= getItemCount();
    }

    @Override
    public ItemStack assemble(BlockDispenserClumpRecipeInput input, HolderLookup.Provider lookup) {
        return this.block.asItem().getDefaultInstance();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return switch (this.category()) {
            case BUILDING -> RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
            case EQUIPMENT -> RecipeBookCategories.CRAFTING_EQUIPMENT;
            case REDSTONE -> RecipeBookCategories.CRAFTING_REDSTONE;
            case MISC -> RecipeBookCategories.CRAFTING_MISC;
        };
    }

    public void spendIngredientsFromInventory(BlockDispenserBlockEntity inventory) {
        NonNullList<ItemStack> inventoryItems = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);

        int itemsToRemove = getItemCount();
        int currentSlotIndex = inventory.getSelectedSlot();
        int startingIndex = currentSlotIndex;
        while (itemsToRemove > 0) {
            ItemStack stack = inventoryItems.get(currentSlotIndex);
            if (item.test(stack)) {
                int itemsToRemoveFromStack = Math.min(stack.getCount(), itemsToRemove);
                stack.shrink(itemsToRemoveFromStack);
                itemsToRemove -= itemsToRemoveFromStack;
            }

            currentSlotIndex = (currentSlotIndex + 1) % inventory.getContainerSize();
            if (currentSlotIndex == startingIndex) {
                break;
            }
        }
        inventory.setChanged();
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> defaultedList = NonNullList.create();
        for (int i = 0; i < getItemCount(); i++) {
            defaultedList.add(getItem());
        }
        return defaultedList;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return Recipe.super.group();
    }

    @Override
    public RecipeSerializer<BlockDispenserClumpRecipe> getSerializer() {
        return BwtRecipes.BLOCK_DISPENSER_CLUMP_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<BlockDispenserClumpRecipe> getType() {
        return BwtRecipes.BLOCK_DISPENSER_CLUMP_RECIPE_TYPE;
    }

    public static class Serializer implements RecipeSerializer<BlockDispenserClumpRecipe> {
        public static final MapCodec<BlockDispenserClumpRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                    IngredientWithCount.Serializer.CODEC
                            .fieldOf("ingredient")
                            .forGetter(BlockDispenserClumpRecipe::item),
                    BuiltInRegistries.BLOCK
                            .byNameCodec()
                            .fieldOf("block")
                            .forGetter(recipe -> recipe.block),
                    CraftingBookCategory.CODEC.fieldOf("category")
                            .orElse(CraftingBookCategory.MISC)
                            .forGetter(recipe -> recipe.category)
                ).apply(instance, BlockDispenserClumpRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, BlockDispenserClumpRecipe> PACKET_CODEC = StreamCodec.composite(
                IngredientWithCount.Serializer.PACKET_CODEC, BlockDispenserClumpRecipe::item,
                ByteBufCodecs.fromCodecWithRegistries(BuiltInRegistries.BLOCK.byNameCodec()), BlockDispenserClumpRecipe::block,
                CraftingBookCategory.STREAM_CODEC, BlockDispenserClumpRecipe::category,
                BlockDispenserClumpRecipe::new
        );


        @Override
        public MapCodec<BlockDispenserClumpRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BlockDispenserClumpRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }

    public interface RecipeFactory<T extends BlockDispenserClumpRecipe> {
        T create(IngredientWithCount item, Block block, CraftingBookCategory category);
    }

    public static class JsonBuilder implements RecipeBuilder {
        protected IngredientWithCount ingredient;
        protected Block block;
        protected CraftingBookCategory category = CraftingBookCategory.MISC;

        @Nullable
        protected String group;
        protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

        public static JsonBuilder create() {
            return new JsonBuilder();
        }

        RecipeFactory<BlockDispenserClumpRecipe> getRecipeFactory() {
            return BlockDispenserClumpRecipe::new;
        }

        public JsonBuilder category(CraftingBookCategory category) {
            this.category = category;
            return this;
        }

        public JsonBuilder ingredient(IngredientWithCount ingredient) {
            this.ingredient = ingredient;
            return this;
        }

        public JsonBuilder ingredient(Item item, int count) {
            return this.ingredient(IngredientWithCount.of(item, count));
        }

        public JsonBuilder ingredient(Item item) {
            return this.ingredient(item, 1);
        }

        public JsonBuilder output(Block block) {
            this.block = block;
            return this;
        }

        @Override
        public JsonBuilder unlockedBy(String name, Criterion<?> criterion) {
            this.criteria.put(name, criterion);
            return this;
        }

        @Override
        public JsonBuilder group(@Nullable String string) {
            this.group = string;
            return this;
        }

        @Override
        public Item getResult() {
            return block.asItem();
        }

        @Override
        public void save(RecipeOutput exporter) {
            this.save(exporter, BuiltInRegistries.BLOCK.getKey(block).getPath() + "_clump");
        }

        @Override
        public void save(RecipeOutput exporter, String recipePath) {
            this.save(exporter, Id.of(recipePath));
        }

        public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
            this.save(recipeOutput, ResourceKey.create(Registries.RECIPE, recipeId));
        }

        @Override
        public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> resourceKey) {
            Advancement.Builder advancementBuilder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey)).rewards(AdvancementRewards.Builder.recipe(resourceKey)).requirements(AdvancementRequirements.Strategy.OR);
            this.criteria.forEach(advancementBuilder::addCriterion);
            BlockDispenserClumpRecipe blockDispenserClumpRecipe = this.getRecipeFactory().create(
                    this.ingredient,
                    this.block,
                    this.category
            );
            recipeOutput.accept(resourceKey, blockDispenserClumpRecipe, advancementBuilder.build(resourceKey.location().withPrefix("recipes/" + this.category.getSerializedName() + "/")));
        }
    }
}


