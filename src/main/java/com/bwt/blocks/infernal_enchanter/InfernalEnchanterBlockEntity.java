package com.bwt.blocks.infernal_enchanter;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.tags.BwtBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InfernalEnchanterBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {

    private Inventory inventory;
    private int enchantmentPowerLevel;
    private static final double MAX_POWER_SOURCE_REQUIRED = 60;
    private static final int ENCHANTER_LEVELS = 5;

    public InfernalEnchanterBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.infernalEnchanterBlockEntity, pos, state);
        this.inventory = new Inventory(2);
        this.animationsController = new InfernalEnchanterAnimationsController();
    }

    public static final List<BlockPos> CANDLE_OFFSETS = BlockPos.stream(-8, -8, -8, 8, 8, 8).map(BlockPos::toImmutable).toList();
    public static final List<BlockPos> POWER_SOURCE_OFFSETS = BlockPos.stream(-8, -8, -8, 8,8,8).map(BlockPos::toImmutable).toList();

    private final InfernalEnchanterAnimationsController animationsController;


    public void tick(@NotNull World world, BlockPos blockPos, BlockState blockState) {

        this.animationsController.tick(world, blockPos, blockState);
        if (world.getTime() % 5 == 0) {
            this.calculateEnchantmentPowerLevel(world, blockPos);
        }
    }

    public static double getChiseledBookshelfPowerLevel(BlockState state) {
        double occupied = ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.stream().filter(state::get).mapToDouble(property -> 1).sum();
        return occupied / ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.size();
    }

    public void calculateEnchantmentPowerLevel(World world, BlockPos blockPos) {

        double sumSources = POWER_SOURCE_OFFSETS.stream().map(blockPos::add).map(world::getBlockState).filter(blockState -> blockState.isIn(BwtBlockTags.INFERNAL_ENCHANTMENT_POWER_PROVIDER)).mapToDouble(
                blockState -> {
                    if(blockState.getBlock() instanceof ChiseledBookshelfBlock) {
                        return (int) (getChiseledBookshelfPowerLevel(blockState));
                    } else {
                        return 1;
                    }
                }
        ).sum();


        var cappedSumSources = Math.min(sumSources, MAX_POWER_SOURCE_REQUIRED);
        int powerLevel = (int) Math.floor(cappedSumSources / (MAX_POWER_SOURCE_REQUIRED / ENCHANTER_LEVELS));
        int finalPowerLevel = Math.min(powerLevel, ENCHANTER_LEVELS);

        this.enchantmentPowerLevel = finalPowerLevel;
        this.markDirty();


    }

    public static <E extends InfernalEnchanterBlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, E e) {
        e.tick(world, blockPos, blockState);
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
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        //We provide *this* to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return new InfernalEnchanterScreenHandler(syncId, playerInventory, inventory, propertyDelegate, ScreenHandlerContext.create(world, pos));
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory.readNbtList(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE), registryLookup);
        this.enchantmentPowerLevel = nbt.getInt("enchantmentPowerLevel");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.put("Inventory", this.inventory.toNbtList(registryLookup));
        nbt.putInt("enchantmentPowerLevel", this.enchantmentPowerLevel);
    }

    @Override
    public void clear() {

    }


    public class Inventory extends SimpleInventory {
        public Inventory(int size) {
            super(size);
        }

        @Override
        public void markDirty() {
            InfernalEnchanterBlockEntity.this.markDirty();
        }
    }

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> InfernalEnchanterBlockEntity.this.enchantmentPowerLevel;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> InfernalEnchanterBlockEntity.this.enchantmentPowerLevel = value;
                default -> {
                }
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };
}
