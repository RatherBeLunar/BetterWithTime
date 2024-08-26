package com.bwt.items.components;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.TooltipAppender;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public class InfernalEnchanterDecorationComponent implements TooltipAppender {

    private final EnumMap<Decoration, Item> items;

    public InfernalEnchanterDecorationComponent(EnumMap<Decoration, Item> items) {
        this.items = items;
    }

    public InfernalEnchanterDecorationComponent(Map<Decoration, Item> decorationItemMap) {
        this(new EnumMap<>(decorationItemMap));
    }

    public EnumMap<Decoration, Item> items() {
        return this.items;
    }

    public Item get(Decoration decoration) {
        return items.get(decoration);
    }

    public static final InfernalEnchanterDecorationComponent DEFAULT = new InfernalEnchanterDecorationComponent(
            new EnumMap<>(Decoration.class) {
                {
                    put(Decoration.NORTH_EAST, Items.CANDLE);
                    put(Decoration.NORTH_WEST, Items.CANDLE);
                    put(Decoration.SOUTH_EAST, Items.CANDLE);
                    put(Decoration.SOUTH_WEST, Items.CANDLE);
                }
            }
    );
    public static final Codec<InfernalEnchanterDecorationComponent> CODEC;
    public static final Codec<Decoration> KEY_CODEC;
    public static final Codec<Item> VALUE_CODEC;
    public static final PacketCodec<RegistryByteBuf, InfernalEnchanterDecorationComponent> PACKET_CODEC;
    //    public static final PacketCodec<RegistryByteBuf, Entry> PAIR_PACKET_CODEC;
    private static final String NBT_KEY = "infernal_enchanter_decoration";


    public NbtCompound toNbt(NbtCompound nbt) {
        if (this.equals(DEFAULT)) {
            return nbt;
        } else {
            nbt.put(NBT_KEY, CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow());
            return nbt;
        }
    }

    public static InfernalEnchanterDecorationComponent fromNbt(@Nullable NbtCompound nbt) {
        return nbt != null && nbt.contains(NBT_KEY) ? CODEC.parse(NbtOps.INSTANCE, nbt.get(NBT_KEY)).result().orElse(DEFAULT) : DEFAULT;
    }

    static {

        KEY_CODEC = Decoration.CODEC;
        VALUE_CODEC = Registries.ITEM.getCodec();
        CODEC = Codec.unboundedMap(KEY_CODEC, VALUE_CODEC).xmap(InfernalEnchanterDecorationComponent::new, InfernalEnchanterDecorationComponent::items);


        PACKET_CODEC = PacketCodecs.map(
                value -> new EnumMap<>(Decoration.class),
                Decoration.PACKET_CODEC, PacketCodecs.registryValue(RegistryKeys.ITEM)
        ).xmap(InfernalEnchanterDecorationComponent::new, InfernalEnchanterDecorationComponent::items);
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        for (Map.Entry<Decoration, Item> entry : items.entrySet()) {
            tooltip.accept(Text.literal(entry.getKey().asString()).append(ScreenTexts.SPACE).append(entry.getValue().getName()));
        }
    }

    private static final float o = 6/16f;
    public enum Decoration implements StringIdentifiable {
        NORTH_EAST(0, "north_east", o,6/16f,-o),
        NORTH_WEST(1, "north_west",-o,6/16f,-o),
        SOUTH_EAST(2, "south_east", o, 6/16f, o),
        SOUTH_WEST(3, "south_west", -o, 6/16f, o);

        private final int id;
        private final String name;
        private float x,y,z;

        private static final IntFunction<Decoration> BY_ID = ValueLists.createIdToValueFunction(Decoration::getId, values(), ValueLists.OutOfBoundsHandling.ZERO);
        public static final PacketCodec<ByteBuf, Decoration> PACKET_CODEC = PacketCodecs.indexed(BY_ID, Decoration::getId);
        public static final Codec<Decoration> CODEC = StringIdentifiable.createBasicCodec(Decoration::values);

        Decoration(int id, String name, float x, float y, float z) {
            this.id = id;
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public int getId() {
            return id;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getZ() {
            return z;
        }
    }


}
