package com.bwt.blocks.soul_forge;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.mixin.accessors.CraftingInventoryAccessorMixin;
import com.bwt.recipes.BwtRecipes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SoulForgeBlockEntity extends LockableContainerBlockEntity implements SidedInventory, RecipeUnlocker, RecipeInputProvider {
    public static final int WIDTH = 4;
    public static final int HEIGHT = 4;
    public static final int GRID_SIZE = WIDTH * HEIGHT;
    public static final int INVENTORY_SIZE = GRID_SIZE + 1;

    private static final int[] OUTPUT_SLOTS = IntStream.range(0, GRID_SIZE).toArray();
    private static final int[] INPUT_SLOTS = IntStream.range(1, GRID_SIZE).toArray();
    public DefaultedList<ItemStack> inventory;
    public ItemStack output = ItemStack.EMPTY;
    private RecipeEntry<?> lastRecipe;
    private final CraftingInventory craftingInventory = new CraftingInventory(null, WIDTH, HEIGHT);
    private final ArrayList<SoulForgeScreenHandler> openScreens = new ArrayList<>();

    public SoulForgeBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.soulForgeBlockEntity, pos, state);
        this.inventory = DefaultedList.ofSize(GRID_SIZE, ItemStack.EMPTY);
        ((CraftingInventoryAccessorMixin) craftingInventory).setInventory(inventory);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new SoulForgeScreenHandler(syncId, playerInventory, this);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        if (!output.isEmpty()) {
            nbt.put("Output", output.encode(registryLookup));
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        if (nbt.contains("Output")) {
            ItemStack.fromNbt(registryLookup, nbt.getCompound("Output")).ifPresent(itemStack -> this.output = itemStack);
        }
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.crafting");
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    public int[] getAvailableSlots(Direction direction) {
        return (direction == Direction.DOWN && (!output.isEmpty() || getCurrentRecipe().isPresent())) ? OUTPUT_SLOTS : INPUT_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return slot > 0 && getStack(slot).isEmpty();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot != 0 || !output.isEmpty() || getCurrentRecipe().isPresent();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return slot != 0 && slot <= size();
    }

    @Override
    public int size() {
        return 10;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) return false;
        }
        return output.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot > 0) return this.inventory.get(slot - 1);
        if (!output.isEmpty()) return output;
        Optional<RecipeEntry<CraftingRecipe>> recipe = getCurrentRecipe();
        return recipe
                .filter(craftingRecipe -> world != null)
                .map(craftingRecipe -> craftingRecipe.value().craft(craftingInventory.createRecipeInput(), world.getRegistryManager()))
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot == 0) {
            if (output.isEmpty()) output = craft();
            return output.split(amount);
        }
        return Inventories.splitStack(this.inventory, slot - 1, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot == 0) {
            ItemStack output = this.output;
            this.output = ItemStack.EMPTY;
            return output;
        }
        return Inventories.removeStack(this.inventory, slot - 1);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot == 0) {
            output = stack;
            return;
        }
        inventory.set(slot - 1, stack);
        markDirty();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        openScreens.forEach(screen -> screen.onContentChanged(this));
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return player.getBlockPos().getSquaredDistance(this.pos) <= 64.0D;
    }

    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        for (ItemStack stack : this.inventory) finder.addInput(stack);
    }

    @Override
    public void setLastRecipe(RecipeEntry<?> recipe) {
        lastRecipe = recipe;
    }

    @Override
    public RecipeEntry<?> getLastRecipe() {
        return lastRecipe;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    private Optional<RecipeEntry<CraftingRecipe>> getCurrentRecipe() {
        // No need to find recipes if the inventory is empty. Cannot craft anything.
        if (this.world == null || this.isEmpty()) return Optional.empty();

        RecipeEntry<?> lastRecipe = getLastRecipe();
        RecipeManager manager = this.world.getRecipeManager();

        if (lastRecipe != null) {
            List<RecipeEntry<CraftingRecipe>> regularRecipes = manager.listAllOfType(RecipeType.CRAFTING);
            List<RecipeEntry<CraftingRecipe>> soulForgeRecipes = manager.listAllOfType(BwtRecipes.SOUL_FORGE_RECIPE_TYPE);
            Optional<RecipeEntry<CraftingRecipe>> optionalRecipe = Stream.concat(regularRecipes.stream(), soulForgeRecipes.stream()).filter(recipe -> recipe.id().equals(lastRecipe.id())).findFirst();
            if (optionalRecipe.isPresent() && optionalRecipe.get().value().matches(craftingInventory.createRecipeInput(), world)) {
                return optionalRecipe;
            }
        }
        Optional<RecipeEntry<CraftingRecipe>> recipe = manager.getFirstMatch(RecipeType.CRAFTING, craftingInventory.createRecipeInput(), world);
        if (recipe.isPresent()) {
            setLastRecipe(recipe.get());
            return recipe;
        }
        Optional<RecipeEntry<CraftingRecipe>> recipe2 = manager.getFirstMatch(BwtRecipes.SOUL_FORGE_RECIPE_TYPE, craftingInventory.createRecipeInput(), world);
        if (recipe2.isPresent()) {
            setLastRecipe(recipe2.get());
            return recipe2;
        }
        return Optional.empty();
    }

    private ItemStack craft() {
        if (this.world == null) return ItemStack.EMPTY;
        Optional<RecipeEntry<CraftingRecipe>> optionalRecipe = getCurrentRecipe();
        if (optionalRecipe.isEmpty()) return ItemStack.EMPTY;
        RecipeEntry<CraftingRecipe> recipe = optionalRecipe.get();
        ItemStack result = recipe.value().craft(craftingInventory.createRecipeInput(), world.getRegistryManager());
        DefaultedList<ItemStack> remaining = world.getRecipeManager().getRemainingStacks(RecipeType.CRAFTING, craftingInventory.createRecipeInput(), world);
        for (int i = 0; i < GRID_SIZE; i++) {
            ItemStack current = inventory.get(i);
            ItemStack remainingStack = remaining.get(i);
            if (!current.isEmpty()) current.decrement(1);
            if (!remainingStack.isEmpty()) {
                if (current.isEmpty()) {
                    inventory.set(i, remainingStack);
                } else if (ItemStack.areItemsAndComponentsEqual(current, remainingStack)) {
                    current.increment(remainingStack.getCount());
                } else {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), remainingStack);
                }
            }
        }
        markDirty();
        return result;
    }

    public void onContainerClose(SoulForgeScreenHandler container) {
        this.openScreens.remove(container);
    }

    public boolean matches(Recipe<CraftingRecipeInput> recipe) {
        return this.world != null && recipe.matches(this.craftingInventory.createRecipeInput(), this.world);
    }
}
