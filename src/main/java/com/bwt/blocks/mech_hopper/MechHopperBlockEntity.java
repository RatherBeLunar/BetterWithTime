package com.bwt.blocks.mech_hopper;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.mixin.VanillaHopperInvoker;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.hopper_filter.HopperFilterRecipe;
import com.bwt.recipes.hopper_filter.HopperFilterRecipeInput;
import com.bwt.recipes.soul_bottling.SoulBottlingRecipe;
import com.bwt.recipes.soul_bottling.SoulBottlingRecipeInput;
import com.bwt.sounds.BwtSoundEvents;
import com.bwt.utils.SimpleSingleStackInventory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.Difficulty;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class MechHopperBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 19;
    protected static final int STACK_SIZE_TO_EJECT = 8;
    protected static final int SOUL_STORAGE_LIMIT = 8;
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


    public final FilterInventory filterInventory = new FilterInventory();
    public final HopperInventory hopperInventory = new HopperInventory(INVENTORY_SIZE - 1);
    public final CombinedStorage<ItemVariant, InventoryStorage> inventoryWrapper = new CombinedStorage<>(
            List.of(
                    InventoryStorage.of(filterInventory, null),
                    InventoryStorage.of(hopperInventory, null)
            )
    );
    private static final int[] AVAILABLE_SLOTS = IntStream.range(1, INVENTORY_SIZE).toArray();

    protected final ContainerData propertyDelegate = new ContainerData() {
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
        public int getCount() {
            return 1;
        }
    };

    public MechHopperBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.mechHopperBlockEntity, pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.filterInventory.readNbt(nbt.getCompound("Filter"), registryLookup);
        this.hopperInventory.fromTag(nbt.getList("Inventory", Tag.TAG_COMPOUND), registryLookup);
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
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.put("Filter", this.filterInventory.toNbt(registryLookup));
        nbt.put("Inventory", this.hopperInventory.createTag(registryLookup));
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
    public void setChanged() {
        int oldSlotsOccupied = slotsOccupied;
        slotsOccupied = ((int) hopperInventory.items.stream().filter(stack -> !stack.isEmpty()).count());
        int size = hopperInventory.getContainerSize();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            if ((oldSlotsOccupied == size && slotsOccupied != size) || (slotsOccupied == size && oldSlotsOccupied != size)) {
                level.blockUpdated(worldPosition, getBlockState().getBlock());
            }
        }
        super.setChanged();
    }

    public Item getFilterItem() {
        return filterInventory.getTheItem().getItem();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MechHopperBlockEntity blockEntity) {
        if (level.isClientSide || !state.is(BwtBlocks.hopperBlock)) {
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
                    blockEntity.attemptToEjectStack(level, pos);
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
            if (blockEntity.getFilterItem().equals(Items.SOUL_SAND)) {
                bottleSouls(level, blockEntity);
            }
            else {
                blockEntity.soulCount = 0;
            }
        }
    }

    protected static void bottleSouls(Level level, MechHopperBlockEntity blockEntity) {
        BlockState blockBelowState = level.getBlockState(blockEntity.getBlockPos().below());

        SoulBottlingRecipeInput recipeInput = new SoulBottlingRecipeInput(blockBelowState.getBlock());
        Optional<SoulBottlingRecipe> optionalRecipe = level.getRecipeManager().getRecipeFor(
                BwtRecipes.SOUL_BOTTLING_RECIPE_TYPE,
                recipeInput,
                level
        ).map(RecipeHolder::value);

        // If unpowered, we just need to check for explosions
        // Otherwise, nothing happens while unpowered
        if (blockEntity.mechPower <= 0) {
            if (blockEntity.soulCount >= SOUL_STORAGE_LIMIT) {
                soulOverloadExplode(level, blockEntity);
            }
            return;
        }

        // If no bottle is found, souls dissipate
        if (optionalRecipe.isEmpty()) {
            blockEntity.soulCount = 0;
            return;
        }

        SoulBottlingRecipe recipe = optionalRecipe.get();

        // Not enough souls to fill bottle yet
        if (blockEntity.soulCount < recipe.soulCount()) {
            return;
        }

        // Powered + enough souls + bottle found? Convert
        level.removeBlock(blockEntity.getBlockPos().below(), false);
        Containers.dropItemStack(level, blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY() - 1, blockEntity.getBlockPos().getZ(), recipe.getResult());

        // the rest of the souls escape (if any remain)
        blockEntity.soulCount = 0;
    }

    protected static void soulOverloadExplode(Level level, MechHopperBlockEntity blockEntity) {
        level.destroyBlock(blockEntity.getBlockPos(), false);
        Containers.dropContents(level, blockEntity.getBlockPos(), blockEntity.hopperInventory);
        level.playSound(null, blockEntity.getBlockPos(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS);
        if (!level.getDifficulty().equals(Difficulty.PEACEFUL)) {
            Ghast ghastEntity = new Ghast(EntityType.GHAST, level);
            ghastEntity.setPos(blockEntity.getBlockPos().getCenter());
            level.addFreshEntity(ghastEntity);
        }
    }

    public void attemptToEjectStack(Level level, BlockPos pos) {
        List<Integer> occupiedIndices = IntStream.range(0, hopperInventory.getContainerSize())
                .filter(i -> !getItem(i).isEmpty())
                .boxed().toList();

        if (occupiedIndices.isEmpty()) {
            return;
        }

        int stackIndex = occupiedIndices.get(level.random.nextIntBetweenInclusive(0, occupiedIndices.size() - 1));

        ItemStack invStack = getItem(stackIndex);

        int stackCountToDrop = Math.min(MechHopperBlockEntity.STACK_SIZE_TO_EJECT, invStack.getCount());

        BlockPos belowPos = pos.below();
        BlockState blockBelowState = level.getBlockState(belowPos);

        if (blockBelowState.is(BlockTags.AIR) || blockBelowState.canBeReplaced()) {
            ItemStack ejectStack = invStack.copyWithCount(stackCountToDrop);
            ejectStack(level, getBlockPos(), ejectStack);
            removeItem(stackIndex, stackCountToDrop);
        }

        Container inventoryBelow = HopperBlockEntity.getContainerAt(level, belowPos);
        if (inventoryBelow == null) {
            return;
        }
        if (VanillaHopperInvoker.isInventoryFull(inventoryBelow, Direction.UP)) {
            return;
        }
        stackCountToDrop = Math.min(stackCountToDrop, inventoryBelow.getMaxStackSize());
        for (int i = 0; i < hopperInventory.getContainerSize(); ++i) {
            if (getItem(i).isEmpty()) continue;
            ItemStack itemStack = getItem(i);
            ItemStack itemStack2 = HopperBlockEntity.addItem(this, inventoryBelow, removeItem(i, stackCountToDrop), Direction.UP);
            if (itemStack2.isEmpty()) {
                inventoryBelow.setChanged();
                return;
            }

            itemStack.setCount(itemStack.getCount() + itemStack2.getCount());
            if (itemStack.getCount() == 0) {
                setItem(i, ItemStack.EMPTY);
            }
        }
    }

    protected void ejectStack(Level level, BlockPos pos, ItemStack stack) {
        float xOffset = level.random.nextFloat() * 0.1F + 0.45F;
        float yOffset = -0.35F;
        float zOffset = level.random.nextFloat() * 0.1F + 0.45F;

        ItemEntity itemEntity = new ItemEntity(level, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, stack);
        itemEntity.setDeltaMovement(0.0f, -0.01f, 0.0f);

        itemEntity.setPickUpDelay(10);
        level.addFreshEntity(itemEntity);
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new MechHopperScreenHandler(syncId, playerInventory, filterInventory, hopperInventory, propertyDelegate);
    }

    // Pick up items from above
    public void onEntityCollided(Level level, Entity entity) {
        if (this.itemPickupCooldown > 0) {
            return;
        }
        if (entity instanceof ItemEntity itemEntity) {
            pickupItemEntity(level, itemEntity);
        }
    }

    protected Optional<HopperFilterRecipe> getMatchingRecipe(ItemStack itemStack) {
        HopperFilterRecipeInput recipeInput = new HopperFilterRecipeInput(getFilterItem(), itemStack);
        if (level == null) {
            return Optional.empty();
        }
        return level.getRecipeManager().getRecipeFor(
                BwtRecipes.HOPPER_FILTER_RECIPE_TYPE,
                recipeInput,
                level
        ).map(RecipeHolder::value);
    }

    protected boolean passesFilter(ItemStack itemStack) {
        return MechHopperBlock.filterMap.getOrDefault(getFilterItem(), s -> true).test(itemStack);
    }

    protected void pickupItemEntity(Level level, ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getItem();
        if (itemStack.isEmpty()) {
            return;
        }

        Optional<HopperFilterRecipe> optionalRecipe = getMatchingRecipe(itemStack);
        if (optionalRecipe.isPresent()) {
            processRecipe(level, itemEntity, optionalRecipe.get(), itemStack);
            return;
        }

        if (!passesFilter(itemStack)) {
            return;
        }
        try (Transaction transaction = Transaction.openOuter()) {
            int count = itemStack.getCount();
            long inserted = StorageUtil.insertStacking(
                    this.inventoryWrapper.parts.get(1).getSlots(),
                    ItemVariant.of(itemStack),
                    count,
                    transaction
            );
            itemEntity.setItem(itemEntity.getItem().copyWithCount((int) (count - inserted)));
            this.itemPickupCooldown++;
            transaction.commit();
            this.hopperInventory.setChanged();
        }
    }

    private void processRecipe(Level level, ItemEntity itemEntity, HopperFilterRecipe recipe, ItemStack itemStack) {
        int inputCount = itemStack.getCount();

        // Results get inserted into the hopper
        ItemStack resultStack = recipe.result();
        // Byproducts get spawned on top of the hopper
        ItemStack byproductStack = recipe.byproduct();

        // Operations may be limited by space in the hopper's inventory
        int operationsSucceeded;
        if (!resultStack.isEmpty()) {
            try (Transaction transaction = Transaction.openOuter()) {
                // If 1 input item converts to multiple output items,
                // operationsSucceeded only counts up the number of input items accepted
                operationsSucceeded = (int) StorageUtil.insertStacking(
                        this.inventoryWrapper.parts.get(1).getSlots(),
                        ItemVariant.of(resultStack),
                        (long) inputCount * resultStack.getCount(),
                        transaction
                ) / resultStack.getCount();
                this.itemPickupCooldown++;
                transaction.commit();
                this.hopperInventory.setChanged();
            }
        }
        else {
            // If inventory doesn't need to accept items, we can always process the whole stack
            operationsSucceeded = itemStack.getCount();
        }
        itemEntity.setItem(itemStack.copyWithCount(inputCount - operationsSucceeded));
        if (!byproductStack.isEmpty()) {
            int itemCount = operationsSucceeded * byproductStack.getCount();
            while (itemCount > 0) {
                int spawnCount = Math.min(itemCount, byproductStack.getItem().getDefaultMaxStackSize());
                this.spawnNewItemOnTop(level, itemEntity.position(), new ItemStack(byproductStack.getItem(), spawnCount));
                itemCount -= spawnCount;
            }
        }

        int soulsInserted = operationsSucceeded * recipe.soulCount();
        if (soulsInserted > 0) {
            int newSoulCount = this.soulCount + soulsInserted;
            if (newSoulCount > SOUL_STORAGE_LIMIT && this.mechPower <= 0) {
                soulOverloadExplode(level, this);
                return;
            }
            this.soulCount = Math.min(newSoulCount, SOUL_STORAGE_LIMIT);
            this.setChanged();
            // Play ghast noise
            level.playSound(null, this.worldPosition, BwtSoundEvents.SOUL_CONVERSION, SoundSource.BLOCKS, 1f, 1.5f);
        }
    }

    protected void spawnNewItemOnTop(Level level, Vec3 inputPos, ItemStack newItem) {
        Containers.dropItemStack(level, inputPos.x(), inputPos.y(), inputPos.z(), newItem);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag nbtCompound = saveWithoutMetadata(registryLookup);
        nbtCompound.putInt("slotsOccupied", slotsOccupied);
        nbtCompound.put("Filter", this.filterInventory.toNbt(registryLookup));
        return nbtCompound;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return hopperInventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < INVENTORY_SIZE - 1) {
            return hopperInventory.getItem(slot);
        }
        if (slot == INVENTORY_SIZE - 1) {
            return filterInventory.getTheItem();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return hopperInventory.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return hopperInventory.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        hopperInventory.setItem(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return hopperInventory.stillValid(player);
    }

    @Override
    public void clearContent() {
        hopperInventory.clearContent();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.UP ? AVAILABLE_SLOTS : new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        // Only insert via the top
        if (slot <= 0 || dir != Direction.UP) {
            return false;
        }
        // Don't process recipes via hoppers.
        // Enforce filter checks
        return getMatchingRecipe(stack).isEmpty() && passesFilter(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot > 0;
    }

    public class FilterInventory extends SimpleSingleStackInventory {
        public FilterInventory() {
            super(1);
        }

        @Override
        public void setChanged() {
            MechHopperBlockEntity.this.setChanged();
        }

        @Override
        public BlockEntity getContainerBlockEntity() {
            return MechHopperBlockEntity.this;
        }
    }

    public class HopperInventory extends SimpleContainer {
        public HopperInventory(int size) {
            super(size);
        }
        @Override
        public void setChanged() {
            MechHopperBlockEntity.this.setChanged();
        }
    }
}
