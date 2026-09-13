package com.bwt.utils;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Map;

public class TrackedDataHandlers implements ModInitializer {
    public static final EntityDataSerializer<Map<Vec3i, BlockState>> blockStateMapHandler = EntityDataSerializer.forValueType(new StreamCodec<>() {
        @Override
        public Map<Vec3i, BlockState> decode(RegistryFriendlyByteBuf buf) {
            return buf.readMap(RegistryFriendlyByteBuf::readBlockPos, innerBuf -> Block.BLOCK_STATE_REGISTRY.byId(innerBuf.readInt()));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, Map<Vec3i, BlockState> map) {
            buf.writeMap(map, (innerBuf, key) -> innerBuf.writeBlockPos(new BlockPos(key)), (innerBuf, value) -> innerBuf.writeInt(Block.BLOCK_STATE_REGISTRY.getId(value)));
        }
    });
    public static final EntityDataSerializer<Map<Vec3i, CompoundTag>> blockEntityMapHandler = EntityDataSerializer.forValueType(new StreamCodec<>() {
        @Override
        public Map<Vec3i, CompoundTag> decode(RegistryFriendlyByteBuf buf) {
            return buf.readMap(RegistryFriendlyByteBuf::readBlockPos, RegistryFriendlyByteBuf::readNbt);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, Map<Vec3i, CompoundTag> map) {
            buf.writeMap(map, (innerBuf, key) -> innerBuf.writeBlockPos(new BlockPos(key)), RegistryFriendlyByteBuf::writeNbt);
        }
    });

    @Override
    public void onInitialize() {
        EntityDataSerializers.registerSerializer(blockStateMapHandler);
        EntityDataSerializers.registerSerializer(blockEntityMapHandler);
    }
}
