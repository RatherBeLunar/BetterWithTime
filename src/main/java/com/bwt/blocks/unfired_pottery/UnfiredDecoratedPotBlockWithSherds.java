package com.bwt.blocks.unfired_pottery;

import com.bwt.blocks.BwtBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Sherds;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class UnfiredDecoratedPotBlockWithSherds extends UnfiredPotteryBlock implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final MapCodec<UnfiredDecoratedPotBlockWithSherds> CODEC = createCodec(UnfiredDecoratedPotBlockWithSherds::new);

    public UnfiredDecoratedPotBlockWithSherds(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public MapCodec<UnfiredDecoratedPotBlockWithSherds> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Blocks.DECORATED_POT.getDefaultState().getOutlineShape(world, pos, context);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        super.onSyncedBlockEvent(state, world, pos, type, data);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.onSyncedBlockEvent(type, data);
    }

    @Nullable
    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory)blockEntity : null;
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> validateTicker(
            BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<A> ticker
    ) {
        return expectedType == givenType ? ticker : null;
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new UnfiredDecoratedPotBlockEntity(pos, state);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof UnfiredDecoratedPotBlockEntity unfiredDecoratedPotBlockEntity) {
            if (!world.isClient && player.isCreative() && unfiredDecoratedPotBlockEntity.hasSherds()) {
                ItemStack itemStack = new ItemStack(this);
                itemStack.applyComponentsFrom(unfiredDecoratedPotBlockEntity.createComponentMap());
                ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
        }

        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved) {
            return;
        }
        if (!newState.isOf(state.getBlock()) && !newState.isAir()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof UnfiredDecoratedPotBlockEntity unfiredDecoratedPotBlockEntity) {
                ItemScatterer.spawn(world, pos, DefaultedList.copyOf(ItemStack.EMPTY, unfiredDecoratedPotBlockEntity.streamSherds().map(Item::getDefaultStack).toArray(ItemStack[]::new)));
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

//    @Override
//    protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
//        BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);
//        if (blockEntity instanceof UnfiredDecoratedPotBlockEntity unfiredDecoratedPotBlockEntity) {
//            builder.addDynamicDrop(
//                    DecoratedPotBlock.SHERDS_DYNAMIC_DROP_ID,
//                    lootConsumer -> unfiredDecoratedPotBlockEntity.streamSherds()
//                            .map(Item::getDefaultStack)
//                            .forEach(lootConsumer)
//            );
//        }
//
//        return super.getDroppedStacks(state, builder);
//    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!stack.isIn(ItemTags.DECORATED_POT_SHERDS)) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }
        Direction side = hit.getSide();
        if (side.getAxis().isVertical()) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof UnfiredDecoratedPotBlockEntity unfiredDecoratedPotBlockEntity)) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }
        if (!world.isClient && unfiredDecoratedPotBlockEntity.tryAddSherd(side, stack.getItem())) {
            stack.decrementUnlessCreative(1, player);
        }
        return ItemActionResult.success(world.isClient);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        Direction side = hit.getSide();
        if (side.getAxis().isVertical()) {
            return ActionResult.PASS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof UnfiredDecoratedPotBlockEntity unfiredDecoratedPotBlockEntity)) {
            return ActionResult.PASS;
        }
        Optional<Item> sherd = unfiredDecoratedPotBlockEntity.tryRemoveSherd(side);
        if (sherd.isEmpty()) {
            return ActionResult.PASS;
        }
        dropStack(world, pos, side, sherd.get().getDefaultStack());
        if (!unfiredDecoratedPotBlockEntity.hasSherds()) {
            world.setBlockState(pos, BwtBlocks.unfiredDecoratedPotBlock.getDefaultState(), Block.NOTIFY_LISTENERS, 0);
        }
        return ActionResult.success(world.isClient);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        super.appendTooltip(stack, context, tooltip, options);
        Sherds sherds = stack.getOrDefault(DataComponentTypes.POT_DECORATIONS, Sherds.DEFAULT);
        if (!sherds.equals(Sherds.DEFAULT)) {
            tooltip.add(ScreenTexts.EMPTY);
            Stream.of(sherds.front(), sherds.left(), sherds.right(), sherds.back())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Item::getDefaultStack)
                    .forEach(sherd -> tooltip.add(sherd.getName().copyContentOnly().formatted(Formatting.GRAY)));
        }
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return world.getBlockEntity(pos) instanceof UnfiredDecoratedPotBlockEntity unfiredDecoratedPotBlockEntity
                ? unfiredDecoratedPotBlockEntity.asStack()
                : super.getPickStack(world, pos, state);
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}
