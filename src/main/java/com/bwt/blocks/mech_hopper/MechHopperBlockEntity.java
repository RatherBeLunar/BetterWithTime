package com.bwt.blocks.mech_hopper;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.mixin.VanillaHopperInvoker;
import com.bwt.utils.SimpleSingleStackInventory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class MechHopperBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    public static final int INVENTORY_SIZE = 19;
    protected static final int STACK_SIZE_TO_EJECT = 8;
    protected static final int PICKUP_COOLDOWN = 3;
    protected static final int ITEM_DROP_COOLDOWN = 3;


    protected int mechPower;
    protected int soulCount;
    protected int xpCount;
    protected int itemPickupCooldown;
    protected int itemDropCooldown;
    protected int xpPickupCooldown;
    protected int xpDropCooldown;
    public int slotsOccupied;
    protected boolean outputBlocked;


    public final MechHopperBlockEntity.FilterInventory filterInventory = new MechHopperBlockEntity.FilterInventory();
    public final HopperInventory hopperInventory = new HopperInventory(INVENTORY_SIZE - 1);
    public final CombinedStorage<ItemVariant, InventoryStorage> inventoryWrapper = new CombinedStorage<>(
            List.of(
                    InventoryStorage.of(filterInventory, null),
                    InventoryStorage.of(hopperInventory, null)
            )
    );

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> MechHopperBlockEntity.this.mechPower;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> MechHopperBlockEntity.this.mechPower = value > 0 ? 1 : 0;
                default -> {}
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public MechHopperBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.mechHopperBlockEntity, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.filterInventory.readNbt(nbt.getCompound("Filter"));
        this.hopperInventory.readNbtList(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE));
        this.mechPower = nbt.getInt("mechPower");
        this.soulCount = nbt.getInt("soulCount");
        this.xpCount = nbt.getInt("xpCount");
        this.itemPickupCooldown = nbt.getInt("itemPickupCooldown");
        this.itemDropCooldown = nbt.getInt("itemDropCooldown");
        this.xpPickupCooldown = nbt.getInt("xpPickupCooldown");
        this.xpDropCooldown = nbt.getInt("xpDropCooldown");
        this.slotsOccupied = nbt.getInt("slotsOccupied");
        this.outputBlocked = nbt.getBoolean("outputBlocked");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("Filter", this.filterInventory.toNbt());
        nbt.put("Inventory", this.hopperInventory.toNbtList());
        nbt.putInt("mechPower", this.mechPower);
        nbt.putInt("soulCount", this.soulCount);
        nbt.putInt("xpCount", this.xpCount);
        nbt.putInt("itemPickupCooldown", this.itemPickupCooldown);
        nbt.putInt("itemDropCooldown", this.itemDropCooldown);
        nbt.putInt("xpPickupCooldown", this.xpPickupCooldown);
        nbt.putInt("xpDropCooldown", this.xpDropCooldown);
        nbt.putInt("slotsOccupied", this.slotsOccupied);
        nbt.putBoolean("outputBlocked", this.outputBlocked);
    }

    @Override
    public void markDirty() {
        slotsOccupied = ((int) hopperInventory.heldStacks.stream().filter(stack -> !stack.isEmpty()).count());
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
        }
        super.markDirty();
    }

    public Item getFilterItem() {
        return filterInventory.getStack().getItem();
    }

    public static void tick(World world, BlockPos pos, BlockState state, MechHopperBlockEntity blockEntity) {
        if (world.isClient || !state.isOf(BwtBlocks.hopperBlock)) {
            return;
        }

        if (blockEntity.itemPickupCooldown > 0) {
            blockEntity.itemPickupCooldown = (blockEntity.itemPickupCooldown + 1) % PICKUP_COOLDOWN;
        }

        if (blockEntity.mechPower > 0)
        {
//            attemptToEjectXP();

            if (!blockEntity.outputBlocked) {
                // the hopper is powered, eject items
                blockEntity.itemDropCooldown += 1;

                if (blockEntity.itemDropCooldown >= MechHopperBlockEntity.ITEM_DROP_COOLDOWN) {
                    blockEntity.attemptToEjectStack(world, pos);
                    blockEntity.itemDropCooldown = 0;
                }
            }
            else {
                blockEntity.itemDropCooldown = 0;
            }
        }
        else {
            blockEntity.itemDropCooldown = 0;
            blockEntity.xpDropCooldown = 0;
        }

        if (blockEntity.soulCount > 0) {
            // souls can only be trapped if there's a soul sand filter on the hopper

//            if ( m_iFilterItemID == Block.slowSand.blockID )
//            {
//                int iBlockBelowID = worldObj.getBlockId( xCoord, yCoord - 1, zCoord );
//                int iBlockBelowMetaData = worldObj.getBlockMetadata( xCoord, yCoord - 1, zCoord );
//
//                if (hopperPowered)
//                {
//                    if ( iBlockBelowID != FCBetterThanWolves.fcAestheticNonOpaque.blockID ||
//                            iBlockBelowMetaData != FCBlockAestheticNonOpaque.m_iSubtypeUrn )
//                    {
//                        m_iContainedSoulCount = 0;
//                    }
//                }
//
//                if ( m_iContainedSoulCount >= m_iOverloadSoulCount )
//                {
//                    if ( hopperPowered &&
//                            iBlockBelowID == FCBetterThanWolves.fcAestheticNonOpaque.blockID &&
//                            iBlockBelowMetaData == FCBlockAestheticNonOpaque.m_iSubtypeUrn )
//                    {
//                        // convert the urn below to a soul urn
//
//                        worldObj.setBlockWithNotify( xCoord, yCoord - 1, zCoord, 0 );
//
//                        ItemStack newItemStack = new ItemStack( FCBetterThanWolves.fcItemSoulUrn.itemID,
//                                1, 0 );
//
//                        FCUtilsItem.EjectStackWithRandomOffset( worldObj,
//                                xCoord, yCoord - 1, zCoord, newItemStack );
//
//                        // the rest of the souls escape (if any remain)
//
//                        m_iContainedSoulCount = 0;
//                    }
//                    else
//                    {
//                        // the hopper has become overloaded with souls.  Destroy it and generate a ghast.
//
//                        HopperSoulOverload();
//                    }
//                }
//            }
//            else
//            {
//                m_iContainedSoulCount = 0;
//            }
        }
    }

    public void attemptToEjectStack(World world, BlockPos pos) {
        List<Integer> occupiedIndices = IntStream.range(0, size())
                .filter(i -> !getStack(i).isEmpty())
                .boxed().toList();

        if (occupiedIndices.isEmpty()) {
            return;
        }

        int stackIndex = occupiedIndices.get(world.random.nextBetween(0, occupiedIndices.size() - 1));

        ItemStack invStack = getStack(stackIndex);

        int stackCountToDrop = Math.min(MechHopperBlockEntity.STACK_SIZE_TO_EJECT, invStack.getCount());

        BlockPos belowPos = pos.down();
        BlockState blockBelowState = world.getBlockState(belowPos);

        if (blockBelowState.isAir() || blockBelowState.isReplaceable()) {
            ItemStack ejectStack = invStack.copyWithCount(stackCountToDrop);
            ejectStack(world, getPos(), ejectStack);
            removeStack(stackIndex, stackCountToDrop);
        }

        Inventory inventoryBelow = HopperBlockEntity.getInventoryAt(world, belowPos);
        if (inventoryBelow == null) {
            return;
        }
        if (VanillaHopperInvoker.isInventoryFull(inventoryBelow, Direction.UP)) {
            return;
        }
        for (int i = 0; i < size(); ++i) {
            if (getStack(i).isEmpty()) continue;
            ItemStack itemStack = getStack(i).copy();
            ItemStack itemStack2 = HopperBlockEntity.transfer(this, inventoryBelow, removeStack(i, stackCountToDrop), Direction.UP);
            if (itemStack2.isEmpty()) {
                inventoryBelow.markDirty();
                return;
            }
            setStack(i, itemStack);
        }
    }

    protected void ejectStack(World world, BlockPos pos, ItemStack stack) {
        float xOffset = world.random.nextFloat() * 0.1F + 0.45F;
        float yOffset = -0.35F;
        float zOffset = world.random.nextFloat() * 0.1F + 0.45F;

        ItemEntity itemEntity = new ItemEntity(world, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, stack);
        itemEntity.setVelocity(0.0f, -0.01f, 0.0f);

        itemEntity.setPickupDelay(10);
        world.spawnEntity(itemEntity);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MechHopperScreenHandler(syncId, playerInventory, filterInventory, hopperInventory, propertyDelegate);
    }

    // Pick up items from above
    public static void onEntityCollided(Entity entity, MechHopperBlockEntity blockEntity) {
        if (blockEntity.itemPickupCooldown > 0) {
            return;
        }
        ItemStack itemStack;
        if (
                entity instanceof ItemEntity itemEntity
                && !(itemStack = itemEntity.getStack()).isEmpty()
                && MechHopperBlock.filterMap.getOrDefault(blockEntity.getFilterItem(), s -> false).test(itemStack)
        ) {
            int count = itemStack.getCount();
            try (Transaction transaction = Transaction.openOuter()) {
                long inserted = StorageUtil.insertStacking(
                        blockEntity.inventoryWrapper.parts.get(1).getSlots(),
                        ItemVariant.of(itemStack),
                        count,
                        transaction
                );
                itemEntity.setStack(itemEntity.getStack().copyWithCount((int) (count - inserted)));
                blockEntity.itemPickupCooldown++;
                transaction.commit();
                blockEntity.hopperInventory.markDirty();
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = createNbt();
        nbtCompound.putInt("slotsOccupied", slotsOccupied);
        nbtCompound.put("Filter", this.filterInventory.toNbt());
        return nbtCompound;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public int size() {
        return hopperInventory.size();
    }

    @Override
    public boolean isEmpty() {
        return hopperInventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return hopperInventory.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return hopperInventory.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return hopperInventory.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        hopperInventory.setStack(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return hopperInventory.canPlayerUse(player);
    }

    @Override
    public void clear() {
        hopperInventory.clear();
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

    public class HopperInventory extends SimpleInventory {
        public HopperInventory(int size) {
            super(size);
        }
        @Override
        public void markDirty() {
            MechHopperBlockEntity.this.markDirty();
        }


    }
}
