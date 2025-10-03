package com.bwt.blocks.unfired_pottery;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Sherds;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class UnfiredDecoratedPotBlockEntity extends BlockEntity {
    public static final String SHERDS_NBT_KEY = "sherds";
    private Sherds sherds;

    public UnfiredDecoratedPotBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.unfiredDecoratedPotBlockEntity, pos, state);
        this.sherds = Sherds.DEFAULT;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        this.sherds.toNbt(nbt);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.sherds = Sherds.fromNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbtCompound = createNbt(registryLookup);
        sherds.toNbt(nbtCompound);
        return nbtCompound;
    }

    private void updateListeners() {
        this.markDirty();
        if (this.world != null) {
            this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
        }
    }

    public Direction getHorizontalFacing() {
        return this.getCachedState().get(UnfiredDecoratedPotBlockWithSherds.FACING);
    }

    public HashMap<Direction, Optional<Item>> getDirectionalSherds() {
        Direction relativeDirection = getHorizontalFacing();
        ArrayList<Optional<Item>> directionalSherds = new ArrayList<>(getRotationalSherds());
        while (!relativeDirection.equals(Direction.NORTH)) {
            relativeDirection = relativeDirection.rotateYClockwise();
            directionalSherds.add(directionalSherds.remove(0));
        }
        List<Direction> directions = List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
        return IntStream.range(0, directions.size())
                .boxed()
                .collect(
                        Collectors.toMap(
                                directions::get,
                                directionalSherds::get,
                                (a, b) -> b,
                                HashMap::new
                        )
                );
    }

    public HashMap<Direction, Item> getPresentDirectionSherds() {
        return getDirectionalSherds().entrySet().stream()
                .filter(entry -> entry.getValue().isPresent())
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().get()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> b,
                        HashMap::new
                ));
    }

    public List<Optional<Item>> getRotationalSherds() {
        return List.of(sherds.front(), sherds.right(), sherds.back(), sherds.left());
    }

    public void setRotationalSherds(List<Optional<Item>> rotationalSherds) {
        setSherds(new Sherds(rotationalSherds.get(2), rotationalSherds.get(3), rotationalSherds.get(1), rotationalSherds.get(0)));
    }

    public boolean hasSherds() {
        return streamSherds().findAny().isPresent();
    }

    public boolean tryAddSherd(Direction side, Item sherd) {
        Direction relativeDirection = getHorizontalFacing();
        if (side.getAxis().isVertical()) {
            return false;
        }
        int rotations = 0;
        while (!relativeDirection.equals(side)) {
            relativeDirection = relativeDirection.rotateYClockwise();
            rotations++;
        }
        ArrayList<Optional<Item>> rotationalSherds = new ArrayList<>(getRotationalSherds());
        Optional<Item> directionalSherd = rotationalSherds.get(rotations);
        if (directionalSherd.isPresent()) {
            return false;
        }
        rotationalSherds.set(rotations, Optional.of(sherd));
        setRotationalSherds(rotationalSherds);
        return true;
    }

    public Optional<Item> tryRemoveSherd(Direction side) {
        Direction relativeDirection = getHorizontalFacing();
        if (side.getAxis().isVertical()) {
            return Optional.empty();
        }
        int rotations = 0;
        while (!relativeDirection.equals(side)) {
            relativeDirection = relativeDirection.rotateYClockwise();
            rotations++;
        }
        ArrayList<Optional<Item>> rotationalSherds = new ArrayList<>(getRotationalSherds());
        Optional<Item> directionalSherd = rotationalSherds.get(rotations);
        rotationalSherds.set(rotations, Optional.empty());
        setRotationalSherds(rotationalSherds);
        return directionalSherd;
    }

    public void setSherds(Sherds sherds) {
        this.sherds = sherds;
        updateListeners();
    }

    public Stream<Item> streamSherds() {
        return Stream.of(sherds.front(), sherds.back(), sherds.left(), sherds.right())
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public void readFrom(ItemStack stack) {
        this.readComponents(stack);
    }

    public ItemStack asStack() {
        ItemStack itemStack = BwtBlocks.unfiredDecoratedPotBlockWithSherds.asItem().getDefaultStack();
        itemStack.applyComponentsFrom(this.createComponentMap());
        return itemStack;
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        componentMapBuilder.add(DataComponentTypes.POT_DECORATIONS, this.sherds);
    }

    @Override
    protected void readComponents(BlockEntity.ComponentsAccess components) {
        super.readComponents(components);
        this.sherds = components.getOrDefault(DataComponentTypes.POT_DECORATIONS, Sherds.DEFAULT);
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);
        nbt.remove(SHERDS_NBT_KEY);
    }
}
