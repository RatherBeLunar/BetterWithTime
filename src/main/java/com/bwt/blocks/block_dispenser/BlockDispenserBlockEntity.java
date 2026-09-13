package com.bwt.blocks.block_dispenser;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.block_entities.ImplementedInventory;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockDispenserBlockEntity extends DispenserBlockEntity implements MenuProvider, ImplementedInventory, WorldlyContainer {
    public static final int CONTAINER_SIZE = 16;
    private static final int[] AVAILABLE_SLOTS = IntStream.range(0, CONTAINER_SIZE).toArray();
    private int selectedSlot;

    public BlockDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.blockDispenserBlockEntity, pos, state);
        setItems(NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY));
        selectedSlot = 0;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int slotToSelect) {
        this.selectedSlot = Math.max(slotToSelect, 0) % CONTAINER_SIZE;
        this.setChanged();
    }

    private final ContainerData propertyDelegate = new ContainerData() {
        @Override
        public int get(int index) {
            return selectedSlot;
        }

        @Override
        public void set(int index, int value) {
            selectedSlot = value;
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    @Override
    public NonNullList<ItemStack> getItems() {
        return super.getItems();
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    protected int findNextValidSlotIndex() {
        int invSize = getItems().size();
        // Wrap around inventory, including a return to the selected slot
        for (int currentSlot = selectedSlot + 1; currentSlot <= invSize + selectedSlot; currentSlot++ )
        {
            if (!getItems().get(currentSlot % invSize).isEmpty())
            {
                return currentSlot % invSize;
            }
        }

        return 0;
    }

    public void advanceSelectedSlot() {
        setSelectedSlot(findNextValidSlotIndex());
    }

    public ItemStack getCurrentItemToDispense() {
        ItemStack itemStack = getItems().get(selectedSlot);

        if (!itemStack.isEmpty()) {
            return itemStack;
        }

        int newSlot = findNextValidSlotIndex();
        setSelectedSlot(newSlot);

        return getItems().get(newSlot);
    }

    public boolean hasRoomFor(ItemStack stack) {
        int count = stack.getCount();
        for (ItemStack invStack : getItems()) {
            if (invStack.isEmpty()) {
                return true;
            }
            if (!ItemStack.isSameItemSameComponents(invStack, stack)) {
                continue;
            }
            count -= (invStack.getMaxStackSize() - invStack.getCount());
            if (count <= 0) {
                return true;
            }
        }
        return false;
    }

    public ItemStack insert(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int invSize = getItems().size();
        for (int currentSlot = 0; currentSlot < invSize; currentSlot++ )
        {
            ItemStack invStack = getItems().get(currentSlot);
            if (ItemStack.isSameItemSameComponents(invStack, stack)) {
                int space = invStack.getMaxStackSize() - invStack.getCount();
                int inserted = Math.min(space, stack.getCount());
                invStack.grow(inserted);
                setItem(currentSlot, invStack);
                stack.shrink(inserted);
            }
            if (stack.getCount() <= 0) {
                return stack;
            }
        }
        if (stack.getCount() <= 0) {
            return stack;
        }
        for (int currentSlot = 0; currentSlot < invSize; currentSlot++ ) {
            ItemStack invStack = getItems().get(currentSlot);
            if (invStack.isEmpty()) {
                invStack = stack.copyAndClear();
                setItem(currentSlot, invStack);
            }
            if (stack.getCount() <= 0) {
                return stack;
            }
        }
        return stack;
    }

    public ItemStack take(Item item, int count) {
        if (item == Items.AIR || count == 0) {
            return ItemStack.EMPTY;
        }

        int invSize = getItems().size();
        for (int currentSlot = selectedSlot; currentSlot < invSize + selectedSlot; currentSlot++ )
        {
            ItemStack invStack = getItems().get(currentSlot % invSize);
            if (invStack.isEmpty()) {
                continue;
            }
            else if (invStack.is(item)) {
                int available = invStack.getCount();
                int removed = Math.min(count, available);
                invStack.shrink(removed);
                setItem(currentSlot % invSize, invStack);
                count -= removed;
            }
            if (count <= 0) {
                return new ItemStack(item, count);
            }
        }
        return new ItemStack(item, count);
    }

    //These Methods are from the NamedScreenHandlerFactory Interface
    //createMenu creates the ScreenHandler itself
    //getDisplayName will Provide its name which is normally shown at the top

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        //We provide *this* to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return new BlockDispenserScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider lookup) {
        super.loadAdditional(nbt, lookup);
        ContainerHelper.loadAllItems(nbt, this.getItems(), lookup);
        this.selectedSlot = nbt.getInt("nextSlotToDispense");
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider lookup) {
        super.saveAdditional(nbt, lookup);
        ContainerHelper.saveAllItems(nbt, this.getItems(), lookup);
        nbt.putInt("nextSlotToDispense", selectedSlot);
    }

    // SidedInventory, to disable extraction

    @Override
    public int[] getSlotsForFace(Direction side) {
        return AVAILABLE_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return false;
    }
}



