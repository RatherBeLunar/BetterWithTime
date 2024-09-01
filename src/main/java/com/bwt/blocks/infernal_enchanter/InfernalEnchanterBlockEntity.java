package com.bwt.blocks.infernal_enchanter;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.items.components.BwtDataComponents;
import com.bwt.items.components.InfernalEnchanterDecorationComponent;
import com.bwt.tags.BwtBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class InfernalEnchanterBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {

    private Inventory inventory;
    private int enchantmentPowerLevel, enchantmentPowerSourceCount;
    private static final double MAX_POWER_SOURCE_REQUIRED = 60;
    private static final int ENCHANTER_LEVELS = 5;
    private InfernalEnchanterDecorationComponent decorationComponent;

    public InfernalEnchanterBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.infernalEnchanterBlockEntity, pos, state);
        this.inventory = new Inventory(2);
        this.animationsController = new InfernalEnchanterAnimationsController();
    }

    public static final List<BlockPos> POWER_PROVIDER_OFFSETS = BlockPos.stream(-8, -8, -8, 8, 8, 8).map(BlockPos::toImmutable).toList();
    public static final List<BlockPos> EXTERNAL_CANDLE_OFFSETS = BlockPos.stream(-8, -8, -8, 8, 8, 8).map(BlockPos::toImmutable).toList();

    private final InfernalEnchanterAnimationsController animationsController;



    public void tick(@NotNull World world, BlockPos blockPos, BlockState blockState) {

        this.animationsController.tick(world, blockPos, blockState);
        if (world.getTime() % 10 == 0) {
            this.calculateEnchantmentPowerLevel();
            if(blockState.get(InfernalEnchanterBlock.LIT)) {
                InfernalEnchanterBlock.spawnCandleParticles(world.getBlockState(getPos()), world, getPos(), world.getRandom());
            }
        }
    }


    public static double getChiseledBookshelfPowerLevel(BlockState state) {
        double occupied = ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.stream().filter(state::get).mapToDouble(property -> 1).sum();
        return occupied / ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.size();
    }

    public boolean isPowerProvider(BlockPos pos) {
        return world.getBlockState(getPos().add(pos)).isIn(BwtBlockTags.INFERNAL_ENCHANTMENT_POWER_PROVIDER);
    }

    public double getPowerSourceValue(BlockPos offset) {
        BlockPos p = this.getPos().add(offset);
        BlockState state = world.getBlockState(p);
        if (state.getBlock() instanceof ChiseledBookshelfBlock) {
            return getChiseledBookshelfPowerLevel(state);
        }
        return 1;
    }

    public boolean canRayTraceToPowerProvider(BlockPos offset) {
        BlockPos p = this.getPos().add(offset);
        BlockState state = world.getBlockState(p);

        var start = getPos().toCenterPos();
        var end = p.toCenterPos();

        var result = world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, ShapeContext.absent()));

        if (result == null || result.getType() == HitResult.Type.MISS) {
            return false;
        }

        var resultPos = result.getBlockPos();
        if (!p.equals(resultPos)) {
            return false;
        }

        return true;
    }

    public void spawnLetterParticles() {
        Iterator iter = POWER_PROVIDER_OFFSETS.iterator();
        var random = world.getRandom();
        while (iter.hasNext()) {
            BlockPos offset = (BlockPos) iter.next();
            if (random.nextInt(64) == 0 && (isPowerProvider(getPos().add(offset)) && canRayTraceToPowerProvider(offset))) {
                world.addParticle(ParticleTypes.ENCHANT, (double) pos.getX() + 0.5, (double) pos.getY() + 2.0, (double) pos.getZ() + 0.5, (double) ((float) offset.getX() + random.nextFloat()) - 0.5, (double) ((float) offset.getY() - random.nextFloat() - 1.0F), (double) ((float) offset.getZ() + random.nextFloat()) - 0.5);
            }
        }
    }


    public void calculateEnchantmentPowerLevel() {
        var found = POWER_PROVIDER_OFFSETS.stream().filter(this::isPowerProvider).toList();

        double sum = found.stream().mapToDouble(this::getPowerSourceValue).sum();

        var cappedSum = Math.min(sum, MAX_POWER_SOURCE_REQUIRED);
        int powerLevel = (int) Math.floor(cappedSum / (MAX_POWER_SOURCE_REQUIRED / ENCHANTER_LEVELS));
        int finalPowerLevel = Math.min(powerLevel, ENCHANTER_LEVELS);

        this.enchantmentPowerLevel = finalPowerLevel;
        this.enchantmentPowerSourceCount = (int) Math.floor(sum);
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
        this.enchantmentPowerSourceCount = nbt.getInt("enchantmentPowerSourceCount");
        this.decorationComponent = InfernalEnchanterDecorationComponent.fromNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.put("Inventory", this.inventory.toNbtList(registryLookup));
        nbt.putInt("enchantmentPowerLevel", this.enchantmentPowerLevel);
        nbt.putInt("enchantmentPowerSourceCount", this.enchantmentPowerSourceCount);
        this.decorationComponent.toNbt(nbt);
    }

    @Override
    public void clear() {

    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createComponentlessNbt(registryLookup);
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        componentMapBuilder.add(BwtDataComponents.INFERNAL_ENCHANTER_DECORATION_COMPONENT, this.decorationComponent);
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.decorationComponent = components.getOrDefault(BwtDataComponents.INFERNAL_ENCHANTER_DECORATION_COMPONENT, InfernalEnchanterDecorationComponent.DEFAULT);
    }

    public InfernalEnchanterDecorationComponent getDecorationComponent() {
        return decorationComponent;
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
                case 1 -> InfernalEnchanterBlockEntity.this.enchantmentPowerSourceCount;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> InfernalEnchanterBlockEntity.this.enchantmentPowerLevel = value;
                case 1 -> InfernalEnchanterBlockEntity.this.enchantmentPowerSourceCount = value;
                default -> {
                }
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };
}
