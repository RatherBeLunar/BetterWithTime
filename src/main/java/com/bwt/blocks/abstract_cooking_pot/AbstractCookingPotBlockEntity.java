package com.bwt.blocks.abstract_cooking_pot;

import com.bwt.block_entities.ImplementedInventory;
import com.bwt.recipes.AbstractCookingPotRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public abstract class AbstractCookingPotBlockEntity<I extends AbstractCookingPotBlockEntity<I, R>, R extends AbstractCookingPotRecipe<I>> extends LootableContainerBlockEntity implements NamedScreenHandlerFactory, ImplementedInventory, SidedInventory {
    public static final int INVENTORY_SIZE = 27;
    protected static final int[] AVAILABLE_SLOTS = IntStream.range(0, INVENTORY_SIZE).toArray();
    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);

    protected int ticksToCompleteCook;
    protected int cookProgressTicks;

    public RecipeType<R> recipeType;

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
            BlockEntityType<I> blockEntityType,
            RecipeType<R> recipeType,
            BlockPos pos,
            BlockState state
    ) {
        super(blockEntityType, pos, state);
        ticksToCompleteCook = 200;
        this.recipeType = recipeType;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        this.cookProgressTicks = nbt.getInt("cookProgressTicks");
        this.ticksToCompleteCook = nbt.getInt("ticksToCompleteCook");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("cookProgressTicks", this.cookProgressTicks);
        nbt.putInt("ticksToCompleteCook", this.ticksToCompleteCook);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return AVAILABLE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    protected DefaultedList<ItemStack> method_11282() {
        return inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        //We provide *this* to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return new AbstractCookingPotScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new AbstractCookingPotScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected Text getContainerName() {
        return getDisplayName();
    }

    public static <I extends AbstractCookingPotBlockEntity<I, R>, R extends AbstractCookingPotRecipe<I>> void tick(World world, BlockPos pos, BlockState state, I blockEntity) {
        blockEntity.cookProgressTicks = (blockEntity.cookProgressTicks + 1) % 200;
        blockEntity.markDirty();

        if (blockEntity.cookProgressTicks != 0) {
            return;
        }

        RecipeManager recipeManager = world.getRecipeManager();
        List<RecipeEntry<R>> matches = recipeManager.getAllMatches(blockEntity.recipeType, blockEntity, world);
        if (matches.isEmpty()) {
            return;
        }
        // Get biggest
        matches.sort(Comparator.comparing((RecipeEntry<R> match) -> match.value().getIngredients().size()).reversed());
        R recipe = matches.get(0).value();
        recipe.craft(blockEntity, world.getRegistryManager());
    }
}
