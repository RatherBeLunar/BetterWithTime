package com.bwt.blocks.pulley;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.AnchorBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.RopeBlock;
import com.bwt.entities.MovingRopeEntity;
import com.bwt.items.BwtItems;
import com.bwt.utils.BlockPosAndState;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Predicate;

public class PulleyBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    protected static final int INVENTORY_SIZE = 4;

    public final PulleyBlockEntity.Inventory inventory = new com.bwt.blocks.pulley.PulleyBlockEntity.Inventory(INVENTORY_SIZE);
    public final InventoryStorage inventoryWrapper = InventoryStorage.of(inventory, null);
    protected int mechPower;

    private MovingRopeEntity rope;
    private UUID ropeId;


    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> PulleyBlockEntity.this.mechPower;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> PulleyBlockEntity.this.mechPower = value > 0 ? 1 : 0;
                default -> {}
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public PulleyBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.pulleyBlockEntity, pos, state);
    }

    public static boolean isMechPowered(BlockState state) {
        return state.get(PulleyBlock.MECH_POWERED);
    }

    public static boolean isRedstonePowered(BlockState state) {
        return state.get(PulleyBlock.POWERED);
    }

    public boolean isRaising(BlockState state) {
        return isMechPowered(state) && !isRedstonePowered(state);
    }

    public boolean isLowering(BlockState state) {
        return !isMechPowered(state) && !isRedstonePowered(state)
                && inventory.heldStacks.stream()
                    .anyMatch(itemStack -> itemStack.isOf(BwtItems.ropeItem) && itemStack.getCount() > 0);
    }

    public static void tick(World world, BlockPos pos, BlockState state, PulleyBlockEntity blockEntity) {
        if (world.isClient) {
            return;
        }
        blockEntity.tryNextOperation(world, pos, state);
    }

    private void tryNextOperation(World world, BlockPos pos, BlockState state) {
        if (doesRopeEntityExist(world)) {
            return;
        }

        if (canGoDown(world, pos, state, false)) {
            goDown(world, pos);
            return;
        }
        if (canGoUp(world, pos, state)) {
            goUp(world, pos);
        }
    }

    private boolean doesRopeEntityExist(World world) {
        // Rope is fully present and loaded
        if (rope != null && !rope.isRemoved()) {
            return true;
        }
        // Rope needs to be loaded from nbt but hasn't gotten to yet
        if (ropeId == null) {
            return false;
        }
        if (!(world instanceof ServerWorld serverWorld)) {
            return true;
        }
        // Try to load the rope
        Entity entity = serverWorld.getEntity(ropeId);
        // Rope hasn't gotten to load yet
        if (entity == null) {
            return true;
        }
        if (entity instanceof MovingRopeEntity movingRopeEntity) {
            this.rope = movingRopeEntity;
            ropeId = null;
            return true;
        }
        return false;
    }

    private boolean validRopeConnector(BlockState state) {
        return (state.isOf(BwtBlocks.anchorBlock) && state.get(AnchorBlock.FACING) == Direction.UP) || state.isOf(BwtBlocks.ropeBlock);
    }

    private boolean canGoUp(World world, BlockPos pos, BlockState state) {
        if (!isRaising(state)) {
            return false;
        }
        if (!putRope(true)) {
            return false;
        }
        BlockPos lowest = RopeBlock.getBottomRopePos(world, pos);
        return !lowest.equals(pos);
    }

    private boolean canGoDown(World world, BlockPos pos, BlockState state, boolean isMoving) {
        if (!isLowering(state)) {
            return false;
        }
        if (!hasRope()) {
            return false;
        }
        BlockPos newPos = RopeBlock.getBottomRopePos(world, pos).down();
        BlockState newState = world.getBlockState(newPos);
        boolean flag = !isMoving && validRopeConnector(newState);
        return newPos.getY() > world.getBottomY() && (newState.isReplaceable() || flag) && newPos.up().getY() > world.getBottomY();
    }

    private void goUp(World world, BlockPos pos) {
        BlockPos lowest = RopeBlock.getBottomRopePos(world, pos);
        BlockState belowState = world.getBlockState(lowest.down());
        rope = new MovingRopeEntity(world, pos, lowest, lowest.up().getY());
        ropeId = null;
        if (validRopeConnector(belowState) && !movePlatform(world, lowest.down(), true)) {
            rope = null;
            return;
        }
        world.playSound(null, pos.down(), BwtBlocks.ropeBlock.getDefaultState().getSoundGroup().getBreakSound(), SoundCategory.BLOCKS,
                0.4F + (world.random.nextFloat() * 0.1F), 1.0F);
        world.spawnEntity(rope);
        world.removeBlock(lowest, false);
        putRope(false);
    }

    private void goDown(World world, BlockPos pos) {
        BlockPos newPos = RopeBlock.getBottomRopePos(world, pos).down();
        BlockState bottomState = world.getBlockState(newPos);
        rope = new MovingRopeEntity(world, pos, newPos.up(), newPos.getY());
        ropeId = null;
        if (validRopeConnector(bottomState) && !movePlatform(world, newPos, false)) {
            rope = null;
            return;
        }
        world.spawnEntity(rope);
    }

    private boolean movePlatform(World world, BlockPos anchor, boolean up) {
        BlockState state = world.getBlockState(anchor);
        if (!(validRopeConnector(state))) {
            return false;
        }

        HashSet<BlockPos> platformBlocks = new HashSet<>();
        platformBlocks.add(anchor);
        BlockPos below = anchor.down();
        BlockState belowState = world.getBlockState(below);
        if (isPlatform(belowState)) {
            if (!addToList(world, below, below, platformBlocks, up)) {
                return false;
            }
        }
        else if (!(up || isIgnoreable(belowState))) {
            return false;
        }


        if (!world.isClient) {
            for (BlockPos blockPos : platformBlocks) {
                Vec3i offset = blockPos.subtract(anchor.up());
                rope.addBlock(offset, world, blockPos, world.getBlockState(blockPos));
                BlockState upState = world.getBlockState(blockPos.up());
                if (isMoveableBlock(upState)) {
                    if (upState.getBlock() instanceof AbstractRailBlock) {
                        upState = flattenRail(upState);
                    }
                    if (upState.getBlock() instanceof RedstoneWireBlock) {
                        upState = upState.with(RedstoneWireBlock.POWER, 0);
                    }
                    rope.addBlock(new Vec3i(offset.getX(), offset.getY() + 1, offset.getZ()), world, blockPos.up(), upState);
                }
            }
            rope.getBlockMap().entrySet().stream()
                    .sorted((a, b) -> b.getKey().getY() - a.getKey().getY())
                    .forEach(entry -> {
                        BlockPos blockPos = anchor.up().add(entry.getKey());
                        world.removeBlock(blockPos, false);
                    });
        }

        return true;
    }

    public boolean isIgnoreable(BlockState state) {
        return state.isReplaceable();
    }

    public boolean isMoveableBlock(BlockState state) {
        return state.isOf(Blocks.REDSTONE_WIRE) || state.getBlock() instanceof AbstractRailBlock;
    }

    public boolean isPlatform(BlockState state) {
        return state.isOf(BwtBlocks.platformBlock);
    }

    private BlockState flattenRail(BlockState state) {
        EnumProperty<RailShape> property = state.contains(Properties.RAIL_SHAPE) ? Properties.RAIL_SHAPE : state.contains(Properties.STRAIGHT_RAIL_SHAPE) ? Properties.STRAIGHT_RAIL_SHAPE : null;
        RailShape currentShape = state.get(property);
        return state.with(property, switch (currentShape) {
            case ASCENDING_EAST, ASCENDING_WEST -> RailShape.EAST_WEST;
            case ASCENDING_NORTH, ASCENDING_SOUTH -> RailShape.NORTH_SOUTH;
            default -> currentShape;
        });
    }

    private boolean addToList(World world, BlockPos pos, BlockPos sourcePos, HashSet<BlockPos> set, boolean up) {
        Vec3i distance = pos.subtract(sourcePos);
        if (Math.abs(distance.getX()) > 2 || Math.abs(distance.getZ()) > 2 || Math.abs(distance.getY()) > 5) {
            return false;
        }
        if (!isPlatform(world.getBlockState(pos))) {
            return true;
        }

        BlockPos blockCheck = up ? pos.up() : pos.down();
        BlockState otherState = world.getBlockState(blockCheck);
        if (!(isIgnoreable(otherState) || isMoveableBlock(otherState) || isPlatform(otherState)) && !set.contains(blockCheck))
            return false;

        set.add(pos);

        return Arrays.stream(Direction.values())
                .map(pos::offset)
                .filter(Predicate.not(set::contains))
                .map(offsetPos -> addToList(world, offsetPos, sourcePos, set, up))
                .reduce(false, Boolean::logicalOr);
    }

    public boolean onJobCompleted(World world, BlockPos pulleyPos, BlockState pulleyState, boolean up, int targetY) {
        BlockPos ropePos = new BlockPos(pulleyPos.getX(), targetY - (up ? 1 : 0), pulleyPos.getZ());
        BlockState state = world.getBlockState(ropePos);
        BlockState defaultRopeState = BwtBlocks.ropeBlock.getDefaultState();
        if (!up) {
            if ((world.isAir(ropePos) || state.isReplaceable()) && defaultRopeState.canPlaceAt(world, ropePos) && hasRope()) {
                world.playSound(null, pulleyPos.down(), defaultRopeState.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 0.4F, 1.0F);
                world.setBlockState(ropePos, defaultRopeState);
                takeRope(false);
            } else {
                tryNextOperation(world, pulleyPos, pulleyState);
                rope.discard();
                ropeId = null;
                return false;
            }
        }
        if ((rope.isMovingUp() ? canGoUp(world, pulleyPos, pulleyState) : canGoDown(world, pulleyPos, pulleyState, true)) && !rope.isPathBlocked()) {
            rope.setTargetY(targetY + (rope.isMovingUp() ? 1 : -1));
            if (up) {
                if (!world.isAir(ropePos.up())) {
                    world.playSound(null, pulleyPos.down(), defaultRopeState.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS,
                            0.4F + (world.random.nextFloat() * 0.1F), 1.0F);
                    world.removeBlock(ropePos.up(), false);
                    putRope(false);
                }
            }
            return true;
        } else {
            tryNextOperation(world, pulleyPos, pulleyState);
            rope.discard();
            ropeId = null;
            return false;
        }
    }

    protected boolean takeRope(int count, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            long numExtracted = inventoryWrapper.extract(ItemVariant.of(BwtItems.ropeItem.getDefaultStack()), count, transaction);
            if (simulate) {
                transaction.abort();
            }
            else {
                transaction.commit();
            }
            return numExtracted >= count;
        }
    }

    protected boolean takeRope(boolean simulate) {
        return takeRope(1, simulate);
    }

    protected boolean putRope(int count, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            long numExtracted = inventoryWrapper.insert(ItemVariant.of(BwtItems.ropeItem.getDefaultStack()), count, transaction);
            if (simulate) {
                transaction.abort();
            }
            else {
                transaction.commit();
            }
            return numExtracted >= count;
        }
    }

    protected boolean putRope(boolean simulate) {
        return putRope(1, simulate);
    }

    public boolean hasRope(int count) {
        return takeRope(count, true);
    }

    public boolean hasRope() {
        return hasRope(1);
    }


    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory.readNbtList(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE), registryLookup);
        this.mechPower = nbt.getInt("mechPower");
        if (nbt.contains("ropeId")) {
            this.ropeId = nbt.getUuid("ropeId");
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.put("Inventory", this.inventory.toNbtList(registryLookup));
        nbt.putInt("mechPower", this.mechPower);
        if (this.rope != null && !rope.isRemoved()) {
            nbt.putUuid("ropeId", this.rope.getUuid());
        }
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new PulleyScreenHandler(syncId, playerInventory, inventory, propertyDelegate);
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
            PulleyBlockEntity.this.markDirty();
        }
    }
}
