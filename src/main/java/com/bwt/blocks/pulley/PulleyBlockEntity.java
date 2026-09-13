package com.bwt.blocks.pulley;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.AnchorBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.RopeBlock;
import com.bwt.entities.MovingRopeEntity;
import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RailShape;
import java.util.*;

public class PulleyBlockEntity extends BlockEntity implements MenuProvider, Container {
    protected static final int INVENTORY_SIZE = 4;

    public final PulleyBlockEntity.Inventory inventory = new com.bwt.blocks.pulley.PulleyBlockEntity.Inventory(INVENTORY_SIZE);
    public final InventoryStorage inventoryWrapper = InventoryStorage.of(inventory, null);
    protected int mechPower;

    private MovingRopeEntity rope;
    private UUID ropeId;


    protected final ContainerData propertyDelegate = new ContainerData() {
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
        public int getCount() {
            return 1;
        }
    };

    public PulleyBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.pulleyBlockEntity, pos, state);
    }

    public static boolean isMechPowered(BlockState state) {
        return state.getValue(PulleyBlock.MECH_POWERED);
    }

    public static boolean isRedstonePowered(BlockState state) {
        return state.getValue(PulleyBlock.POWERED);
    }

    public boolean isRaising(BlockState state) {
        return isMechPowered(state) && !isRedstonePowered(state);
    }

    public boolean isLowering(BlockState state) {
        return !isMechPowered(state) && !isRedstonePowered(state)
                && inventory.items.stream()
                    .anyMatch(itemStack -> itemStack.is(BwtItems.ropeItem) && itemStack.getCount() > 0);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PulleyBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }
        blockEntity.tryNextOperation(level, pos, state);
    }

    private void tryNextOperation(Level level, BlockPos pos, BlockState state) {
        if (doesRopeEntityExist(level)) {
            return;
        }

        if (canGoDown(level, pos, state, false)) {
            goDown(level, pos);
            return;
        }
        if (canGoUp(level, pos, state)) {
            goUp(level, pos);
        }
    }

    private boolean doesRopeEntityExist(Level level) {
        // Rope is fully present and loaded
        if (rope != null && !rope.isRemoved()) {
            return true;
        }
        // Rope needs to be loaded from nbt but hasn't gotten to yet
        if (ropeId == null) {
            return false;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return true;
        }
        // Try to load the rope
        Entity entity = serverLevel.getEntity(ropeId);
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
        return (state.is(BwtBlocks.anchorBlock) && state.getValue(AnchorBlock.FACING) == Direction.UP) || state.is(BwtBlocks.ropeBlock);
    }

    private boolean canGoUp(Level level, BlockPos pos, BlockState state) {
        if (!isRaising(state)) {
            return false;
        }
        if (!putRope(true)) {
            return false;
        }
        BlockPos lowest = RopeBlock.getBottomRopePos(level, pos);
        return !lowest.equals(pos);
    }

    private boolean canGoDown(Level level, BlockPos pos, BlockState state, boolean isMoving) {
        if (!isLowering(state)) {
            return false;
        }
        if (!hasRope()) {
            return false;
        }
        BlockPos newPos = RopeBlock.getBottomRopePos(level, pos).below();
        BlockState newState = level.getBlockState(newPos);
        boolean flag = !isMoving && validRopeConnector(newState);
        return newPos.getY() > level.getMinBuildHeight() && (newState.canBeReplaced() || flag) && newPos.above().getY() > level.getMinBuildHeight();
    }

    private void goUp(Level level, BlockPos pos) {
        BlockPos lowest = RopeBlock.getBottomRopePos(level, pos);
        BlockState belowState = level.getBlockState(lowest.below());
        rope = new MovingRopeEntity(level, pos, lowest, lowest.above().getY());
        ropeId = null;
        if (validRopeConnector(belowState) && !movePlatform(level, lowest.below(), true)) {
            rope = null;
            return;
        }
        level.playSound(null, pos.below(), BwtBlocks.ropeBlock.defaultBlockState().getSoundType().getBreakSound(), SoundSource.BLOCKS,
                0.4F + (level.random.nextFloat() * 0.1F), 1.0F);
        level.addFreshEntity(rope);
        level.removeBlock(lowest, false);
        putRope(false);
    }

    private void goDown(Level level, BlockPos pos) {
        BlockPos newPos = RopeBlock.getBottomRopePos(level, pos).below();
        BlockState bottomState = level.getBlockState(newPos);
        rope = new MovingRopeEntity(level, pos, newPos.above(), newPos.getY());
        ropeId = null;
        if (validRopeConnector(bottomState) && !movePlatform(level, newPos, false)) {
            rope = null;
            return;
        }
        level.addFreshEntity(rope);
    }

    private boolean movePlatform(Level level, BlockPos anchor, boolean up) {
        BlockState state = level.getBlockState(anchor);
        if (!(validRopeConnector(state))) {
            return false;
        }

        HashSet<BlockPos> platformBlocks = new HashSet<>();
        platformBlocks.add(anchor);
        BlockPos below = anchor.below();
        BlockState belowState = level.getBlockState(below);
        if (isPlatform(belowState)) {
            if (!addToList(level, below, below, platformBlocks, up)) {
                return false;
            }
        }
        else if (!(up || isIgnoreable(belowState))) {
            return false;
        }


        if (!level.isClientSide) {
            for (BlockPos blockPos : platformBlocks) {
                Vec3i offset = blockPos.subtract(anchor.above());
                rope.addBlock(offset, level, blockPos, level.getBlockState(blockPos));
                BlockState upState = level.getBlockState(blockPos.above());
                if (isMoveableBlock(upState)) {
                    if (upState.getBlock() instanceof BaseRailBlock) {
                        upState = flattenRail(upState);
                    }
                    if (upState.getBlock() instanceof RedStoneWireBlock) {
                        upState = upState.setValue(RedStoneWireBlock.POWER, 0);
                    }
                    rope.addBlock(new Vec3i(offset.getX(), offset.getY() + 1, offset.getZ()), level, blockPos.above(), upState);
                }
            }
            rope.getBlockMap().entrySet().stream()
                    .sorted((a, b) -> b.getKey().getY() - a.getKey().getY())
                    .forEach(entry -> {
                        BlockPos blockPos = anchor.above().offset(entry.getKey());
                        level.removeBlock(blockPos, false);
                    });
        }

        return true;
    }

    public boolean isIgnoreable(BlockState state) {
        return state.canBeReplaced();
    }

    public boolean isMoveableBlock(BlockState state) {
        return state.is(Blocks.REDSTONE_WIRE) || state.getBlock() instanceof BaseRailBlock;
    }

    public boolean isPlatform(BlockState state) {
        return state.is(BwtBlocks.platformBlock);
    }

    private BlockState flattenRail(BlockState state) {
        EnumProperty<RailShape> property = state.hasProperty(BlockStateProperties.RAIL_SHAPE) ? BlockStateProperties.RAIL_SHAPE : state.hasProperty(BlockStateProperties.RAIL_SHAPE_STRAIGHT) ? BlockStateProperties.RAIL_SHAPE_STRAIGHT : null;
        RailShape currentShape = state.getValue(property);
        return state.setValue(property, switch (currentShape) {
            case ASCENDING_EAST, ASCENDING_WEST -> RailShape.EAST_WEST;
            case ASCENDING_NORTH, ASCENDING_SOUTH -> RailShape.NORTH_SOUTH;
            default -> currentShape;
        });
    }

    private boolean addToList(Level level, BlockPos pos, BlockPos sourcePos, HashSet<BlockPos> set, boolean up) {
        Vec3i distance = pos.subtract(sourcePos);
        if (Math.abs(distance.getX()) > 2 || Math.abs(distance.getZ()) > 2 || Math.abs(distance.getY()) > 5) {
            return false;
        }
        if (!isPlatform(level.getBlockState(pos))) {
            return true;
        }

        BlockPos blockCheck = up ? pos.above() : pos.below();
        BlockState otherState = level.getBlockState(blockCheck);
        if (!(isIgnoreable(otherState) || isMoveableBlock(otherState) || isPlatform(otherState)) && !set.contains(blockCheck))
            return false;

        set.add(pos);

        List<BlockPos> fails = new ArrayList<>();

        Arrays.stream(Direction.values()).map(pos::relative).forEach(offsetPos -> {
            Vec3i distance2 = offsetPos.subtract(sourcePos);
            if (Math.abs(distance2.getX()) > 2 || Math.abs(distance2.getZ()) > 2 || Math.abs(distance2.getY()) > 5) {
                return;
            }
            if (fails.isEmpty() && !set.contains(offsetPos)) {
                if (!addToList(level, offsetPos, sourcePos, set, up))
                    fails.add(offsetPos);
            }
        });

        return fails.isEmpty();
    }

    public boolean onJobCompleted(Level level, BlockPos pulleyPos, BlockState pulleyState, boolean up, int targetY) {
        BlockPos ropePos = new BlockPos(pulleyPos.getX(), targetY - (up ? 1 : 0), pulleyPos.getZ());
        BlockState state = level.getBlockState(ropePos);
        BlockState defaultRopeState = BwtBlocks.ropeBlock.defaultBlockState();
        if (!up) {
            if (state.canBeReplaced() && defaultRopeState.canSurvive(level, ropePos) && hasRope()) {
                level.playSound(null, pulleyPos.below(), defaultRopeState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 0.4F, 1.0F);
                level.setBlockAndUpdate(ropePos, defaultRopeState);
                takeRope(false);
            } else {
                tryNextOperation(level, pulleyPos, pulleyState);
                rope.discard();
                ropeId = null;
                return false;
            }
        }
        if ((rope.isMovingUp() ? canGoUp(level, pulleyPos, pulleyState) : canGoDown(level, pulleyPos, pulleyState, true)) && !rope.isPathBlocked()) {
            rope.setTargetY(targetY + (rope.isMovingUp() ? 1 : -1));
            if (up) {
                if (!level.getBlockState(ropePos.above()).is(BlockTags.AIR)) {
                    level.playSound(null, pulleyPos.below(), defaultRopeState.getSoundType().getBreakSound(), SoundSource.BLOCKS,
                            0.4F + (level.random.nextFloat() * 0.1F), 1.0F);
                    level.removeBlock(ropePos.above(), false);
                    putRope(false);
                }
            }
            return true;
        } else {
            tryNextOperation(level, pulleyPos, pulleyState);
            rope.discard();
            ropeId = null;
            return false;
        }
    }

    protected boolean takeRope(int count, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            long numExtracted = inventoryWrapper.extract(ItemVariant.of(BwtItems.ropeItem.getDefaultInstance()), count, transaction);
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
            long numExtracted = inventoryWrapper.insert(ItemVariant.of(BwtItems.ropeItem.getDefaultInstance()), count, transaction);
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
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.inventory.fromTag(nbt.getList("Inventory", Tag.TAG_COMPOUND), registryLookup);
        this.mechPower = nbt.getInt("mechPower");
        if (nbt.contains("ropeId")) {
            this.ropeId = nbt.getUUID("ropeId");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.put("Inventory", this.inventory.createTag(registryLookup));
        nbt.putInt("mechPower", this.mechPower);
        if (this.rope != null && !rope.isRemoved()) {
            nbt.putUUID("ropeId", this.rope.getUUID());
        }
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, net.minecraft.world.entity.player.Inventory playerInventory, Player player) {
        return new PulleyScreenHandler(syncId, playerInventory, inventory, propertyDelegate);
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
            PulleyBlockEntity.this.setChanged();
        }
    }
}
