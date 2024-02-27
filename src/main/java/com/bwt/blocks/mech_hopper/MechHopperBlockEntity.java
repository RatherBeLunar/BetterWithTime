package com.bwt.blocks.mech_hopper;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.utils.SimpleSingleStackInventory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
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
import net.minecraft.world.World;

import java.util.List;

public class MechHopperBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    protected static final int INVENTORY_SIZE = 19;
    protected int mechPower;
    protected int soulCount;
    protected int xpCount;
    protected int xpDropCooldown;

    public final MechHopperBlockEntity.FilterInventory filterInventory = new MechHopperBlockEntity.FilterInventory();
    public final MechHopperBlockEntity.Inventory inventory = new MechHopperBlockEntity.Inventory(INVENTORY_SIZE);
    public final CombinedStorage<ItemVariant, InventoryStorage> inventoryWrapper = new CombinedStorage<>(
            List.of(
                    InventoryStorage.of(filterInventory, null),
                    InventoryStorage.of(inventory, null)
            )
    );

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> MechHopperBlockEntity.this.mechPower;
                case 1 -> MechHopperBlockEntity.this.soulCount;
                case 2 -> MechHopperBlockEntity.this.xpCount;
                case 3 -> MechHopperBlockEntity.this.xpDropCooldown;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> MechHopperBlockEntity.this.mechPower = value > 0 ? 1 : 0;
                case 1 -> MechHopperBlockEntity.this.soulCount = value;
                case 2 -> MechHopperBlockEntity.this.xpCount = value;
                case 3 -> MechHopperBlockEntity.this.xpDropCooldown = value;
                default -> {}
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };

    public MechHopperBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.mechHopperBlockEntity, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MechHopperBlockEntity blockEntity) {
        if (!state.isOf(BwtBlocks.hopperBlock) || !state.get(MechHopperBlock.MECH_POWERED)) {
            return;
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.filterInventory.readNbt(nbt.getCompound("Filter"));
        this.inventory.readNbtList(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE));
        this.mechPower = nbt.getInt("mechPower");
        this.soulCount = nbt.getInt("soulCount");
        this.xpCount = nbt.getInt("xpCount");
        this.xpDropCooldown = nbt.getInt("xpDropCooldown");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("Filter", this.filterInventory.toNbt());
        nbt.put("Inventory", this.inventory.toNbtList());
        nbt.putInt("mechPower", this.mechPower);
        nbt.putInt("soulCount", this.soulCount);
        nbt.putInt("xpCount", this.xpCount);
        nbt.putInt("xpDropCooldown", this.xpDropCooldown);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MechHopperScreenHandler(syncId, playerInventory, filterInventory, inventory, propertyDelegate);
    }

    // Pick up items from above
    public static void onEntityCollided(Entity entity, MechHopperBlockEntity blockEntity) {
        ItemStack itemStack;
        if (entity instanceof ItemEntity itemEntity && !(itemStack = itemEntity.getStack()).isEmpty()) {
            int count = itemStack.getCount();
            try (Transaction transaction = Transaction.openOuter()) {
                long inserted = StorageUtil.insertStacking(
                        blockEntity.inventoryWrapper.parts.get(1).getSlots(),
                        ItemVariant.of(itemStack),
                        count,
                        transaction
                );
                itemEntity.setStack(itemEntity.getStack().copyWithCount((int) (count - inserted)));
                transaction.commit();
                blockEntity.inventory.markDirty();
            }
        }
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

    public class FilterInventory extends SimpleSingleStackInventory {
        public FilterInventory() {
            super(1);
        }

        @Override
        public void markDirty() {
            MechHopperBlockEntity.this.markDirty();
        }

        @Override
        public BlockEntity asBlockEntity() {
            return MechHopperBlockEntity.this;
        }
    }

    public class Inventory extends SimpleInventory {
        public Inventory(int size) {
            super(size);
        }
        @Override
        public void markDirty() {
            MechHopperBlockEntity.this.markDirty();
        }


    }
}
