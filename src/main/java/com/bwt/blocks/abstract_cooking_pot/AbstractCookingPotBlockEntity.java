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
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCookingPotBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    public static final int INVENTORY_SIZE = 27;

    public final AbstractCookingPotBlockEntity.Inventory inventory = new AbstractCookingPotBlockEntity.Inventory(INVENTORY_SIZE);
    public final InventoryStorage inventoryWrapper = InventoryStorage.of(inventory, null);

    protected int ticksToCompleteCook;
    protected int cookProgressTicks;

    public AbstractCookingPotRecipeType recipeType;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> AbstractCookingPotBlockEntity.this.cookProgressTicks;
                case 1 -> AbstractCookingPotBlockEntity.this.ticksToCompleteCook;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    AbstractCookingPotBlockEntity.this.cookProgressTicks = value;
                case 1:
                    AbstractCookingPotBlockEntity.this.ticksToCompleteCook = value;
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public AbstractCookingPotBlockEntity(
            BlockEntityType<? extends AbstractCookingPotBlockEntity> blockEntityType,
            AbstractCookingPotRecipeType recipeType,
            BlockPos pos,
            BlockState state
    ) {
        super(blockEntityType, pos, state);
        ticksToCompleteCook = 200;
        this.recipeType = recipeType;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory.readNbtList(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE));
        this.cookProgressTicks = nbt.getInt("cookProgressTicks");
        this.ticksToCompleteCook = nbt.getInt("ticksToCompleteCook");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("Inventory", this.inventory.toNbtList());
        nbt.putInt("cookProgressTicks", this.cookProgressTicks);
        nbt.putInt("ticksToCompleteCook", this.ticksToCompleteCook);
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
        FireData fireData = getFireData(world, pos);
        if (!fireData.anyFire()) {
            if (blockEntity.cookProgressTicks != 0) {
                blockEntity.cookProgressTicks = 0;
                blockEntity.markDirty();
            }
            return;
        }

        RecipeManager recipeManager = world.getRecipeManager();
        List<RecipeEntry<AbstractCookingPotRecipe>> matches = recipeManager.getAllMatches(blockEntity.recipeType, blockEntity.inventory, world);
        if (matches.isEmpty()) {
            if (blockEntity.cookProgressTicks != 0) {
                blockEntity.cookProgressTicks = 0;
                blockEntity.markDirty();
            }
            return;
        }

        blockEntity.cookProgressTicks = blockEntity.cookProgressTicks + 1;
        if (blockEntity.cookProgressTicks >= 200) {
            blockEntity.cookProgressTicks = 0;
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

    public record FireData(int unstokedCount, int stokedCount) {
        public boolean anyFire() {return (unstokedCount + stokedCount) > 0;}
    }

    public static FireData getFireData(World world, BlockPos pos) {
        int unstokedCount = 0;
        int stokedCount = 0;
        BlockPos below = pos.down();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockState state = world.getBlockState(below.offset(Direction.Axis.X, x).offset(Direction.Axis.Y, z));
                if (state.isOf(Blocks.FIRE)) {
                    unstokedCount += 1;
                }
            }
        }
        return new FireData(unstokedCount, stokedCount);
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

    public boolean recipeFits(AbstractCookingPotRecipe recipe) {
        long emptySlotsAvailable = inventory.heldStacks.stream().filter(ItemStack::isEmpty).count();

        for (ItemStack result : recipe.getResults()) {
            // Find matching stacks and try to insert there first
            Optional<Integer> space = inventory.heldStacks.stream()
                    .filter(invStack -> ItemStack.areItemsEqual(invStack, result))
                    .map(invStack -> invStack.getMaxCount() - invStack.getCount())
                    .reduce(Integer::sum);
            emptySlotsAvailable -= (result.getCount() - space.orElse(0)) % result.getMaxCount();
            if (emptySlotsAvailable < 0) {
                return false;
            }
        }
        return true;
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
}
