package com.bwt.utils;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Map;

public class TrackedDataHandlers implements ModInitializer {
    public static TrackedDataHandler<Map<Vec3i, BlockState>> blockStateMapHandler = TrackedDataHandler.create(new PacketCodec<>() {
        @Override
        public Map<Vec3i, BlockState> decode(RegistryByteBuf buf) {
            return buf.readMap(RegistryByteBuf::readBlockPos, innerBuf -> Block.STATE_IDS.get(innerBuf.readInt()));
        }

        @Override
        public void encode(RegistryByteBuf buf, Map<Vec3i, BlockState> map) {
            buf.writeMap(map, (innerBuf, key) -> innerBuf.writeBlockPos(new BlockPos(key)), (innerBuf, value) -> innerBuf.writeInt(Block.STATE_IDS.getRawId(value)));
        }
    });
    public static TrackedDataHandler<Map<Vec3i, NbtCompound>> blockEntityMapHandler = TrackedDataHandler.create(new PacketCodec<>() {
        @Override
        public Map<Vec3i, NbtCompound> decode(RegistryByteBuf buf) {
            return buf.readMap(RegistryByteBuf::readBlockPos, RegistryByteBuf::readNbt);
        }

        @Override
        public void encode(RegistryByteBuf buf, Map<Vec3i, NbtCompound> map) {
            buf.writeMap(map, (innerBuf, key) -> innerBuf.writeBlockPos(new BlockPos(key)), RegistryByteBuf::writeNbt);
        }
    });

    @Override
    public void onInitialize() {
        TrackedDataHandlerRegistry.register(blockStateMapHandler);
        TrackedDataHandlerRegistry.register(blockEntityMapHandler);
    }
}
