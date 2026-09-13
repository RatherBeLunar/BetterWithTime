package com.bwt.recipes.soul_forge;

import com.bwt.generation.EmiDefaultsGenerator;
import com.bwt.mixin.accessors.ShapedRecipeJsonBuilderAccessorMixin;
import com.bwt.recipes.BwtRecipes;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import java.util.Objects;

public class SoulForgeShapedRecipe extends ShapedRecipe implements SoulForgeRecipe {
    protected final ShapedRecipePattern raw;

    public SoulForgeShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern raw, ItemStack result, boolean showNotification) {
        super(group, category, raw, result, showNotification);
        this.raw = raw;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.SOUL_FORGE_SHAPED_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BwtRecipes.SOUL_FORGE_RECIPE_TYPE;
    }

    @Override
    public int getWidth() {
        return this.raw.width();
    }

    @Override
    public int getHeight() {
        return this.raw.height();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.raw.width() && height >= this.raw.height();
    }

    public ShapedRecipePattern getRaw() {
        return raw;
    }

    public ItemStack getResult() {
        return getResultItem(null);
    }

    public static class Serializer implements RecipeSerializer<SoulForgeShapedRecipe> {
        public static final MapCodec<SoulForgeShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
                        RawSoulForgeShapedRecipe.CODEC.forGetter(SoulForgeShapedRecipe::getRaw),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(SoulForgeShapedRecipe::getResult),
                        Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification)
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
            ShapedRecipePattern rawShapedRecipe = ShapedRecipePattern.STREAM_CODEC.decode(buf);
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(buf);
            boolean bl = buf.readBoolean();
            return new SoulForgeShapedRecipe(string, craftingRecipeCategory, rawShapedRecipe, itemStack, bl);
        }

        private static void write(RegistryFriendlyByteBuf buf, SoulForgeShapedRecipe recipe) {
            buf.writeUtf(recipe.getGroup());
            buf.writeEnum(recipe.category());
            ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.raw);
            ItemStack.STREAM_CODEC.encode(buf, recipe.getResult());
            buf.writeBoolean(recipe.showNotification());
        }
    }

    public static class JsonBuilder extends ShapedRecipeBuilder {
        public JsonBuilder(RecipeCategory category, ItemLike output, int count) {
            super(category, output, count);
        }

        public static JsonBuilder shaped(RecipeCategory category, ItemLike output) {
            return JsonBuilder.shaped(category, output, 1);
        }

        public static JsonBuilder shaped(RecipeCategory category, ItemLike output, int count) {
            return new JsonBuilder(category, output, count);
        }

        protected boolean isDefaultRecipe;
        public JsonBuilder markDefault() {
            this.isDefaultRecipe = true;
            return this;
        }
        public void addToDefaults(ResourceLocation recipeId) {
            if (this.isDefaultRecipe) {
                EmiDefaultsGenerator.addBwtRecipe(recipeId.withPrefix("/soulforge-bwt-"));
            }
        }

        private ShapedRecipePattern ensureValid(ResourceLocation recipeId) {
            ShapedRecipeJsonBuilderAccessorMixin accessor = ((ShapedRecipeJsonBuilderAccessorMixin) this);
            if (accessor.getCriteria().isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + recipeId);
            }
            return ShapedRecipePattern.of(accessor.getKey(), accessor.getRows());
        }

        @Override
        public void save(RecipeOutput exporter, String recipePath) {
            this.save(exporter, Id.of(recipePath));
        }

        @Override
        public void save(RecipeOutput exporter, ResourceLocation recipeId) {
            this.addToDefaults(recipeId);

            recipeId = Id.of(recipeId.getPath());
            ShapedRecipeJsonBuilderAccessorMixin accessor = ((ShapedRecipeJsonBuilderAccessorMixin) this);
            ShapedRecipePattern rawShapedRecipe = ensureValid(recipeId);
            Advancement.Builder builder = exporter.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(AdvancementRequirements.Strategy.OR);
            accessor.getCriteria().forEach(builder::addCriterion);
            SoulForgeShapedRecipe shapedRecipe = new SoulForgeShapedRecipe(Objects.requireNonNullElse(accessor.getGroup(), ""), RecipeBuilder.determineBookCategory(accessor.getCategory()), rawShapedRecipe, new ItemStack(accessor.getResult(), accessor.getCount()), accessor.getShowNotification());
            exporter.accept(recipeId, shapedRecipe, builder.build(recipeId.withPrefix("recipes/" + accessor.getCategory().getFolderName() + "/")));
        }
    }
}

