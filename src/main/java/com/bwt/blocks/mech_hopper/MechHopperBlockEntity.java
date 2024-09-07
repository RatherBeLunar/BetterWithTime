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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class MechHopperBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
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
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.filterInventory.readNbt(nbt.getCompound("Filter"), registryLookup);
        this.hopperInventory.readNbtList(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE), registryLookup);
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
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.put("Filter", this.filterInventory.toNbt(registryLookup));
        nbt.put("Inventory", this.hopperInventory.toNbtList(registryLookup));
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
        int oldSlotsOccupied = slotsOccupied;
        slotsOccupied = ((int) hopperInventory.heldStacks.stream().filter(stack -> !stack.isEmpty()).count());
        int size = hopperInventory.size();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
            if ((oldSlotsOccupied == size && slotsOccupied != size) || (slotsOccupied == size && oldSlotsOccupied != size)) {
                world.updateNeighbors(pos, getCachedState().getBlock());
            }
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
            if (blockEntity.getFilterItem().equals(Items.SOUL_SAND)) {
                bottleSouls(world, blockEntity);
            }
            else {
                blockEntity.soulCount = 0;
            }
        }
    }

    protected static void bottleSouls(World world, MechHopperBlockEntity blockEntity) {
        BlockState blockBelowState = world.getBlockState(blockEntity.getPos().down());

        SoulBottlingRecipeInput recipeInput = new SoulBottlingRecipeInput(blockBelowState.getBlock());
        Optional<SoulBottlingRecipe> optionalRecipe = world.getRecipeManager().getFirstMatch(
                BwtRecipes.SOUL_BOTTLING_RECIPE_TYPE,
                recipeInput,
                world
        ).map(RecipeEntry::value);

        // If unpowered, we just need to check for explosions
        // Otherwise, nothing happens while unpowered
        if (blockEntity.mechPower <= 0) {
            if (blockEntity.soulCount >= SOUL_STORAGE_LIMIT) {
                soulOverloadExplode(world, blockEntity);
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
        world.removeBlock(blockEntity.getPos().down(), false);
        ItemScatterer.spawn(world, blockEntity.getPos().getX(), blockEntity.getPos().getY() - 1, blockEntity.getPos().getZ(), recipe.getResult());

        // the rest of the souls escape (if any remain)
        blockEntity.soulCount = 0;
    }

    protected static void soulOverloadExplode(World world, MechHopperBlockEntity blockEntity) {
        world.breakBlock(blockEntity.getPos(), false);
        ItemScatterer.spawn(world, blockEntity.getPos(), blockEntity.hopperInventory);
        world.playSound(null, blockEntity.getPos(), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS);
        if (!world.getDifficulty().equals(Difficulty.PEACEFUL)) {
            GhastEntity ghastEntity = new GhastEntity(EntityType.GHAST, world);
            ghastEntity.setPosition(blockEntity.getPos().toCenterPos());
            world.spawnEntity(ghastEntity);
        }
    }

    public void attemptToEjectStack(World world, BlockPos pos) {
        List<Integer> occupiedIndices = IntStream.range(0, hopperInventory.size())
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
        for (int i = 0; i < hopperInventory.size(); ++i) {
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
    public static void onEntityCollided(World world, Entity entity, MechHopperBlockEntity blockEntity) {
        if (blockEntity.itemPickupCooldown > 0) {
            return;
        }
        if (entity instanceof ItemEntity itemEntity) {
            pickupItemEntity(world, itemEntity, blockEntity);
        }
    }

    protected static void pickupItemEntity(World world, ItemEntity itemEntity, MechHopperBlockEntity blockEntity) {
        ItemStack itemStack = itemEntity.getStack();
        if (itemStack.isEmpty()) {
            return;
        }
        Item filterItem = blockEntity.getFilterItem();

        HopperFilterRecipeInput recipeInput = new HopperFilterRecipeInput(filterItem, itemStack);
        Optional<HopperFilterRecipe> optionalRecipe = world.getRecipeManager().getFirstMatch(
                BwtRecipes.HOPPER_FILTER_RECIPE_TYPE,
                recipeInput,
                world
        ).map(RecipeEntry::value);

        if (optionalRecipe.isPresent()) {
            processRecipe(world, itemEntity, blockEntity, optionalRecipe.get(), itemStack);
            return;
        }

        if (!MechHopperBlock.filterMap.getOrDefault(filterItem, s -> true).test(itemStack)) {
            return;
        }
        try (Transaction transaction = Transaction.openOuter()) {
            int count = itemStack.getCount();
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

    private static void processRecipe(World world, ItemEntity itemEntity, MechHopperBlockEntity blockEntity, HopperFilterRecipe recipe, ItemStack itemStack) {
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
                        blockEntity.inventoryWrapper.parts.get(1).getSlots(),
                        ItemVariant.of(resultStack),
                        (long) inputCount * resultStack.getCount(),
                        transaction
                ) / resultStack.getCount();
                blockEntity.itemPickupCooldown++;
                transaction.commit();
                blockEntity.hopperInventory.markDirty();
            }
        }
        else {
            // If inventory doesn't need to accept items, we can always process the whole stack
            operationsSucceeded = itemStack.getCount();
        }
        itemEntity.setStack(itemStack.copyWithCount(inputCount - operationsSucceeded));
        if (!byproductStack.isEmpty()) {
            int itemCount = operationsSucceeded * byproductStack.getCount();
            while (itemCount > 0) {
                int spawnCount = Math.min(itemCount, byproductStack.getItem().getMaxCount());
                blockEntity.spawnNewItemOnTop(world, itemEntity.getPos(), new ItemStack(byproductStack.getItem(), spawnCount));
                itemCount -= spawnCount;
            }
        }

        int soulsInserted = operationsSucceeded * recipe.soulCount();
        if (soulsInserted > 0) {
            int newSoulCount = blockEntity.soulCount + soulsInserted;
            if (newSoulCount > SOUL_STORAGE_LIMIT && blockEntity.mechPower <= 0) {
                soulOverloadExplode(world, blockEntity);
                return;
            }
            blockEntity.soulCount = Math.min(newSoulCount, SOUL_STORAGE_LIMIT);
            blockEntity.markDirty();
            // Play ghast noise
            world.playSound(null, blockEntity.pos, BwtSoundEvents.SOUL_CONVERSION, SoundCategory.BLOCKS, 1f, 1.5f);
        }
    }

    protected void spawnNewItemOnTop(World world, Vec3d inputPos, ItemStack newItem) {
        ItemScatterer.spawn(world, inputPos.getX(), inputPos.getY(), inputPos.getZ(), newItem);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbtCompound = createNbt(registryLookup);
        nbtCompound.putInt("slotsOccupied", slotsOccupied);
        nbtCompound.put("Filter", this.filterInventory.toNbt(registryLookup));
        return nbtCompound;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public int size() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return hopperInventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot < INVENTORY_SIZE - 1) {
            return hopperInventory.getStack(slot);
        }
        if (slot == INVENTORY_SIZE - 1) {
            return filterInventory.getStack();
        }
        return ItemStack.EMPTY;
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
