package com.bwt.recipes.soul_forge;

import com.bwt.recipes.BwtRecipes;
import com.bwt.utils.Id;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SoulForgeShapedRecipe implements SoulForgeRecipe {
    final SoulForgeShapedRecipePattern pattern;
    final ItemStack result;
    final String group;
    final CraftingBookCategory category;
    final boolean showNotification;
    @Nullable
    private PlacementInfo placementInfo;


    public SoulForgeShapedRecipe(String group, CraftingBookCategory category, SoulForgeShapedRecipePattern raw, ItemStack result, boolean showNotification) {
        this.group = group;
        this.category = category;
        this.pattern = raw;
        this.result = result;
        this.showNotification = showNotification;
    }

    public SoulForgeShapedRecipe(String group, CraftingBookCategory category, SoulForgeShapedRecipePattern raw, ItemStack result) {
        this(group, category, raw, result, true);
    }

    @Override
    public RecipeSerializer<? extends SoulForgeRecipe> getSerializer() {
        return BwtRecipes.SOUL_FORGE_SHAPED_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<CraftingInput>> getType() {
        return BwtRecipes.SOUL_FORGE_RECIPE_TYPE;
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    public List<Optional<Ingredient>> getIngredients() {
        return this.pattern.ingredients();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.createFromOptionals(this.pattern.ingredients());
        }

        return this.placementInfo;
    }

    @Override
    public boolean showNotification() {
        return this.showNotification;
    }

    public boolean matches(CraftingInput craftingInput, Level level) {
        return this.pattern.matches(craftingInput);
    }

    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
        return this.result.copy();
    }

    public int getWidth() {
        return this.pattern.width();
    }

    public int getHeight() {
        return this.pattern.height();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
                new ShapedCraftingRecipeDisplay(
                        this.pattern.width(),
                        this.pattern.height(),
                        this.pattern.ingredients().stream().map(optional -> optional.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE)).toList(),
                        new SlotDisplay.ItemStackSlotDisplay(this.result),
                        new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
                )
        );
    }

    public SoulForgeShapedRecipePattern pattern() {
        return pattern;
    }

    public ItemStack getResult() {
        return result;
    }

    public static class Serializer implements RecipeSerializer<SoulForgeShapedRecipe> {
        public static final MapCodec<SoulForgeShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(SoulForgeShapedRecipe::group),
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(SoulForgeShapedRecipe::category),
                        SoulForgeShapedRecipePattern.MAP_CODEC.forGetter(SoulForgeShapedRecipe::pattern),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(SoulForgeShapedRecipe::getResult),
                        Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(SoulForgeShapedRecipe::showNotification)
                ).apply(instance, SoulForgeShapedRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SoulForgeShapedRecipe> PACKET_CODEC = StreamCodec.of(
                SoulForgeShapedRecipe.Serializer::write,
                SoulForgeShapedRecipe.Serializer::read
        );

        @Override
        public MapCodec<SoulForgeShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SoulForgeShapedRecipe> streamCodec() {
            return PACKET_CODEC;
        }

        private static SoulForgeShapedRecipe read(RegistryFriendlyByteBuf buf) {
            String string = buf.readUtf();
            CraftingBookCategory craftingRecipeCategory = buf.readEnum(CraftingBookCategory.class);
            SoulForgeShapedRecipePattern rawShapedRecipe = SoulForgeShapedRecipePattern.STREAM_CODEC.decode(buf);
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(buf);
            boolean bl = buf.readBoolean();
            return new SoulForgeShapedRecipe(string, craftingRecipeCategory, rawShapedRecipe, itemStack, bl);
        }

        private static void write(RegistryFriendlyByteBuf buf, SoulForgeShapedRecipe recipe) {
            buf.writeUtf(recipe.group());
            buf.writeEnum(recipe.category());
            SoulForgeShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.pattern());
            ItemStack.STREAM_CODEC.encode(buf, recipe.getResult());
            buf.writeBoolean(recipe.showNotification());
        }
    }

    public static class JsonBuilder extends SoulForgeRecipeBuilder {
        private final List<String> rows = Lists.newArrayList();
        private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();

        private JsonBuilder(HolderGetter<Item> holderGetter, RecipeCategory recipeCategory, ItemStack itemStack) {
            super(holderGetter, recipeCategory, itemStack);
        }

        public static JsonBuilder shaped(HolderGetter<Item> holderGetter, RecipeCategory recipeCategory, ItemLike itemLike) {
            return shaped(holderGetter, recipeCategory, itemLike, 1);
        }

        public static JsonBuilder shaped(HolderGetter<Item> holderGetter, RecipeCategory recipeCategory, ItemLike itemLike, int i) {
            return new JsonBuilder(holderGetter, recipeCategory, itemLike.asItem().getDefaultInstance().copyWithCount(i));
        }

//        public static JsonBuilder shaped(RecipeCategory category, ItemLike output) {
//            return JsonBuilder.shaped(category, output, 1);
//        }
//
//        public static JsonBuilder shaped(RecipeCategory category, ItemLike output, int count) {
//            return new JsonBuilder(category, output, count);
//        }

        public JsonBuilder define(Character character, ItemLike itemLike) {
            return this.define(character, Ingredient.of(itemLike));
        }

        public JsonBuilder define(Character character, Ingredient ingredient) {
            if (this.key.containsKey(character)) {
                throw new IllegalArgumentException("Symbol '" + character + "' is already defined!");
            }

            if (character == ' ') {
                throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
            }

            this.key.put(character, ingredient);
            return this;
        }

        public JsonBuilder pattern(String string) {
            if (!this.rows.isEmpty() && string.length() != this.rows.getFirst().length()) {
                throw new IllegalArgumentException("Pattern must be the same width on every line!");
            }

            this.rows.add(string);
            return this;
        }

        @Override
        public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> resourceKey) {
            this.ensureValid(resourceKey);
            SoulForgeShapedRecipePattern shapedRecipePattern = SoulForgeShapedRecipePattern.of(this.key, this.rows);
            Advancement.Builder builder = recipeOutput.advancement()
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                    .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                    .requirements(AdvancementRequirements.Strategy.OR);
            this.criteria.forEach(builder::addCriterion);
            SoulForgeShapedRecipe shapedRecipe = new SoulForgeShapedRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    RecipeBuilder.determineBookCategory(this.category),
                    shapedRecipePattern,
                    this.result,
                    this.showNotification
            );
            recipeOutput.accept(resourceKey, shapedRecipe, builder.build(resourceKey.location().withPrefix("recipes/" + this.category.getFolderName() + "/")));
        }
    }
}

