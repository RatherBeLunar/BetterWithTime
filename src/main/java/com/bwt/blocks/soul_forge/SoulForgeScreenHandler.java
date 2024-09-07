package com.bwt.blocks.soul_forge;

import com.bwt.BetterWithTime;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class SoulForgeScreenHandler extends AbstractRecipeScreenHandler<CraftingRecipeInput, CraftingRecipe> {
    private final Inventory inventory;
    private final PlayerEntity player;

    public SoulForgeScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(SoulForgeBlockEntity.INVENTORY_SIZE));
    }

    public SoulForgeScreenHandler(int id, PlayerInventory playerInventory, Inventory inventory) {
        super(BetterWithTime.soulForgeScreenHandler, id);
        this.inventory = inventory;
        this.player = playerInventory.player;

        this.addSlot(new OutputSlot(this.inventory, this.player, 0, 139, 44));

        for(int y = 0; y < SoulForgeBlockEntity.HEIGHT; ++y) {
            for(int x = 0; x < SoulForgeBlockEntity.HEIGHT; ++x) {
                this.addSlot(new Slot(inventory, x + y * SoulForgeBlockEntity.WIDTH + 1, 26 + x * 18, 17 + y * 18));
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

    @Override
    public void populateRecipeFinder(RecipeMatcher finder) {
        if (inventory instanceof SoulForgeBlockEntity soulForgeBlockEntity) {
            soulForgeBlockEntity.provideRecipeInputs(finder);
        }
    }

    @Override
    public void clearCraftingSlots() {
        if (inventory instanceof SoulForgeBlockEntity soulForgeBlockEntity) {
            soulForgeBlockEntity.clear();
        }
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return canInsertIntoSlot(slot.id) && super.canInsertIntoSlot(stack, slot);
    }

    @Override
    public boolean matches(RecipeEntry<CraftingRecipe> recipe) {
        if (inventory instanceof SoulForgeBlockEntity soulForgeBlockEntity) {
            return soulForgeBlockEntity.matches(recipe.value());
        }
        return false;
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 0;
    }

    @Override
    public int getCraftingWidth() {
        return SoulForgeBlockEntity.WIDTH;
    }

    @Override
    public int getCraftingHeight() {
        return SoulForgeBlockEntity.HEIGHT;
    }

    @Override
    public int getCraftingSlotCount() {
        return SoulForgeBlockEntity.GRID_SIZE + 1;
    }

    @Override
    public RecipeBookCategory getCategory() {
        return RecipeBookCategory.CRAFTING;
    }

    @Override
    public boolean canInsertIntoSlot(int index) {
        return index != this.getCraftingResultSlotIndex();
    }

    @Override
    public void onContentChanged(Inventory inv) {
        if (this.player instanceof ServerPlayerEntity) {
            ServerPlayNetworkHandler netHandler = ((ServerPlayerEntity) this.player).networkHandler;
            netHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(this.syncId, 0, 0, inventory.getStack(0)));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack remainderResultStack = ItemStack.EMPTY;
        if (!(inventory instanceof SoulForgeBlockEntity soulForgeBlockEntity)) {
            return remainderResultStack;
        }

        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack original = slot.getStack();
            ItemStack resultStack = original.copy();
            remainderResultStack = resultStack.copy();
            if (index == 0) {
                if (!this.insertItem(resultStack, SoulForgeBlockEntity.INVENTORY_SIZE, SoulForgeBlockEntity.INVENTORY_SIZE + 36, true)) return ItemStack.EMPTY;
                soulForgeBlockEntity.removeStack(index, original.getCount() - resultStack.getCount());
                // this sets recipe in container
                if(
                        player instanceof ServerPlayerEntity
                        && soulForgeBlockEntity.getLastRecipe() != null
                        && !soulForgeBlockEntity.shouldCraftRecipe(player.getWorld(), (ServerPlayerEntity) player, soulForgeBlockEntity.getLastRecipe())
                ) {
                    return ItemStack.EMPTY;
                }
                slots.get(0).onQuickTransfer(resultStack, original); // calls onCrafted if different
            } else if (index >= SoulForgeBlockEntity.INVENTORY_SIZE && index < SoulForgeBlockEntity.INVENTORY_SIZE + 36) {
                if (!this.insertItem(resultStack, 1, SoulForgeBlockEntity.INVENTORY_SIZE, false)) {
                    if (index < SoulForgeBlockEntity.INVENTORY_SIZE + 27) {
                        if (!this.insertItem(resultStack, SoulForgeBlockEntity.INVENTORY_SIZE + 27, SoulForgeBlockEntity.INVENTORY_SIZE + 36, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.insertItem(resultStack, SoulForgeBlockEntity.INVENTORY_SIZE, SoulForgeBlockEntity.INVENTORY_SIZE + 27, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.insertItem(resultStack, SoulForgeBlockEntity.INVENTORY_SIZE, SoulForgeBlockEntity.INVENTORY_SIZE + 36, false)) {
                return ItemStack.EMPTY;
            }

            if (resultStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (resultStack.getCount() == remainderResultStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, resultStack);
        }
        return remainderResultStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        ItemStack cursorStack = this.player.currentScreenHandler.getCursorStack();
        if (!cursorStack.isEmpty()) {
            player.dropItem(cursorStack, false);
            this.player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
        }
        if (inventory instanceof SoulForgeBlockEntity soulForgeBlockEntity) {
            soulForgeBlockEntity.onContainerClose(this);
        }
    }

    private class OutputSlot extends Slot {
        private final PlayerEntity player;
        OutputSlot(Inventory inv, PlayerEntity player, int index, int x, int y) {
            super(inv, index, x, y);
            this.player = player;
        }

        @Override
        public boolean canInsert(ItemStack itemStack_1) {
            return false;
        }

        @Override
        protected void onTake(int amount) {
            SoulForgeScreenHandler.this.inventory.removeStack(0, amount);
        }

        @Override
        protected void onCrafted(ItemStack stack, int amount) {
            super.onCrafted(stack); // from CraftingResultsSlot onCrafted
            if (amount > 0) stack.onCraftByPlayer(this.player.getWorld(), this.player, amount);
            if (this.inventory instanceof RecipeUnlocker) ((RecipeUnlocker)this.inventory).unlockLastRecipe(this.player, getStacks());
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            onCrafted(stack, stack.getCount());
            super.onTakeItem(player, stack);
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }
}
