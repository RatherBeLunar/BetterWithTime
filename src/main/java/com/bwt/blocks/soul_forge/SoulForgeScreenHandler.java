package com.bwt.blocks.soul_forge;

import com.bwt.BetterWithTime;
import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.BwtRecipes;
import com.bwt.utils.OrderedRecipeMatcher;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SoulForgeScreenHandler extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
    private static final int WIDTH = 4;
    private static final int HEIGHT = 4;
    private final CraftingContainer input = new TransientCraftingContainer(this, WIDTH, HEIGHT);
    private final ResultContainer result = new ResultContainer();
    private final ContainerLevelAccess context;
    private final Player player;
    private boolean filling;

    public SoulForgeScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public SoulForgeScreenHandler(int id, Inventory playerInventory, ContainerLevelAccess context) {
        super(BetterWithTime.soulForgeScreenHandler, id);
        this.context = context;
        this.player = playerInventory.player;
        this.addSlots(playerInventory);
    }

    protected void addSlots(Inventory playerInventory) {
        this.addSlot(new SoulForgeCraftingResultSlot(this.player, this.input, this.result, 0, 139, 44));

        for(int y = 0; y < HEIGHT; ++y) {
            for(int x = 0; x < WIDTH; ++x) {
                this.addSlot(new Slot(input, x + y * WIDTH, 26 + x * 18, 17 + y * 18));
            }
        }

        for(int y = 0; y < 3; ++y) {
            for(int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 102 + y * 18));
            }
        }

        for(int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 160));
        }
    }

    protected static void updateResult(
            AbstractContainerMenu handler,
            Level level,
            Player player,
            CraftingContainer craftingInventory,
            ResultContainer resultInventory,
            @Nullable RecipeHolder<CraftingRecipe> recipe
    ) {
        if (level.isClientSide || level.getServer() == null) {
            return;
        }
        CraftingInput craftingRecipeInput = craftingInventory.asCraftInput();
        ServerPlayer serverPlayerEntity = (ServerPlayer)player;
        ItemStack itemStack = ItemStack.EMPTY;
        Optional<? extends RecipeHolder<? extends CraftingRecipe>> optional = OrderedRecipeMatcher.getFirstRecipeOfMultipleTypes(
                level,
                craftingRecipeInput,
                List.of(BwtRecipes.SOUL_FORGE_RECIPE_TYPE, RecipeType.CRAFTING)
        );
        if (optional.isPresent()) {
            RecipeHolder<? extends CraftingRecipe> recipeEntry = optional.get();
            CraftingRecipe craftingRecipe = recipeEntry.value();
            if (resultInventory.setRecipeUsed(level, serverPlayerEntity, recipeEntry)) {
                ItemStack itemStack2 = craftingRecipe.assemble(craftingRecipeInput, level.registryAccess());
                if (itemStack2.isItemEnabled(level.enabledFeatures())) {
                    itemStack = itemStack2;
                }
            }
        }

        resultInventory.setItem(0, itemStack);
        handler.setRemoteSlot(0, itemStack);
        serverPlayerEntity.connection.send(new ClientboundContainerSetSlotPacket(handler.containerId, handler.incrementStateId(), 0, itemStack));
    }

    @Override
    public void slotsChanged(Container inventory) {
        if (!this.filling) {
            this.context.execute((level, pos) -> updateResult(this, level, this.player, this.input, this.result, null));
        }
    }

    @Override
    public void beginPlacingRecipe() {
        this.filling = true;
    }

    @Override
    public void finishPlacingRecipe(RecipeHolder<CraftingRecipe> recipe) {
        this.filling = false;
        this.context.execute((level, pos) -> updateResult(this, level, this.player, this.input, this.result, recipe));
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents finder) {
        this.input.fillStackedContents(finder);
    }

    @Override
    public void clearCraftingContent() {
        this.input.clearContent();
        this.result.clearContent();
    }

    @Override
    public boolean recipeMatches(RecipeHolder<CraftingRecipe> recipe) {
        return recipe.value().matches(this.input.asCraftInput(), this.player.level());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.context.execute((level, pos) -> this.clearContainer(player, this.input));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.context, player, BwtBlocks.soulForgeBlock);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        // Vanilla decompiled code has these as magic numbers.
        // I find it easier to parse these when they're overly verbose.
        int craftingResultIndex = 0;
        int craftingGridStart = 1;
        int craftingGridEnd = HEIGHT * WIDTH; // 4x4 grid = 1-16 inclusive
        int playerInventoryStart = craftingGridEnd + 1; // 17
        int playerHotbarStart = playerInventoryStart + (9 * 3); // = 44 = 17 + 27, which the size of the non-hotbar inventory
        int playerInventoryEnd = playerHotbarStart + 9; // = 53 = 44 + 9, the size of the hotbar
        if (slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            itemStack = itemStack2.copy();
            if (slot == craftingResultIndex) {
                this.context.execute((level, pos) -> itemStack2.getItem().onCraftedBy(itemStack2, level, player));
                if (!this.moveItemStackTo(itemStack2, playerInventoryStart, playerInventoryEnd, true)) {
                    return ItemStack.EMPTY;
                }

                slot2.onQuickCraft(itemStack2, itemStack);
            } else if (slot >= playerInventoryStart && slot < playerInventoryEnd) {
                if (!this.moveItemStackTo(itemStack2, craftingGridStart, playerInventoryStart, false)) {
                    if (slot < playerHotbarStart) {
                        if (!this.moveItemStackTo(itemStack2, playerHotbarStart, playerInventoryEnd, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemStack2, playerInventoryStart, playerHotbarStart, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemStack2, playerInventoryStart, playerInventoryEnd, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setByPlayer(ItemStack.EMPTY);
            } else {
                slot2.setChanged();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTake(player, itemStack2);
            if (slot == craftingResultIndex) {
                player.drop(itemStack2, false);
            }
        }

        return itemStack;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.result && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getGridWidth() {
        return this.input.getWidth();
    }

    @Override
    public int getGridHeight() {
        return this.input.getHeight();
    }

    @Override
    public int getSize() {
        return 10;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public boolean shouldMoveToInventory(int index) {
        return index != this.getResultSlotIndex();
    }
}
