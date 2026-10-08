package com.bwt.blocks.soul_forge;

import com.bwt.BetterWithTime;
import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.soul_forge.SoulForgeRecipe;
import com.bwt.utils.OrderedRecipeMatcher;
import com.mojang.datafixers.util.Either;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.List;
import java.util.Optional;

public class SoulForgeScreenHandler extends AbstractCraftingMenu {
    private static final int WIDTH = 4;
    private static final int HEIGHT = 4;
    private final ContainerLevelAccess access;
    private final Player player;
    private boolean placingRecipe;


    public SoulForgeScreenHandler(int i, Inventory inventory) {
        this(i, inventory, ContainerLevelAccess.NULL);
    }

    public SoulForgeScreenHandler(int id, Inventory inventory, ContainerLevelAccess access) {
        super(BetterWithTime.soulForgeScreenHandler, id, 4, 4);
        this.access = access;
        this.player = inventory.player;
        this.addSlots(inventory);
    }

    protected void addSlots(Inventory playerInventory) {
        this.addSlot(new SoulForgeCraftingResultSlot(this.player, this.craftSlots, this.resultSlots, 0, 139, 44));
        this.addCraftingGridSlots(26, 17);
        this.addStandardInventorySlots(playerInventory, 8, 102);
    }

    protected static void slotChangedCraftingGrid(
            AbstractContainerMenu abstractContainerMenu,
            ServerLevel serverLevel,
            Player player,
            CraftingContainer craftingContainer,
            ResultContainer resultContainer
    ) {
        if (serverLevel.isClientSide) {
            return;
        }
        CraftingInput craftingRecipeInput = craftingContainer.asCraftInput();
        ServerPlayer serverPlayerEntity = (ServerPlayer)player;
        Either<Optional<RecipeHolder<SoulForgeRecipe>>, Optional<RecipeHolder<CraftingRecipe>>> either = OrderedRecipeMatcher.getFirstRecipeForSoulForge(
                serverLevel,
                craftingRecipeInput
        );
        ItemStack itemStack = either.map(
                optional -> optional.filter(soulForgeRecipeHolder -> resultContainer.setRecipeUsed(serverPlayerEntity, soulForgeRecipeHolder)),
                optional -> optional.filter(craftingRecipeHolder -> resultContainer.setRecipeUsed(serverPlayerEntity, craftingRecipeHolder))
        )
                .map(recipeHolder -> recipeHolder.value().assemble(craftingRecipeInput, serverLevel.registryAccess()))
                .filter(stack -> stack.isItemEnabled(serverLevel.enabledFeatures()))
                .orElse(ItemStack.EMPTY);

        resultContainer.setItem(0, itemStack);
        abstractContainerMenu.setRemoteSlot(0, itemStack);
        serverPlayerEntity.connection.send(new ClientboundContainerSetSlotPacket(abstractContainerMenu.containerId, abstractContainerMenu.incrementStateId(), 0, itemStack));
    }


    @Override
    public void slotsChanged(Container container) {
        if (!this.placingRecipe) {
            this.access.execute((level, blockPos) -> {
                if (level instanceof ServerLevel serverLevel) {
                    slotChangedCraftingGrid(this, serverLevel, this.player, this.craftSlots, this.resultSlots);
                }
            });
        }
    }

    @Override
    public void beginPlacingRecipe() {
        this.placingRecipe = true;
    }

    @Override
    public void finishPlacingRecipe(ServerLevel serverLevel, RecipeHolder<CraftingRecipe> recipeHolder) {
        this.placingRecipe = false;
        slotChangedCraftingGrid(this, serverLevel, this.player, this.craftSlots, this.resultSlots);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, blockPos) -> this.clearContainer(player, this.craftSlots));
    }


    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, BwtBlocks.soulForgeBlock);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(itemStack, slot);
    }

    @Override
    public Slot getResultSlot() {
        return this.slots.getFirst();
    }

    @Override
    public List<Slot> getInputGridSlots() {
        return this.slots.subList(1, getGridWidth() * getGridHeight() + 1);
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    protected Player owner() {
        return this.player;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotNumber) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotNumber);
        // Vanilla decompiled code has these as magic numbers.
        // I find it easier to parse these when they're overly verbose.
        int craftingResultIndex = 0;
        int craftingGridStart = 1;
        int craftingGridEnd = HEIGHT * WIDTH; // 4x4 grid = 1-16 inclusive
        int playerInventoryStart = craftingGridEnd + 1; // 17
        int playerHotbarStart = playerInventoryStart + (9 * 3); // = 44 = 17 + 27, which the size of the non-hotbar inventory
        int playerInventoryEnd = playerHotbarStart + 9; // = 53 = 44 + 9, the size of the hotbar
        if (slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (slotNumber == craftingResultIndex) {
                this.access.execute((level, pos) -> itemStack2.getItem().onCraftedBy(itemStack2, level, player));
                if (!this.moveItemStackTo(itemStack2, playerInventoryStart, playerInventoryEnd, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemStack2, itemStack);
            } else if (slotNumber >= playerInventoryStart && slotNumber < playerInventoryEnd) {
                if (!this.moveItemStackTo(itemStack2, craftingGridStart, playerInventoryStart, false)) {
                    if (slotNumber < playerHotbarStart) {
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
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemStack2);
            if (slotNumber == craftingResultIndex) {
                player.drop(itemStack2, false);
            }
        }

        return itemStack;
    }
}
