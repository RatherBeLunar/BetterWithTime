package com.bwt.recipes.block_dispenser_clump;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserBlockEntity;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.IngredientWithCount;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public record BlockDispenserClumpRecipe(IngredientWithCount item, ItemStack block) implements Recipe<BlockDispenserClumpRecipeInput> {
    public Ingredient getItem() {
        return item.ingredient();
    }

    public int getItemCount() {
        return item.count();
    }

    @Override
    public boolean matches(BlockDispenserClumpRecipeInput input, World world) {
        // We could make this >= itemCount, but we want to match any number of ingredients
        // That way, if you have < itemCount, the ingredient doesn't get spit out
        return input.items().stream().filter(getItem()).mapToInt(ItemStack::getCount).sum() > 0;
    }

    public boolean canAfford(BlockDispenserBlockEntity inventory) {
        // This function is similar to matches, but is called manually after matching
        return inventory.getItems().stream().filter(getItem()).mapToInt(ItemStack::getCount).sum() >= getItemCount();
    }

    @Override
    public ItemStack craft(BlockDispenserClumpRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.block.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return block;
    }

    public void spendIngredientsFromInventory(BlockDispenserBlockEntity inventory) {
        DefaultedList<ItemStack> inventoryItems = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

        int itemsToRemove = getItemCount();
        int currentSlotIndex = inventory.getSelectedSlot();
        int startingIndex = currentSlotIndex;
        while (itemsToRemove > 0) {
            ItemStack stack = inventoryItems.get(currentSlotIndex);
            if (item.test(stack)) {
                int itemsToRemoveFromStack = Math.min(stack.getCount(), itemsToRemove);
                stack.decrement(itemsToRemoveFromStack);
                itemsToRemove -= itemsToRemoveFromStack;
            }

            currentSlotIndex = (currentSlotIndex + 1) % inventory.size();
            if (currentSlotIndex == startingIndex) {
                break;
            }
        }
        inventory.markDirty();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        for (int i = 0; i < getItemCount(); i++) {
            defaultedList.add(getItem());
        }
        return defaultedList;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return Recipe.super.isIgnoredInRecipeBook();
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
    public ItemStack createIcon() {
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
                    Registries.ITEM
                            .getEntryCodec()
                            .fieldOf("block")
                            .forGetter(recipe -> recipe.block.getRegistryEntry())
                ).apply(
                        instance,
                        (ingredient, block) -> new BlockDispenserClumpRecipe(ingredient, new ItemStack(block))
                )
        );
        public static final PacketCodec<RegistryByteBuf, BlockDispenserClumpRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read
        );


        @Override
        public MapCodec<BlockDispenserClumpRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, BlockDispenserClumpRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        protected static BlockDispenserClumpRecipe read(RegistryByteBuf buf) {
            IngredientWithCount ingredient = IngredientWithCount.Serializer.read(buf);
            ItemStack block = ItemStack.PACKET_CODEC.decode(buf);
            return new BlockDispenserClumpRecipe(ingredient, block);
        }

        protected static void write(RegistryByteBuf buf, BlockDispenserClumpRecipe recipe) {
            IngredientWithCount.Serializer.PACKET_CODEC.encode(buf, recipe.item);
            ItemStack.PACKET_CODEC.encode(buf, recipe.block);
        }
    }

    public interface RecipeFactory<T extends BlockDispenserClumpRecipe> {
        T create(IngredientWithCount item, ItemStack block);
    }

    public static class JsonBuilder implements CraftingRecipeJsonBuilder {
        protected Ingredient item;
        protected int count = 1;
        protected ItemStack block;

        @Nullable
        protected String group;
        protected final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

        public static JsonBuilder create() {
            return new JsonBuilder();
        }

        RecipeFactory<BlockDispenserClumpRecipe> getRecipeFactory() {
            return BlockDispenserClumpRecipe::new;
        }

        public JsonBuilder ingredient(Ingredient ingredient) {
            this.item = ingredient;
            return this;
        }

        public JsonBuilder ingredient(Item item) {
            return this.ingredient(Ingredient.ofItems(item));
        }

        public JsonBuilder count(int count) {
            this.count = count;
            return this;
        }

        public JsonBuilder output(ItemConvertible block) {
            this.block = new ItemStack(block.asItem());
            return this;
        }

        @Override
        public JsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
            this.criteria.put(name, criterion);
            return this;
        }

        @Override
        public JsonBuilder group(@Nullable String string) {
            this.group = string;
            return this;
        }

        @Override
        public Item getOutputItem() {
            return block.getItem();
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(exporter, RecipeProvider.getItemPath(block.getItem()) + "_clump");
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            this.criteria.forEach(advancementBuilder::criterion);
            BlockDispenserClumpRecipe blockDispenserClumpRecipe = this.getRecipeFactory().create(
                    new IngredientWithCount(item, count),
                    this.block
            );
            exporter.accept(recipeId, blockDispenserClumpRecipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/")));
        }
    }
}


