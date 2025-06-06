package com.bwt.blocks.mill_stone;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.IngredientWithCount;
import com.bwt.recipes.mill_stone.MillStoneRecipe;
import com.bwt.recipes.mill_stone.MillStoneRecipeInput;
import com.bwt.utils.OrderedRecipeMatcher;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class MillStoneBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    protected int grindProgressTime;
    public static final int timeToGrind = 200;
    protected static final int INVENTORY_SIZE = 3;

    public final MillStoneBlockEntity.Inventory inventory = new com.bwt.blocks.mill_stone.MillStoneBlockEntity.Inventory(INVENTORY_SIZE);
    public final InventoryStorage inventoryWrapper = InventoryStorage.of(inventory, null);

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> MillStoneBlockEntity.this.grindProgressTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> MillStoneBlockEntity.this.grindProgressTime = value;
                default -> {}
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public MillStoneBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.millStoneBlockEntity, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MillStoneBlockEntity blockEntity) {
        if (!state.isOf(BwtBlocks.millStoneBlock) || !state.get(MillStoneBlock.MECH_POWERED)) {
            return;
        }
        MillStoneRecipeInput recipeInput = new MillStoneRecipeInput(blockEntity.inventory.getHeldStacks());
        List<RecipeEntry<MillStoneRecipe>> matches = world.getRecipeManager().getAllMatches(BwtRecipes.MILL_STONE_RECIPE_TYPE, recipeInput, world);
        if (matches.isEmpty()) {
            if (blockEntity.grindProgressTime != 0) {
                blockEntity.grindProgressTime = 0;
                blockEntity.markDirty();
            }
            return;
        }

        blockEntity.grindProgressTime += 1;
        if (blockEntity.grindProgressTime >= timeToGrind) {
            blockEntity.grindProgressTime = 0;
            blockEntity.markDirty();
        }
        else {
            return;
        }

        // Get the first recipe and grind it
        OrderedRecipeMatcher.getFirstRecipe(matches, blockEntity.inventory.getHeldStacks(), match -> blockEntity.completeRecipe(match, world, pos));
    }

    public boolean completeRecipe(MillStoneRecipe recipe, World world, BlockPos pos) {
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
            // Eject results
            for (ItemStack result : recipe.getResults()) {
                ejectItem(world, result, pos);
            }
            transaction.commit();
            return true;
        }
    }

    public static void ejectItem(World world, ItemStack stack, BlockPos pos) {
        // Start at the center of the block
        Vec3d centerPos = pos.toCenterPos();
        Vec3d horizontalUnitVector = new Vec3d(1, 0, 1);

        // Pick a random direction
        double angle = Math.toRadians(world.random.nextBetween(0, 359));
        // Get distance from the center to the edge of a square, using the angle
        double distToEdge = Math.min(0.5 / Math.abs(Math.cos(angle)), 0.5 / Math.abs(Math.sin(angle)));
        // Apply that distance to get our item spawn position
        Vec3d itemPos = horizontalUnitVector
                .rotateY((float) angle)
                .multiply(distToEdge + 0.01)
                .add(centerPos);
        // Velocity is in the same X/Z direction as position, but with random strength and y offset
        Vec3d itemVelocity = horizontalUnitVector
                .rotateY((float) angle)
                .multiply(world.random.nextFloat() * 0.0125D + 0.1F)
                .add(0, world.random.nextGaussian() * 0.0125D + 0.05F, 0);

        ItemEntity itemEntity = new ItemEntity(world, itemPos.getX(), itemPos.getY(), itemPos.getZ(), stack);
        itemEntity.setVelocity(itemVelocity);
        world.spawnEntity(itemEntity);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory.readNbtList(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE), registryLookup);
        this.grindProgressTime = nbt.getInt("grindProgressTime");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.put("Inventory", this.inventory.toNbtList(registryLookup));
        nbt.putInt("grindProgressTime", this.grindProgressTime);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        //We provide *this* to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return new MillStoneScreenHandler(syncId, playerInventory, inventory, propertyDelegate);
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
            MillStoneBlockEntity.this.markDirty();
        }
    }
}
