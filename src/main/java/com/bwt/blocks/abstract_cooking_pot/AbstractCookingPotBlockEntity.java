package com.bwt.blocks.abstract_cooking_pot;

import com.bwt.items.BwtItems;
import com.bwt.recipes.cooking_pots.AbstractCookingPotRecipe;
import com.bwt.recipes.cooking_pots.AbstractCookingPotRecipeType;
import com.bwt.recipes.IngredientWithCount;
import com.bwt.recipes.cooking_pots.CookingPotRecipeInput;
import com.bwt.tags.BwtItemTags;
import com.bwt.utils.BlockPosAndState;
import com.bwt.utils.FireDataCluster;
import com.bwt.utils.OrderedRecipeMatcher;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCookingPotBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<AbstractCookingPotData>, Container {
    protected static final int INVENTORY_SIZE = 27;

    // "Time" is used loosely here, since the rate of change is affected by the amount of fire surrounding the pot
    public static final int timeToCompleteCook = 150 * ( FireDataCluster.primaryFireFactor + ( FireDataCluster.secondaryFireFactor * 8 ) );
    public static final int stackSizeToDrop = 8;
    public int slotsOccupied;

    protected int cookProgressTime;
    protected boolean isStoked;

    public final AbstractCookingPotBlockEntity.Inventory inventory = new AbstractCookingPotBlockEntity.Inventory(INVENTORY_SIZE);
    public final InventoryStorage inventoryWrapper = InventoryStorage.of(inventory, null);


    public final AbstractCookingPotRecipeType unstokedRecipeType;
    public final AbstractCookingPotRecipeType stokedRecipeType;

    protected final ContainerData propertyDelegate = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == 0) {
                return AbstractCookingPotBlockEntity.this.cookProgressTime;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                AbstractCookingPotBlockEntity.this.cookProgressTime = value;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public AbstractCookingPotBlockEntity(
            BlockEntityType<? extends AbstractCookingPotBlockEntity> blockEntityType,
            AbstractCookingPotRecipeType unstokedRecipeType,
            AbstractCookingPotRecipeType stokedRecipeType,
            BlockPos pos,
            BlockState state
    ) {
        super(blockEntityType, pos, state);
        this.unstokedRecipeType = unstokedRecipeType;
        this.stokedRecipeType = stokedRecipeType;
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.inventory.fromTag(nbt.getList("Inventory", Tag.TAG_COMPOUND), registryLookup);
        this.cookProgressTime = nbt.getInt("cookProgressTicks");
        this.isStoked = nbt.getBoolean("isStoked");
        this.slotsOccupied = nbt.getInt("slotsOccupied");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.put("Inventory", this.inventory.createTag(registryLookup));
        nbt.putInt("cookProgressTicks", this.cookProgressTime);
        nbt.putBoolean("isStoked", this.isStoked);
        nbt.putInt("slotsOccupied", this.slotsOccupied);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag nbtCompound = saveWithoutMetadata(registryLookup);
        nbtCompound.putInt("slotsOccupied", slotsOccupied);
        return nbtCompound;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbstractCookingPotBlockEntity blockEntity) {
        FireDataCluster fireDataCluster = FireDataCluster.fromWorld(level, pos);
        boolean isStoked = fireDataCluster.isStoked();
        if (isStoked != blockEntity.isStoked) {
            blockEntity.isStoked = isStoked;
            blockEntity.setChanged();
        }
        if (state.getValue(AbstractCookingPotBlock.TIP_DIRECTION) == Direction.UP) {
            blockEntity.cookItems(level, pos, fireDataCluster);
        }
        else {
            blockEntity.dumpItems(level, pos, state);
        }
    }

    protected void cookItems(Level level, BlockPos pos, FireDataCluster fireDataCluster) {
        if (!fireDataCluster.anyFirePresent()) {
            if (cookProgressTime != 0) {
                cookProgressTime = 0;
                setChanged();
            }
            return;
        }

        if (inventory.hasAnyMatching(itemStack -> itemStack.is(BwtItems.dungItem))
                && inventory.hasAnyMatching(itemStack -> itemStack.getComponents().get(DataComponents.FOOD) != null)) {
            spoilFood();
        }

        if (fireDataCluster.getStokedFactor() > 0) {
            int stokedExplosivesCount = inventory.getItems().stream()
                    .filter(itemStack -> itemStack.is(BwtItemTags.STOKED_EXPLOSIVES))
                    .map(ItemStack::getCount)
                    .reduce(Integer::sum)
                    .orElse(0);
            if (stokedExplosivesCount > 0) {
                explode(level, pos, stokedExplosivesCount);
                return;
            }
        }

        RecipeManager recipeManager = level.getRecipeManager();
        AbstractCookingPotRecipeType recipeTypeToGet = fireDataCluster.isStoked() ? stokedRecipeType : unstokedRecipeType;

        CookingPotRecipeInput recipeInput = new CookingPotRecipeInput(inventory.getItems());
        List<RecipeHolder<AbstractCookingPotRecipe>> matches = recipeManager.getRecipesFor(recipeTypeToGet, recipeInput, level);
        if (matches.isEmpty()) {
            if (cookProgressTime != 0) {
                cookProgressTime = 0;
                setChanged();
            }
            return;
        }

        cookProgressTime = cookProgressTime + fireDataCluster.getDominantFireTypeFactor();
        if (cookProgressTime >= timeToCompleteCook) {
            cookProgressTime = 0;
            setChanged();
        }
        else {
            return;
        }

        // Cook the first recipe we can
        OrderedRecipeMatcher.getFirstRecipe(matches, inventory.getItems(), this::cookRecipe);
    }

    private void dumpItems(Level level, BlockPos pos, BlockState state) {
        Optional<ItemStack> firstItemToDump = inventory.getItems().stream()
                .filter(itemStack -> !itemStack.isEmpty())
                .findFirst();
        if (firstItemToDump.isEmpty()) {
            return;
        }

        ItemStack itemStack = firstItemToDump.get();

        if (itemStack.isEmpty()) {
            return;
        }

        Direction facing = state.getValue(AbstractCookingPotBlock.TIP_DIRECTION);
        BlockPosAndState dumpPosAndState = BlockPosAndState.of(level, pos.relative(facing));
        if (!dumpPosAndState.state().canBeReplaced() && !dumpPosAndState.state().getCollisionShape(level, dumpPosAndState.pos()).isEmpty()) {
            return;
        }
        ejectStack(level, itemStack, facing, dumpPosAndState);
    }

    private void ejectStack(Level level, ItemStack itemStack, Direction facing, BlockPosAndState dumpPosAndState) {
        int stackSizeToDump = Math.min(itemStack.getCount(), stackSizeToDrop);

        Vec3 entityPos = dumpPosAndState.pos().getCenter().subtract(0, 0.25f, 0);
        ItemEntity itemEntity = new ItemEntity(level, entityPos.x, entityPos.y, entityPos.z, itemStack.copyWithCount(stackSizeToDump));
        Vec3 itemVelocity = Vec3.atLowerCornerOf(facing.getNormal()).scale(0.1);
        itemEntity.setDeltaMovement(itemVelocity);
        itemEntity.setPickUpDelay(10);

        itemStack.shrink(stackSizeToDump);
        setChanged();
        level.addFreshEntity(itemEntity);
    }

    private static void explode(Level level, BlockPos pos, int stokedExplosivesCount) {
        level.destroyBlock(pos, true);
        float explosionStrength = Math.min(Math.max(stokedExplosivesCount / 6.4f, 2f), 10f);
        level.explode(null, pos.getX(), pos.getY(), pos.getZ(), explosionStrength, true, Level.ExplosionInteraction.BLOCK);
    }

    @Override
    public int getContainerSize() {
        return inventory.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return inventory.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return inventory.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.setItem(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }

    @Override
    public void clearContent() {
        inventory.clearContent();
    }

    public class Inventory extends SimpleContainer {
        public Inventory(int size) {
            super(size);
        }
        @Override
        public void setChanged() {
            AbstractCookingPotBlockEntity.this.setChanged();
        }
    }

    public boolean cookRecipe(AbstractCookingPotRecipe recipe) {
        try (Transaction transaction = Transaction.openOuter()) {
            List<ItemStack> remaindersAndResults = new ArrayList<>(recipe.getResults());
            // Spend ingredients
            for (IngredientWithCount ingredientWithCount : recipe.getIngredientsWithCount()) {
                long countToSpend = ingredientWithCount.count();
                while (countToSpend > 0) {
                    ItemVariant itemVariant = StorageUtil.findStoredResource(inventoryWrapper, ingredientWithCount::test);
                    if (itemVariant == null) {
                        continue;
                    }
                    if (itemVariant.getItem().hasCraftingRemainingItem()) {
                        Item remainder = itemVariant.getItem().getCraftingRemainingItem();
                        if (remainder != null) {
                            remaindersAndResults.add(new ItemStack(remainder, (int) countToSpend));
                        }
                    }
                    long taken = inventoryWrapper.extract(itemVariant, countToSpend, transaction);
                    countToSpend -= taken;
                    if (taken == 0) {
                        transaction.abort();
                        return false;
                    }
                }
            }
            // Add results
            for (ItemStack result : remaindersAndResults) {
                long inserted = StorageUtil.insertStacking(inventoryWrapper.getSlots(), ItemVariant.of(result), result.getCount(), transaction);
                if (inserted < result.getCount()) {
                    transaction.abort();
                    return false;
                }
            }
            transaction.commit();
            return true;
        }
    }

    public void spoilFood() {
        try (Transaction transaction = Transaction.openOuter()) {
            for (SingleSlotStorage<ItemVariant> slot : inventoryWrapper.getSlots()) {
                ItemVariant resource = slot.getResource();
                if (resource.toStack().getComponents().get(DataComponents.FOOD) == null) {
                    continue;
                }
                long count = slot.extract(resource, resource.getItem().getDefaultMaxStackSize(), transaction);
                slot.insert(ItemVariant.of(BwtItems.foulFoodItem), count, transaction);
            }
            transaction.commit();
        }
    }

    // Pick up items from above like a hopper
    public static void onEntityCollided(Entity entity, AbstractCookingPotBlockEntity blockEntity) {
        ItemStack itemStack;
        if (entity instanceof ItemEntity itemEntity && !(itemStack = itemEntity.getItem()).isEmpty()) {
            int count = itemStack.getCount();
            try (Transaction transaction = Transaction.openOuter()) {
                long inserted = StorageUtil.insertStacking(blockEntity.inventoryWrapper.getSlots(), ItemVariant.of(itemStack), count, transaction);
                itemEntity.setItem(itemEntity.getItem().copyWithCount((int) (count - inserted)));
                transaction.commit();
                blockEntity.inventory.setChanged();
            }
        }
    }

    // Update fill level texture
    @Override
    public void setChanged() {
        slotsOccupied = ((int) inventory.items.stream().filter(stack -> !stack.isEmpty()).count());
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
        super.setChanged();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public AbstractCookingPotData getScreenOpeningData(ServerPlayer player) {
        return new AbstractCookingPotData(this.isStoked);
    }
}
