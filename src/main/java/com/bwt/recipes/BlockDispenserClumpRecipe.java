package com.bwt.recipes;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserBlockEntity;
import com.mojang.serialization.Codec;
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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class BlockDispenserClumpRecipe implements Recipe<BlockDispenserBlockEntity> {
    private final Ingredient item;
    private final int itemCount;
    private final ItemStack block;

    public BlockDispenserClumpRecipe(Ingredient item, int itemCount, ItemStack block) {
        this.item = item;
        this.itemCount = itemCount;
        this.block = block;
    }

    public Ingredient getItem() {
        return item;
    }

    public int getItemCount() {
        return itemCount;
    }

    public ItemStack getOutput() {
        return block;
    }

    @Override
    public boolean matches(BlockDispenserBlockEntity inventory, World world) {
        // We could make this >= itemCount, but we want to match any number of ingredients
        // That way, if you have < itemCount, the ingredient doesn't get spit out
        return inventory.getItems().stream().filter(item).mapToInt(ItemStack::getCount).sum() > 0;
    }

    public boolean canAfford(BlockDispenserBlockEntity inventory) {
        // This function is similar to matches, but is called manually after matching
        return inventory.getItems().stream().filter(item).mapToInt(ItemStack::getCount).sum() >= itemCount;
    }

    @Override
    public ItemStack craft(BlockDispenserBlockEntity inventory, DynamicRegistryManager registryManager) {
        return this.getOutput().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        return block;
    }

    public void spendIngredientsFromInventory(BlockDispenserBlockEntity inventory) {
        DefaultedList<ItemStack> inventoryItems = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

        int itemsToRemove = itemCount;
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
        for (int i = 0; i < itemCount; i++) {
            defaultedList.add(item);
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
        Codec<BlockDispenserClumpRecipe> CODEC = RecordCodecBuilder.create(
                instance->instance.group(
                    Ingredient.DISALLOW_EMPTY_CODEC
                            .fieldOf("ingredient")
                            .forGetter(BlockDispenserClumpRecipe::getItem),
                    Codec.INT
                            .fieldOf("itemCount")
                            .forGetter(BlockDispenserClumpRecipe::getItemCount),
                    Registries.ITEM
                            .createEntryCodec()
                            .fieldOf("block")
                            .forGetter(recipe -> recipe.block.getRegistryEntry())
                ).apply(
                        instance,
                        (item, itemCount, block) -> new BlockDispenserClumpRecipe(item, itemCount, new ItemStack(block))
                )
        );
        @Override
        public Codec<BlockDispenserClumpRecipe> codec() {
            return CODEC;
        }

        @Override
        public BlockDispenserClumpRecipe read(PacketByteBuf buf) {
            var ingredient = Ingredient.fromPacket(buf);
            var count = buf.readInt();
            var block = buf.readItemStack();

            return new BlockDispenserClumpRecipe(ingredient, count, block);
        }

        @Override
        public void write(PacketByteBuf buf, BlockDispenserClumpRecipe recipe) {
            recipe.item.write(buf);
            buf.writeInt(recipe.itemCount);
            buf.writeItemStack(recipe.block);
        }
    }

    public interface RecipeFactory<T extends BlockDispenserClumpRecipe> {
        T create(Ingredient item, int count, ItemStack block);
    }

    public static class JsonBuilder implements CraftingRecipeJsonBuilder {
        protected Ingredient item;
        protected int count;
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
                    this.item,
                    this.count,
                    this.block
            );
            exporter.accept(recipeId, blockDispenserClumpRecipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/")));
        }
    }
}


