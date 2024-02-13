package com.bwt.blocks.abstract_cooking_pot;

import com.bwt.recipes.AbstractCookingPotRecipe;
import com.bwt.recipes.AbstractCookingPotRecipeType;
import com.bwt.recipes.IngredientWithCount;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractCookingPotBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    protected static final int INVENTORY_SIZE = 27;
    protected static final int primaryFireFactor = 5;
    protected static final int secondaryFireFactor = 1; // This was changed to 3 later
    // "Time" is used loosely here, since the rate of change is affected by the amount of fire surrounding the pot
    public static final int timeToCompleteCook = 150 * ( primaryFireFactor + ( secondaryFireFactor * 8 ) );
    protected int cookProgressTime;


    public final AbstractCookingPotBlockEntity.Inventory inventory = new AbstractCookingPotBlockEntity.Inventory(INVENTORY_SIZE);
    public final InventoryStorage inventoryWrapper = InventoryStorage.of(inventory, null);


    public AbstractCookingPotRecipeType recipeType;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> AbstractCookingPotBlockEntity.this.cookProgressTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> AbstractCookingPotBlockEntity.this.cookProgressTime = value;
                default -> {}
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public AbstractCookingPotBlockEntity(
            BlockEntityType<? extends AbstractCookingPotBlockEntity> blockEntityType,
            AbstractCookingPotRecipeType recipeType,
            BlockPos pos,
            BlockState state
    ) {
        super(blockEntityType, pos, state);
        this.recipeType = recipeType;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory.readNbtList(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE));
        this.cookProgressTime = nbt.getInt("cookProgressTicks");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("Inventory", this.inventory.toNbtList());
        nbt.putInt("cookProgressTicks", this.cookProgressTime);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        //We provide *this* to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return new AbstractCookingPotScreenHandler(syncId, playerInventory, inventory, propertyDelegate);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    public static void tick(World world, BlockPos pos, BlockState state, AbstractCookingPotBlockEntity blockEntity) {
        FireData fireData = FireData.fromWorld(world, pos);
        if (fireData.fireFactor <= 0) {
            if (blockEntity.cookProgressTime != 0) {
                blockEntity.cookProgressTime = 0;
                blockEntity.markDirty();
            }
            return;
        }

        RecipeManager recipeManager = world.getRecipeManager();
        List<RecipeEntry<AbstractCookingPotRecipe>> matches = recipeManager.getAllMatches(blockEntity.recipeType, blockEntity.inventory, world);
        if (matches.isEmpty()) {
            if (blockEntity.cookProgressTime != 0) {
                blockEntity.cookProgressTime = 0;
                blockEntity.markDirty();
            }
            return;
        }

        blockEntity.cookProgressTime = blockEntity.cookProgressTime + fireData.fireFactor;
        if (blockEntity.cookProgressTime >= timeToCompleteCook) {
            blockEntity.cookProgressTime = 0;
            blockEntity.markDirty();
        }
        else {
            return;
        }

        // Get biggest
        Iterator<RecipeEntry<AbstractCookingPotRecipe>> matchIterator = matches.stream().sorted(
                Comparator.comparing(
                    (RecipeEntry<AbstractCookingPotRecipe> match)
                            -> match.value().getIngredients().size()
                ).reversed()
        ).iterator();

        while (matchIterator.hasNext()) {
            AbstractCookingPotRecipe match = matchIterator.next().value();
            boolean cookSucceeded = blockEntity.cookRecipe(match);
            if (cookSucceeded) {
                break;
            }
        }
    }

    // TODO: move fire logic into shared helper class
    public enum FireType {UNSTOKED, STOKED}

    public record FireData(int fireFactor, FireType fireType) {
        public static FireData fromWorld(World world, BlockPos pos) {
            int fireCount = 0;
            FireType fireType = FireType.UNSTOKED;
            BlockPos below = pos.down();
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockState state = world.getBlockState(below.offset(Direction.Axis.X, x).offset(Direction.Axis.Z, z));
                    if (x == 0 && z == 0 && !state.isOf(Blocks.FIRE) /* && !state.isOf(BwtBlocks.STOKED_FIRE) */) {
                        return new FireData(0, FireType.UNSTOKED);
                    }
                    if (state.isOf(Blocks.FIRE)) {
                        fireCount += 1;
                    }
//                    if (state.isOf(BwtBlocks.STOKED_FIRE)) {
//                        fireFactor += 1;
//                        fireType = FireType.STOKED;
//                    }
                }
            }
            int fireFactor = primaryFireFactor + (fireCount - 1) * secondaryFireFactor;
            return new FireData(fireFactor, fireType);
        }
    }



    public class Inventory extends SimpleInventory {
        public Inventory(int size) {
            super(size);
        }
        @Override
        public void markDirty() {
            AbstractCookingPotBlockEntity.this.markDirty();
        }
    }

    public boolean cookRecipe(AbstractCookingPotRecipe recipe) {
        try (Transaction transaction = Transaction.openOuter()) {
            // Spend ingredients
            for (IngredientWithCount ingredientWithCount : recipe.getIngredientsWithCount()) {
                long countToSpend = ingredientWithCount.count();
                while (countToSpend > 0) {
                    ItemVariant itemVariant = StorageUtil.findStoredResource(inventoryWrapper, ingredientWithCount::test);
                    if (itemVariant == null) {
                        continue;
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
            for (ItemStack result : recipe.getResults()) {
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

    // Pick up items from above like a hopper
    public static void onEntityCollided(Entity entity, AbstractCookingPotBlockEntity blockEntity) {
        ItemStack itemStack;
        if (entity instanceof ItemEntity itemEntity && !(itemStack = itemEntity.getStack()).isEmpty()) {
            int count = itemStack.getCount();
            try (Transaction transaction = Transaction.openOuter()) {
                long inserted = StorageUtil.insertStacking(blockEntity.inventoryWrapper.getSlots(), ItemVariant.of(itemStack), count, transaction);
                itemEntity.setStack(itemEntity.getStack().copyWithCount((int) (count - inserted)));
                transaction.commit();
                blockEntity.inventory.markDirty();
            }
        }
    }

    // Update fill level texture
    @Override
    public void markDirty() {
        super.markDirty();
        if (this.world == null) {
            return;
        }
        BlockState blockState = this.world.getBlockState(this.pos);
        this.world.setBlockState(this.pos, blockState.with(AbstractCookingPotBlock.LEVEL, getFillLevel()), AbstractCookingPotBlock.NOTIFY_LISTENERS);
    }

    public int getFillLevel() {
        if (inventory == null) {
            return 0;
        }
        float f = 0.0f;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.isEmpty()) continue;
            f += (float)itemStack.getCount() / (float)Math.min(inventory.getMaxCountPerStack(), itemStack.getMaxCount());
        }
        // TODO we use a LEVEL_8 property but we'd like a LEVEL_7 probably
        return MathHelper.lerpPositive(f / (float) inventory.size(), 0, 7);
    }
}
