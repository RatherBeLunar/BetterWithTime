package com.bwt.blocks.mech_hopper;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.MechPowerBlockBase;
import com.bwt.tags.BwtItemTags;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Predicate;

public class MechHopperBlock extends BaseEntityBlock implements MechPowerBlockBase {
    public static final MapCodec<MechHopperBlock> CODEC = MechHopperBlock.simpleCodec(MechHopperBlock::new);

    protected static final VoxelShape OUTLINE_SHAPE = Shapes.or(
//            Block.createCuboidShape(0, 4, 0, 16, 16, 2),
//            Block.createCuboidShape(0, 4, 0, 2, 16, 16),
//            Block.createCuboidShape(0, 4, 14, 16, 16, 16),
//            Block.createCuboidShape(14, 4, 0, 16, 16, 16),
            Block.box(0, 4, 0, 16, 16, 16),
            Block.box(5, 0, 5, 11, 4, 11)
    );

    public static final Map<Item, Predicate<ItemStack>> filterMap = Maps.newLinkedHashMap();

    public MechHopperBlock(Properties settings) {
        super(settings);
    }

    public static void addFilter(Item item, Predicate<ItemStack> predicate) {
        filterMap.put(item, predicate);
    }

    public record TagFilter(TagKey<Item> tagKey) implements Predicate<ItemStack> {

        @Override
        public boolean test(ItemStack itemStack) {
            return itemStack.is(this.tagKey);
        }
    }

    public static void addDefaultFilters() {
        addFilter(Items.LADDER, new TagFilter(BwtItemTags.PASSES_LADDER_FILTER));
        addFilter(Items.IRON_BARS, new TagFilter(BwtItemTags.PASSES_IRON_BARS_FILTER));
        for (Item trapdoor : new Item[]{
                Items.ACACIA_TRAPDOOR,
                Items.BAMBOO_TRAPDOOR,
                Items.BIRCH_TRAPDOOR,
                Items.CHERRY_TRAPDOOR,
                Items.COPPER_TRAPDOOR,
                Items.CRIMSON_TRAPDOOR,
                Items.DARK_OAK_TRAPDOOR,
                Items.EXPOSED_COPPER_TRAPDOOR,
                Items.IRON_TRAPDOOR,
                Items.JUNGLE_TRAPDOOR,
                Items.MANGROVE_TRAPDOOR,
                Items.OAK_TRAPDOOR,
                Items.OXIDIZED_COPPER_TRAPDOOR,
                Items.SPRUCE_TRAPDOOR,
                Items.WARPED_TRAPDOOR,
                Items.WAXED_COPPER_TRAPDOOR,
                Items.WAXED_EXPOSED_COPPER_TRAPDOOR,
                Items.WAXED_OXIDIZED_COPPER_TRAPDOOR,
                Items.WAXED_WEATHERED_COPPER_TRAPDOOR,
                Items.WEATHERED_COPPER_TRAPDOOR,
        }) {
            addFilter(trapdoor, new TagFilter(BwtItemTags.PASSES_TRAPDOOR_FILTER));
        }

        addFilter(Items.SOUL_SAND, itemStack -> false);
        addFilter(BwtBlocks.grateBlock.asItem(), new TagFilter(BwtItemTags.PASSES_GRATE_FILTER));
        addFilter(BwtBlocks.slatsBlock.asItem(), new TagFilter(BwtItemTags.PASSES_SLATS_FILTER));
        addFilter(BwtBlocks.wickerPaneBlock.asItem(), new TagFilter(BwtItemTags.PASSES_WICKER_FILTER));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        MechPowerBlockBase.super.appendProperties(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(MECH_POWERED, false);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MechHopperBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return OUTLINE_SHAPE;
    }

    public void schedulePowerUpdate(BlockState state, Level level, BlockPos pos) {
        boolean isMechPowered = isReceivingMechPower(level, state, pos);
        // If block just turned on
        if (isMechPowered && !isMechPowered(state)) {
            level.scheduleTick(pos, this, MechPowerBlockBase.getTurnOnTickRate());
        }
        // If block just turned off
        else if (!isMechPowered && isMechPowered(state)) {
            level.scheduleTick(pos, this, MechPowerBlockBase.getTurnOffTickRate());
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        schedulePowerUpdate(state, level, pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (level.isClientSide) {
            return;
        }
        schedulePowerUpdate(state, level, pos);
    }

    public BlockState getPowerStates(BlockState state, Level level, BlockPos pos) {
        return state.setValue(MECH_POWERED, isReceivingMechPower(level, state, pos));
    }

    public void updatePowerTransfer(Level level, BlockState blockState, BlockPos pos) {
        BlockState updatedState = getPowerStates(blockState, level, pos);
        level.setBlockAndUpdate(pos, updatedState);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MechHopperBlockEntity hopperBlockEntity) {
            hopperBlockEntity.mechPower = updatedState.getValue(MechHopperBlock.MECH_POWERED) ? 1 : 0;
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.updatePowerTransfer(level, state, pos);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MechHopperBlockEntity hopperBlockEntity) {
            hopperBlockEntity.onEntityCollided(level, entity);
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MechHopperBlockEntity hopperBlockEntity) {
            player.openMenu(hopperBlockEntity);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public Predicate<Direction> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return direction -> !direction.getAxis().isVertical();
    }

    @Nullable
    protected static <A extends BlockEntity> BlockEntityTicker<A> validateTicker(Level level, BlockEntityType<A> givenType) {
        return level.isClientSide ? null : BaseEntityBlock.createTickerHelper(givenType, BwtBlockEntities.mechHopperBlockEntity, MechHopperBlockEntity::tick);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return MechHopperBlock.validateTicker(level, type);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }
}
