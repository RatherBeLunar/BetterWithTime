package com.bwt.recipes.block_dispenser_clump;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserBlockEntity;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.IngredientWithCount;
import com.bwt.utils.Id;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public record BlockDispenserClumpRecipe(IngredientWithCount item, ItemStack block, CraftingBookCategory category) implements Recipe<BlockDispenserClumpRecipeInput> {
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
        return this.block.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return block;
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

    @Override
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
    public String getGroup() {
        return Recipe.super.getGroup();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(BwtBlocks.blockDispenserBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.BLOCK_DISPENSER_CLUMP_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BwtRecipes.BLOCK_DISPENSER_CLUMP_RECIPE_TYPE;
    }

    public static class Serializer implements RecipeSerializer<BlockDispenserClumpRecipe> {
        public static final MapCodec<BlockDispenserClumpRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                    IngredientWithCount.Serializer.DISALLOW_EMPTY_CODEC
                            .fieldOf("ingredient")
                            .forGetter(BlockDispenserClumpRecipe::item),
                    BuiltInRegistries.ITEM
                            .holderByNameCodec()
                            .fieldOf("block")
                            .forGetter(recipe -> recipe.block.getItemHolder()),
                    CraftingBookCategory.CODEC.fieldOf("category")
                            .orElse(CraftingBookCategory.MISC)
                            .forGetter(recipe -> recipe.category)
                ).apply(
                        instance,
                        (ingredient, block, category) -> new BlockDispenserClumpRecipe(ingredient, new ItemStack(block), category)
                )
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, BlockDispenserClumpRecipe> PACKET_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read
        );


        @Override
        public MapCodec<BlockDispenserClumpRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BlockDispenserClumpRecipe> streamCodec() {
            return PACKET_CODEC;
        }

        protected static BlockDispenserClumpRecipe read(RegistryFriendlyByteBuf buf) {
            IngredientWithCount ingredient = IngredientWithCount.Serializer.read(buf);
            ItemStack block = ItemStack.STREAM_CODEC.decode(buf);
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            return new BlockDispenserClumpRecipe(ingredient, block, category);
        }

        protected static void write(RegistryFriendlyByteBuf buf, BlockDispenserClumpRecipe recipe) {
            IngredientWithCount.Serializer.PACKET_CODEC.encode(buf, recipe.item);
            ItemStack.STREAM_CODEC.encode(buf, recipe.block);
            buf.writeEnum(recipe.category);
        }
    }

    public interface RecipeFactory<T extends BlockDispenserClumpRecipe> {
        T create(IngredientWithCount item, ItemStack block, CraftingBookCategory category);
    }

    public static class JsonBuilder implements RecipeBuilder {
        protected Ingredient item;
        protected int count = 1;
        protected ItemStack block;
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

        public JsonBuilder ingredient(Ingredient ingredient) {
            this.item = ingredient;
            return this;
        }

        public JsonBuilder ingredient(Item item) {
            return this.ingredient(Ingredient.of(item));
        }

        public JsonBuilder count(int count) {
            this.count = count;
            return this;
        }

        public JsonBuilder output(ItemLike block) {
            this.block = new ItemStack(block.asItem());
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
            return block.getItem();
        }

        @Override
        public void save(RecipeOutput exporter) {
            this.save(exporter, RecipeProvider.getItemName(block.getItem()) + "_clump");
        }

        @Override
        public void save(RecipeOutput exporter, String recipePath) {
            this.save(exporter, Id.of(recipePath));
        }

        @Override
        public void save(RecipeOutput exporter, ResourceLocation recipeId) {
            Advancement.Builder advancementBuilder = exporter.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(AdvancementRequirements.Strategy.OR);
            this.criteria.forEach(advancementBuilder::addCriterion);
            BlockDispenserClumpRecipe blockDispenserClumpRecipe = this.getRecipeFactory().create(
                    new IngredientWithCount(item, count),
                    this.block,
                    this.category
            );
            exporter.accept(recipeId, blockDispenserClumpRecipe, advancementBuilder.build(recipeId.withPrefix("recipes/" + this.category.getSerializedName() + "/")));
        }
    }
}


