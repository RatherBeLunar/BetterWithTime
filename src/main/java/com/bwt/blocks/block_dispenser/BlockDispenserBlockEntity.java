package com.bwt.blocks.block_dispenser;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.block_entities.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class BlockDispenserBlockEntity extends DispenserBlockEntity implements NamedScreenHandlerFactory, ImplementedInventory, SidedInventory {
    public static final int INVENTORY_SIZE = 16;
    private static final int[] AVAILABLE_SLOTS = IntStream.range(0, INVENTORY_SIZE).toArray();
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private int selectedSlot;

    public BlockDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.blockDispenserBlockEntity, pos, state);
        selectedSlot = 0;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int slotToSelect) {
        this.selectedSlot = Math.max(slotToSelect, 0) % INVENTORY_SIZE;
        this.markDirty();
    }

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return selectedSlot;
        }

        @Override
        public void set(int index, int value) {
            selectedSlot = value;
        }

        @Override
        public int size() {
            return 1;
        }
    };

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public int size() {
        return INVENTORY_SIZE;
    }

    protected int findNextValidSlotIndex() {
        int invSize = inventory.size();
        // Wrap around inventory, including a return to the selected slot
        for (int currentSlot = selectedSlot + 1; currentSlot <= invSize + selectedSlot; currentSlot++ )
        {
            if (!inventory.get(currentSlot % invSize).isEmpty())
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
        ItemStack itemStack = inventory.get(selectedSlot);

        if (!itemStack.isEmpty()) {
            return itemStack;
        }

        int newSlot = findNextValidSlotIndex();
        setSelectedSlot(newSlot);

        return inventory.get(newSlot);
    }

    public boolean hasRoomFor(ItemStack stack) {
        int count = stack.getCount();
        for (ItemStack invStack : inventory) {
            if (invStack.isEmpty()) {
                return true;
            }
            if (invStack.getItem() != stack.getItem()) {
                continue;
            }
            count -= (invStack.getMaxCount() - invStack.getCount());
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

        int invSize = inventory.size();
        for (int currentSlot = 0; currentSlot < invSize; currentSlot++ )
        {
            ItemStack invStack = inventory.get(currentSlot);
            if (invStack.getItem().equals(stack.getItem())) {
                int space = invStack.getMaxCount() - invStack.getCount();
                int inserted = Math.min(space, stack.getCount());
                invStack.increment(inserted);
                setStack(currentSlot, invStack);
                stack.decrement(inserted);
            }
            if (stack.getCount() <= 0) {
                return stack;
            }
        }
        if (stack.getCount() <= 0) {
            return stack;
        }
        for (int currentSlot = 0; currentSlot < invSize; currentSlot++ ) {
            ItemStack invStack = inventory.get(currentSlot);
            if (invStack.isEmpty()) {
                invStack = stack.copyAndEmpty();
                setStack(currentSlot, invStack);
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

        int invSize = inventory.size();
        for (int currentSlot = selectedSlot; currentSlot < invSize + selectedSlot; currentSlot++ )
        {
            ItemStack invStack = inventory.get(currentSlot % invSize);
            if (invStack.isEmpty()) {
                continue;
            }
            else if (invStack.getItem().equals(item)) {
                int available = invStack.getCount();
                int removed = Math.min(count, available);
                invStack.decrement(removed);
                setStack(currentSlot % invSize, invStack);
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
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        //We provide *this* to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return new BlockDispenserScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    protected DefaultedList<ItemStack> method_11282() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        this.selectedSlot = nbt.getInt("nextSlotToDispense");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("nextSlotToDispense", selectedSlot);
    }

    // SidedInventory, to disable extraction

    @Override
    public int[] getAvailableSlots(Direction side) {
        return AVAILABLE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }
}



