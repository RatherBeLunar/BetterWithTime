package com.bwt.blocks.unfired_pottery;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.state.BlockState;

public class UnfiredDecoratedPotBlockEntity extends BlockEntity {
    public static final String SHERDS_NBT_KEY = "sherds";
    private PotDecorations sherds;

    public UnfiredDecoratedPotBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.unfiredDecoratedPotBlockEntity, pos, state);
        this.sherds = PotDecorations.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        this.sherds.save(nbt);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.sherds = PotDecorations.load(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag nbtCompound = saveWithoutMetadata(registryLookup);
        sherds.save(nbtCompound);
        return nbtCompound;
    }

    private void updateListeners() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    public Direction getHorizontalFacing() {
        return this.getBlockState().getValue(UnfiredDecoratedPotBlockWithSherds.FACING);
    }

    public HashMap<Direction, Optional<Item>> getDirectionalSherds() {
        Direction relativeDirection = getHorizontalFacing();
        ArrayList<Optional<Item>> directionalSherds = new ArrayList<>(getRotationalSherds());
        while (!relativeDirection.equals(Direction.NORTH)) {
            relativeDirection = relativeDirection.getClockWise();
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
        setSherds(new PotDecorations(rotationalSherds.get(2), rotationalSherds.get(3), rotationalSherds.get(1), rotationalSherds.get(0)));
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
            relativeDirection = relativeDirection.getClockWise();
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
            relativeDirection = relativeDirection.getClockWise();
            rotations++;
        }
        ArrayList<Optional<Item>> rotationalSherds = new ArrayList<>(getRotationalSherds());
        Optional<Item> directionalSherd = rotationalSherds.get(rotations);
        rotationalSherds.set(rotations, Optional.empty());
        setRotationalSherds(rotationalSherds);
        return directionalSherd;
    }

    public void setSherds(PotDecorations sherds) {
        this.sherds = sherds;
        updateListeners();
    }

    public Stream<Item> streamSherds() {
        return Stream.of(sherds.front(), sherds.back(), sherds.left(), sherds.right())
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public void readFrom(ItemStack stack) {
        this.applyComponentsFromItemStack(stack);
    }

    public ItemStack asStack() {
        ItemStack itemStack = BwtBlocks.unfiredDecoratedPotBlockWithSherds.asItem().getDefaultInstance();
        itemStack.applyComponents(this.collectComponents());
        return itemStack;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder componentMapBuilder) {
        super.collectImplicitComponents(componentMapBuilder);
        componentMapBuilder.set(DataComponents.POT_DECORATIONS, this.sherds);
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput components) {
        super.applyImplicitComponents(components);
        this.sherds = components.getOrDefault(DataComponents.POT_DECORATIONS, PotDecorations.EMPTY);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag nbt) {
        super.removeComponentsFromTag(nbt);
        nbt.remove(SHERDS_NBT_KEY);
    }
}
