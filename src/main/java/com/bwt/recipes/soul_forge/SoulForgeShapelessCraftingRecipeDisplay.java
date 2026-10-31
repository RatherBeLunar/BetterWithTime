package com.bwt.recipes.soul_forge;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

public record SoulForgeShapelessCraftingRecipeDisplay(List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
    public static final MapCodec<SoulForgeShapelessCraftingRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(SoulForgeShapelessCraftingRecipeDisplay::ingredients),
                            SlotDisplay.CODEC.fieldOf("result").forGetter(SoulForgeShapelessCraftingRecipeDisplay::result),
                            SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(SoulForgeShapelessCraftingRecipeDisplay::craftingStation)
                    )
                    .apply(instance, SoulForgeShapelessCraftingRecipeDisplay::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SoulForgeShapelessCraftingRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()),
            SoulForgeShapelessCraftingRecipeDisplay::ingredients,
            SlotDisplay.STREAM_CODEC,
            SoulForgeShapelessCraftingRecipeDisplay::result,
            SlotDisplay.STREAM_CODEC,
            SoulForgeShapelessCraftingRecipeDisplay::craftingStation,
            SoulForgeShapelessCraftingRecipeDisplay::new
    );
    public static final RecipeDisplay.Type<SoulForgeShapelessCraftingRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public RecipeDisplay.Type<SoulForgeShapelessCraftingRecipeDisplay> type() {
        return TYPE;
    }

    @Override
    public boolean isEnabled(FeatureFlagSet featureFlagSet) {
        return this.ingredients.stream().allMatch(slotDisplay -> slotDisplay.isEnabled(featureFlagSet)) && RecipeDisplay.super.isEnabled(featureFlagSet);
    }
}