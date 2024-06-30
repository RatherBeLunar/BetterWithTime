package com.bwt.blocks.abstract_cooking_pot;

import com.bwt.recipes.AbstractCookingPotRecipe;
import com.bwt.recipes.AbstractCookingPotRecipeType;
import com.bwt.recipes.IngredientWithCount;
import com.bwt.tags.BwtItemTags;
import com.bwt.utils.FireData;
import com.bwt.utils.FireType;
import com.bwt.utils.OrderedRecipeMatcher;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractCookingPotBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    protected static final int INVENTORY_SIZE = 27;

    // "Time" is used loosely here, since the rate of change is affected by the amount of fire surrounding the pot
    public static final int timeToCompleteCook = 150 * ( FireData.primaryFireFactor + ( FireData.secondaryFireFactor * 8 ) );
    protected int cookProgressTime;
    public int slotsOccupied;


    public final Inventory inventory = new Inventory(INVENTORY_SIZE);
    public final InventoryStorage inventoryWrapper = InventoryStorage.of(inventory, null);


    public AbstractCookingPotRecipeType unstokedRecipeType;
    public AbstractCookingPotRecipeType stokedRecipeType;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> AbstractCookingPotBlockEntity.this.cookProgressTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> AbstractCookingPotBlockEntity.this.cookProgressTime = value;
                default -> {}
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public AbstractCookingPotBlockEntity(
            BlockEntityType<? extends AbstractCookingPotBlockEntity> blockEntityType,
            AbstractCookingPotRecipeType unstokedRecipeType,
            AbstractCookingPotRecipeType stokedRecipeType,
            BlockPos pos,
            BlockState state
    ) {
        super(blockEntityType, pos, state);
        this.unstokedRecipeType = unstokedRecipeType;
        this.stokedRecipeType = stokedRecipeType;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory.readNbtList(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE), registryLookup);
        this.cookProgressTime = nbt.getInt("cookProgressTicks");
        this.slotsOccupied = nbt.getInt("slotsOccupied");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.put("Inventory", this.inventory.toNbtList(registryLookup));
        nbt.putInt("cookProgressTicks", this.cookProgressTime);
        nbt.putInt("slotsOccupied", this.slotsOccupied);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbtCompound = createNbt(registryLookup);
        nbtCompound.putInt("slotsOccupied", slotsOccupied);
        return nbtCompound;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    public static void tick(World world, BlockPos pos, BlockState state, AbstractCookingPotBlockEntity blockEntity) {
        FireData fireData = FireData.fromWorld(world, pos);

        if (fireData.fireFactor() <= 0) {
            if (blockEntity.cookProgressTime != 0) {
                blockEntity.cookProgressTime = 0;
                blockEntity.markDirty();
            }
            return;
        }

        if (fireData.fireType().equals(FireType.STOKED)) {
            int stokedExplosivesCount = blockEntity.inventory.getHeldStacks().stream()
                    .filter(itemStack -> itemStack.isIn(BwtItemTags.STOKED_EXPLOSIVES))
                    .map(ItemStack::getCount)
                    .reduce(Integer::sum)
                    .orElse(0);
            if (stokedExplosivesCount > 0) {
                explode(world, pos, stokedExplosivesCount);
                return;
            }
        }

        RecipeManager recipeManager = world.getRecipeManager();
        AbstractCookingPotRecipeType recipeTypeToGet = fireData.fireType().equals(FireType.STOKED) ? blockEntity.stokedRecipeType : blockEntity.unstokedRecipeType;
        List<RecipeEntry<AbstractCookingPotRecipe>> matches = recipeManager.getAllMatches(recipeTypeToGet, blockEntity.inventory, world);
        if (matches.isEmpty()) {
            if (blockEntity.cookProgressTime != 0) {
                blockEntity.cookProgressTime = 0;
                blockEntity.markDirty();
            }
            return;
        }

        blockEntity.cookProgressTime = blockEntity.cookProgressTime + fireData.fireFactor();
        if (blockEntity.cookProgressTime >= timeToCompleteCook) {
            blockEntity.cookProgressTime = 0;
            blockEntity.markDirty();
        }
        else {
            return;
        }

        // Cook the first recipe we can
        OrderedRecipeMatcher.getFirstRecipe(matches, blockEntity.inventory.getHeldStacks(), blockEntity::cookRecipe);
    }

    private static void explode(World world, BlockPos pos, int stokedExplosivesCount) {
        world.breakBlock(pos, true);
        float explosionStrength = Math.min(Math.max(stokedExplosivesCount / 6.4f, 2f), 10f);
        world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), explosionStrength, true, World.ExplosionSourceType.BLOCK);
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
            AbstractCookingPotBlockEntity.this.markDirty();
        }
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

    // Pick up items from above like a hopper
    public static void onEntityCollided(Entity entity, AbstractCookingPotBlockEntity blockEntity) {
        ItemStack itemStack;
        if (entity instanceof ItemEntity itemEntity && !(itemStack = itemEntity.getStack()).isEmpty()) {
            int count = itemStack.getCount();
            try (Transaction transaction = Transaction.openOuter()) {
                long inserted = StorageUtil.insertStacking(blockEntity.inventoryWrapper.getSlots(), ItemVariant.of(itemStack), count, transaction);
                itemEntity.setStack(itemEntity.getStack().copyWithCount((int) (count - inserted)));
                transaction.commit();
                blockEntity.inventory.markDirty();
            }
        }
    }

    // Update fill level texture
    @Override
    public void markDirty() {
        slotsOccupied = ((int) inventory.heldStacks.stream().filter(stack -> !stack.isEmpty()).count());
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
        super.markDirty();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
