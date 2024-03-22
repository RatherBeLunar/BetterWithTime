package com.bwt.blocks.pulley;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.AnchorBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.RopeBlock;
import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PulleyBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    protected static final int INVENTORY_SIZE = 4;

    public final PulleyBlockEntity.Inventory inventory = new PulleyBlockEntity.Inventory(INVENTORY_SIZE);
    public final InventoryStorage inventoryWrapper = InventoryStorage.of(inventory, null);
    protected int mechPower;

    public final static int ticksToUpdateRopeState = 20;
    protected boolean hasAssociatedAnchorEntity;
    public int updateRopeStateCounter;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> PulleyBlockEntity.this.mechPower;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> PulleyBlockEntity.this.mechPower = value > 0 ? 1 : 0;
                default -> {}
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public PulleyBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.pulleyBlockEntity, pos, state);
        updateRopeStateCounter = ticksToUpdateRopeState;
    }

    public static boolean isMechPowered(BlockState state) {
        return state.get(PulleyBlock.MECH_POWERED);
    }

    public static boolean isRedstonePowered(BlockState state) {
        return state.get(PulleyBlock.POWERED);
    }

    public boolean isRaising(BlockState state) {
        return isMechPowered(state) && !isRedstonePowered(state);
    }

    public boolean isLowering(BlockState state) {
        return !isMechPowered(state) && !isRedstonePowered(state)
                && inventory.heldStacks.stream()
                    .anyMatch(itemStack -> itemStack.isOf(BwtItems.ropeItem) && itemStack.getCount() > 0);
    }

    public static void tick(World world, BlockPos pos, BlockState state, PulleyBlockEntity blockEntity) {
        if (world.isClient) {
            return;
        }

        blockEntity.mechPower = isMechPowered(state) ? 1 : 0;

        blockEntity.updateRopeStateCounter--;

        if (blockEntity.updateRopeStateCounter <= 0) {
            // if the pulley has an associated anchor, that's what controls the dispensing and retracting of rope

            if (!blockEntity.hasAssociatedAnchorEntity) {
                // redstone prevents the pulley from doing anything
                if (!isRedstonePowered(state)) {
                    if (isMechPowered(state)) {
                        blockEntity.attemptToRetractRope(world);
                    }
                    else {
                        blockEntity.attemptToDispenseRope(world);
                    }
                }
            }
            blockEntity.updateRopeStateCounter = ticksToUpdateRopeState;
        }
    }

    private boolean validRopeConnector(BlockState state) {
        return state.isOf(BwtBlocks.anchorBlock) && state.get(AnchorBlock.FACING).equals(Direction.UP);
    }

    protected boolean takeRope(int count, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            long numExtracted = inventoryWrapper.extract(ItemVariant.of(BwtItems.ropeItem.getDefaultStack()), count, transaction);
            if (simulate) {
                transaction.abort();
            }
            else {
                transaction.commit();
            }
            return numExtracted >= count;
        }
    }

    protected boolean takeRope(boolean simulate) {
        return takeRope(1, simulate);
    }

    protected boolean putRope(int count, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            long numExtracted = inventoryWrapper.insert(ItemVariant.of(BwtItems.ropeItem.getDefaultStack()), count, transaction);
            if (simulate) {
                transaction.abort();
            }
            else {
                transaction.commit();
            }
            return numExtracted >= count;
        }
    }

    protected boolean putRope(boolean simulate) {
        return putRope(1, simulate);
    }

    public boolean hasRope(int count) {
        return takeRope(count, true);
    }


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory.readNbtList(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE));
        this.mechPower = nbt.getInt("mechPower");
        this.updateRopeStateCounter = nbt.getInt("updateRopeStateCounter");
        this.hasAssociatedAnchorEntity = nbt.getBoolean("hasAssociatedAnchorEntity");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("Inventory", this.inventory.toNbtList());
        nbt.putInt("mechPower", this.mechPower);
        nbt.putInt("updateRopeStateCounter", this.updateRopeStateCounter);
        nbt.putBoolean("hasAssociatedAnchorEntity", this.hasAssociatedAnchorEntity);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        //We provide *this* to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return new PulleyScreenHandler(syncId, playerInventory, inventory, propertyDelegate);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return inventory.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return inventory.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.setStack(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    public class Inventory extends SimpleInventory {
        public Inventory(int size) {
            super(size);
        }
        @Override
        public void markDirty() {
            PulleyBlockEntity.this.markDirty();
        }
    }

    public void notifyPulleyEntityOfBlockStateChange(World world) {
        updateRopeStateCounter = ticksToUpdateRopeState;
        NotifyAttachedAnchorOfEntityStateChange(world);
    }

    private void NotifyAttachedAnchorOfEntityStateChange(World world) {
        // scan downward towards bottom of rope
        BlockPos bottomRope = RopeBlock.getLowestRope(world, pos);
        BlockState belowRopeState = world.getBlockState(bottomRope.down());
        if (!belowRopeState.isOf(BwtBlocks.anchorBlock) || !belowRopeState.get(AnchorBlock.FACING).equals(Direction.UP)) {
            return;
        }
//        if (AnchorBlock.notifyAnchorOfAttachedPulleyStateChange(world, bottomRope.down(), belowRopeState, this)) {
//            hasAssociatedAnchorEntity = true;
//        }
    }

    public void attemptToRetractRope(World world) {
        BlockPos bottomRopePos = RopeBlock.getLowestRope(world, pos);
        if (bottomRopePos.equals(pos)) {
            return;
        }
        world.removeBlock(bottomRopePos, false);
        putRope(false);
    }

    public void attemptToDispenseRope(World world) {
        boolean hasRope = takeRope(true);
        updateRopeStateCounter = ticksToUpdateRopeState;

        if (!hasRope) {
            return;
        }

        BlockPos belowRopePos = RopeBlock.getLowestRope(world, pos).down();
        BlockState belowRopeState = world.getBlockState(belowRopePos);
        if (!belowRopeState.isReplaceable()) {
            return;
        }

        takeRope(false);
        world.setBlockState(belowRopePos, BwtBlocks.ropeBlock.getDefaultState());

        // check for an upwards facing anchor below that we have just attached to
        belowRopePos = belowRopePos.down();
        belowRopeState = world.getBlockState(belowRopePos);
//        if (belowRopeState.isOf(BwtBlocks.anchorBlock) && belowRopeState.get(AnchorBlock.FACING).equals(Direction.UP)) {
//            AnchorBlock.notifyAnchorOfAttachedPulleyStateChange(world, belowRopePos, belowRopeState, this);
//        }

    }

    public void notifyOfLossOfAnchorEntity()
    {
        hasAssociatedAnchorEntity = false;
    }
}
