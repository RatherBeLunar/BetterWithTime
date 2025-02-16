package com.bwt.blocks.infernal_enchanter;

import com.bwt.BetterWithTime;
import com.bwt.items.components.BwtDataComponents;
import com.bwt.tags.BwtItemTags;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;

public class InfernalEnchanterScreenHandler extends ScreenHandler {


    public final Property seed;
    private final Inventory inventory;
    private static final int SIZE = 2;
    private final PropertyDelegate propertyDelegate;
    private final ScreenHandlerContext context;

    private static final int MAX_LEVEL = 50;
    private static final double BUTTON_COUNT = 5.0;


    public InfernalEnchanterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(2), new ArrayPropertyDelegate(2), ScreenHandlerContext.EMPTY);
    }

    private int playerInventoryStartIndex, playerInventoryEndIndex;

    public InfernalEnchanterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate, ScreenHandlerContext context) {
        super(BetterWithTime.infernalEnchanterScreenHandler, syncId);
        this.context = context;
        this.propertyDelegate = propertyDelegate;
        this.addProperties(this.propertyDelegate);
        this.seed = Property.create();
        this.addProperty(this.seed).set(playerInventory.player.getEnchantmentTableSeed());

        checkSize(inventory, SIZE);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        this.addSlot(new Slot(inventory, 0, 17, 37) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isIn(BwtItemTags.CAN_INFERNAL_ENCHANT);
            }
        });
        this.addSlot(new Slot(inventory, 1, 17, 75) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.get(BwtDataComponents.ARCANE_ENCHANTMENT_COMPONENT) != null;
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

    public boolean canEnchantToolWithEnchantment(int buttonId) {

        ItemStack tool = this.slots.get(0).getStack();
        ItemStack enchantSource = this.slots.get(1).getStack();
        boolean isEnchantable = tool.isIn(BwtItemTags.CAN_INFERNAL_ENCHANT);
        if(!isEnchantable) {
            return false;
        }
        var arcaneEnchantSource = enchantSource.get(BwtDataComponents.ARCANE_ENCHANTMENT_COMPONENT);
        if (arcaneEnchantSource == null) {
            return false;
        }

        var enchantment = arcaneEnchantSource.getEnchantment();
        if (enchantment == null) {
            return false;
        }

        var resultLevel = this.getResultEnchantmentLevel(buttonId);
         if(resultLevel > enchantment.value().getMaxLevel()) {
            return false;
        }

        TagKey<Item> canApplyEnchantTo = BwtItemTags.canApplyInfernal(enchantment.getKey().orElse(null));
        if(canApplyEnchantTo == null) {
            return false;
        }

        if (!tool.isIn(canApplyEnchantTo)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int buttonId) {
        ItemStack tool = this.slots.get(0).getStack();
        ItemStack enchantSource = this.slots.get(1).getStack();
        var enchantment = this.getEnchantment();
        if(enchantment == null) {
            return false;
        }
        boolean isEnchantable = canEnchantToolWithEnchantment(buttonId);

        if (isEnchantable) {
            this.context.run((world, blockPos) -> {
                int cost = getButtonCost(buttonId);
                int playerLevels = player.experienceLevel;
                if (!player.isCreative() && playerLevels < cost) {
                    //skip if player doesn't have the levels
                    return;
                }

                int appliedTier = getResultEnchantmentLevel(buttonId);
                if(this.propertyDelegate.get(0) < appliedTier) {
                    //skip if the enchanter is not powerful enough and somehow the button packet was sent
                    return;
                }

                player.applyEnchantmentCosts(tool, cost);
                player.incrementStat(Stats.ENCHANT_ITEM);
                if (player instanceof ServerPlayerEntity) {
                    Criteria.ENCHANTED_ITEM.trigger((ServerPlayerEntity)player, tool, appliedTier);
                }
                tool.addEnchantment(enchantment, appliedTier);
                enchantSource.decrement(1);
                inventory.markDirty();
                this.onContentChanged(this.inventory);

                world.playSound(null, blockPos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
            });
            return true;
        }


        return super.onButtonClick(player, buttonId);
    }


    public int getButtonCost(int id) {
        double n = (id + 1) / BUTTON_COUNT;
        return (int) (n * MAX_LEVEL);
    }

    public int getResultEnchantmentLevel(int buttonId) {
        return buttonId + 1;
    }

    public RegistryEntry<Enchantment> getEnchantment() {

        ItemStack enchantSource = this.slots.get(1).getStack();
        var arcaneEnchantSource = enchantSource.get(BwtDataComponents.ARCANE_ENCHANTMENT_COMPONENT);
        if (arcaneEnchantSource == null) {
            return null;
        }


        return arcaneEnchantSource.getEnchantment();
    }



}
