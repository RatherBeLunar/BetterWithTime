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
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class MillStoneBlockEntity extends BlockEntity implements MenuProvider, Container {
    protected int grindProgressTime;
    public static final int timeToGrind = 200;
    protected static final int INVENTORY_SIZE = 3;

    public final MillStoneBlockEntity.Inventory inventory = new com.bwt.blocks.mill_stone.MillStoneBlockEntity.Inventory(INVENTORY_SIZE);
    public final InventoryStorage inventoryWrapper = InventoryStorage.of(inventory, null);

    protected final ContainerData propertyDelegate = new ContainerData() {
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
        public int getCount() {
            return 1;
        }
    };

    public MillStoneBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.millStoneBlockEntity, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MillStoneBlockEntity blockEntity) {
        if (!state.is(BwtBlocks.millStoneBlock) || !state.getValue(MillStoneBlock.MECH_POWERED)) {
            return;
        }
        MillStoneRecipeInput recipeInput = new MillStoneRecipeInput(blockEntity.inventory.getItems());
        List<RecipeHolder<MillStoneRecipe>> matches = level.getRecipeManager().getRecipesFor(BwtRecipes.MILL_STONE_RECIPE_TYPE, recipeInput, level);
        if (matches.isEmpty()) {
            if (blockEntity.grindProgressTime != 0) {
                blockEntity.grindProgressTime = 0;
                blockEntity.setChanged();
            }
            return;
        }

        blockEntity.grindProgressTime += 1;
        if (blockEntity.grindProgressTime >= timeToGrind) {
            blockEntity.grindProgressTime = 0;
            blockEntity.setChanged();
        }
        else {
            return;
        }

        // Get the first recipe and grind it
        OrderedRecipeMatcher.getFirstRecipe(matches, blockEntity.inventory.getItems(), match -> blockEntity.completeRecipe(match, level, pos));
    }

    public boolean completeRecipe(MillStoneRecipe recipe, Level level, BlockPos pos) {
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
                ejectItem(level, result, pos);
            }
            transaction.commit();
            return true;
        }
    }

    public static void ejectItem(Level level, ItemStack stack, BlockPos pos) {
        // Start at the center of the block
        Vec3 centerPos = pos.getCenter();
        Vec3 horizontalUnitVector = new Vec3(1, 0, 1);

        // Pick a random direction
        double angle = Math.toRadians(level.random.nextIntBetweenInclusive(0, 359));
        // Get distance from the center to the edge of a square, using the angle
        double distToEdge = Math.min(0.5 / Math.abs(Math.cos(angle)), 0.5 / Math.abs(Math.sin(angle)));
        // Apply that distance to get our item spawn position
        Vec3 itemPos = horizontalUnitVector
                .yRot((float) angle)
                .scale(distToEdge + 0.01)
                .add(centerPos);
        // Velocity is in the same X/Z direction as position, but with random strength and y offset
        Vec3 itemVelocity = horizontalUnitVector
                .yRot((float) angle)
                .scale(level.random.nextFloat() * 0.0125D + 0.1F)
                .add(0, level.random.nextGaussian() * 0.0125D + 0.05F, 0);

        ItemEntity itemEntity = new ItemEntity(level, itemPos.x(), itemPos.y(), itemPos.z(), stack);
        itemEntity.setDeltaMovement(itemVelocity);
        level.addFreshEntity(itemEntity);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.inventory.fromTag(nbt.getList("Inventory", Tag.TAG_COMPOUND), registryLookup);
        this.grindProgressTime = nbt.getInt("grindProgressTime");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.put("Inventory", this.inventory.createTag(registryLookup));
        nbt.putInt("grindProgressTime", this.grindProgressTime);
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, net.minecraft.world.entity.player.Inventory playerInventory, Player player) {
        //We provide *this* to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return new MillStoneScreenHandler(syncId, playerInventory, inventory, propertyDelegate);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
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
            MillStoneBlockEntity.this.setChanged();
        }
    }
}
