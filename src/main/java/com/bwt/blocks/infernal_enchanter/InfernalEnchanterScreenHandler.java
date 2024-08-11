package com.bwt.blocks.infernal_enchanter;

import com.bwt.BetterWithTime;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class InfernalEnchanterScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private static final int SIZE = 2;
    private final PropertyDelegate propertyDelegate;
    private final ScreenHandlerContext context;

    public InfernalEnchanterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(2), new ArrayPropertyDelegate(1), ScreenHandlerContext.EMPTY);
    }

    private int playerInventoryStartIndex, playerInventoryEndIndex;

    public InfernalEnchanterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate, ScreenHandlerContext context) {
        super(BetterWithTime.infernalEnchanterScreenHandler, syncId);
        this.context = context;
        this.propertyDelegate = propertyDelegate;
        this.addProperties(this.propertyDelegate);

        checkSize(inventory, SIZE);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        // Mill Stone inventory
        this.addSlot(new Slot(inventory, 0, 17, 37) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isEnchantable();
            }
        });
        this.addSlot(new Slot(inventory, 1, 17, 75) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.ENCHANTED_BOOK); // TODO
            }
        });

        // Player inventory
        int p = 2;
        playerInventoryStartIndex = p;
        for (int m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 129 + m * 18));
                p++;
            }

        }
        // Player hotbar
        for (int m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 187));
            p++;
        }
        playerInventoryEndIndex = p;
    }


    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot == 0) {
                if (!this.insertItem(itemStack2, playerInventoryStartIndex, playerInventoryEndIndex, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot == 1) {
                if (!this.insertItem(itemStack2, playerInventoryStartIndex, playerInventoryEndIndex, true)) {
                    return ItemStack.EMPTY;
                }
            } else {

                if (this.slots.get(0).canInsert(itemStack2)) {
                    if (this.slots.get(0).hasStack()) {
                        return ItemStack.EMPTY;
                    }
                    ItemStack itemStack3 = itemStack2.copyWithCount(1);
                    itemStack2.decrement(1);
                    this.slots.get(0).setStack(itemStack3);
                }

                if (this.slots.get(1).canInsert(itemStack2)) {
                    if (this.slots.get(1).hasStack()) {
                        return ItemStack.EMPTY;
                    }
                    ItemStack itemStack3 = itemStack2.copyWithCount(1);
                    itemStack2.decrement(1);
                    this.slots.get(1).setStack(itemStack3);
                }


                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }


    public PropertyDelegate getPropertyDelegate() {
        return this.propertyDelegate;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        player.sendMessage(Text.literal(String.format("%d", id)));



        ItemStack tool = this.slots.get(0).getStack();
        ItemStack enchantSource = this.slots.get(1).getStack();
        if(tool.isEnchantable()) {
            this.context.run((world, blockPos) -> {
                tool.addEnchantment(Enchantments.EFFICIENCY, 25);
            });
            return true;
        }



        return super.onButtonClick(player, id);
    }
}
