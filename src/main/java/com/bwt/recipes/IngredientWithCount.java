package com.bwt.recipes;

import com.bwt.mixin.accessor.IngredientValuesAccessorMixin;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public record IngredientWithCount(Ingredient ingredient, int count) implements CustomIngredient {
    public static final Codec<IngredientWithCount> CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(
                    instance -> instance.group(
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientWithCount::ingredient),
                            ExtraCodecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(IngredientWithCount::count)
                    ).apply(instance, IngredientWithCount::new)
            )
    );

    public static final Serializer SERIALIZER = new Serializer();

    public static IngredientWithCount of(ItemStack stack) {
        return new IngredientWithCount(Ingredient.of(stack.getItem()), stack.getCount());
    }

    public static IngredientWithCount of(Item item) {
        return of(item, 1);
    }

    public static IngredientWithCount of(Item item, int count) {
        return of(new ItemStack(item, count));
    }

    public static IngredientWithCount of(Stream<? extends Item> stream, int count) {
        return new IngredientWithCount(Ingredient.of(stream), count);
    }

    public static IngredientWithCount of(HolderSet<Item> holderSet) {
        return of(holderSet, 1);
    }

    public static IngredientWithCount of(HolderSet<Item> holderSet, int count) {
        return new IngredientWithCount(Ingredient.of(holderSet), count);
    }

    public SlotDisplay display() {
        // TODO get item counts working
        return getValues()
                .unwrap()
                .map(
                        SlotDisplay.TagSlotDisplay::new,
                        list -> new SlotDisplay.Composite(
                                list.stream().map(IngredientWithCount::displayForSingleItem).toList()
                        )
                );
    }

    public static SlotDisplay optionalIngredientToDisplay(Optional<Ingredient> optional) {
        return optional.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE);
    }

    private static SlotDisplay displayForSingleItem(Holder<Item> holder) {
        SlotDisplay slotDisplay = new SlotDisplay.ItemSlotDisplay(holder);
        ItemStack remainder = holder.value().getCraftingRemainder();
        if (!remainder.isEmpty()) {
            SlotDisplay slotDisplay2 = new SlotDisplay.ItemStackSlotDisplay(remainder);
            return new SlotDisplay.WithRemainder(slotDisplay, slotDisplay2);
        } else {
            return slotDisplay;
        }
    }

    @Override
    public boolean test(ItemStack stack) {
        if (!ingredient.test(stack)) return false;
        return stack.getCount() >= count;
    }

    public boolean test(ItemVariant itemVariant) {
        return ingredient.test(itemVariant.toStack());
    }

    public HolderSet<Item> getValues() {
        return ((IngredientValuesAccessorMixin) ((Object) ingredient)).bwt$getValues();
    }

    @Override
    public Stream<Holder<Item>> getMatchingItems() {
        return ingredient.items();
    }

    public Stream<ItemStack> getMatchingStacks() {
        return ingredient.items()
                .map(Holder::value)
                .map(item -> new ItemStack(item, count))
                .filter(this::test);
    }

    public Item getFirstMatchingItem() {
        return getMatchingItems().map(Holder::value).findFirst().orElse(Items.AIR);
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer implements CustomIngredientSerializer<IngredientWithCount> {
        private static final Identifier ID = Id.of("ingredient_with_count");
        public static final MapCodec<IngredientWithCount> CODEC = createCodec();
        public static final StreamCodec<RegistryFriendlyByteBuf, IngredientWithCount> PACKET_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read
        );

        public static MapCodec<IngredientWithCount> createCodec() {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientWithCount::ingredient),
                            Codec.INT.optionalFieldOf("count", 1).forGetter(IngredientWithCount::count)
                    ).apply(instance, IngredientWithCount::new)
            );
        }

        @Override
        public Identifier getIdentifier() {
            return ID;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, IngredientWithCount> getPacketCodec() {
            return PACKET_CODEC;
        }

        @Override
        public MapCodec<IngredientWithCount> getCodec() {
            return CODEC;
        }

        public static IngredientWithCount read(RegistryFriendlyByteBuf buf) {
            Ingredient base = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            int count = buf.readInt();
            return new IngredientWithCount(base, count);
        }

        public static void write(RegistryFriendlyByteBuf buf, IngredientWithCount ingredient) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient.ingredient);
            buf.writeInt(ingredient.count);
        }
    }
}
