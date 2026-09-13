package com.bwt.blocks.unfired_pottery;

import com.bwt.blocks.BwtBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class UnfiredDecoratedPotBlockWithSherds extends UnfiredPotteryBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final MapCodec<UnfiredDecoratedPotBlockWithSherds> CODEC = simpleCodec(UnfiredDecoratedPotBlockWithSherds::new);

    public UnfiredDecoratedPotBlockWithSherds(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public MapCodec<UnfiredDecoratedPotBlockWithSherds> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Blocks.DECORATED_POT.defaultBlockState().getShape(level, pos, context);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int type, int data) {
        super.triggerEvent(state, level, pos, type, data);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(type, data);
    }

    @Nullable
    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof MenuProvider ? (MenuProvider)blockEntity : null;
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> validateTicker(
            BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<A> ticker
    ) {
        return expectedType == givenType ? ticker : null;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new UnfiredDecoratedPotBlockEntity(pos, state);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof UnfiredDecoratedPotBlockEntity unfiredDecoratedPotBlockEntity) {
            if (!level.isClientSide && player.isCreative() && unfiredDecoratedPotBlockEntity.hasSherds()) {
                ItemStack itemStack = new ItemStack(this);
                itemStack.applyComponents(unfiredDecoratedPotBlockEntity.collectComponents());
                ItemEntity itemEntity = new ItemEntity(level, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, itemStack);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (moved) {
            return;
        }
        if (!newState.is(state.getBlock()) && !newState.isAir()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof UnfiredDecoratedPotBlockEntity unfiredDecoratedPotBlockEntity) {
                Containers.dropContents(level, pos, NonNullList.of(ItemStack.EMPTY, unfiredDecoratedPotBlockEntity.streamSherds().map(Item::getDefaultInstance).toArray(ItemStack[]::new)));
            }
        }
        super.onRemove(state, level, pos, newState, moved);
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(ItemTags.DECORATED_POT_SHERDS)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }
        Direction side = hit.getDirection();
        if (side.getAxis().isVertical()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof UnfiredDecoratedPotBlockEntity unfiredDecoratedPotBlockEntity)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }
        if (!level.isClientSide && unfiredDecoratedPotBlockEntity.tryAddSherd(side, stack.getItem())) {
            stack.consume(1, player);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        Direction side = hit.getDirection();
        if (side.getAxis().isVertical()) {
            return InteractionResult.PASS;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof UnfiredDecoratedPotBlockEntity unfiredDecoratedPotBlockEntity)) {
            return InteractionResult.PASS;
        }
        Optional<Item> sherd = unfiredDecoratedPotBlockEntity.tryRemoveSherd(side);
        if (sherd.isEmpty()) {
            return InteractionResult.PASS;
        }
        popResourceFromFace(level, pos, side, sherd.get().getDefaultInstance());
        if (!unfiredDecoratedPotBlockEntity.hasSherds()) {
            level.setBlock(pos, BwtBlocks.unfiredDecoratedPotBlock.defaultBlockState(), Block.UPDATE_CLIENTS, 0);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag options) {
        super.appendHoverText(stack, context, tooltip, options);
        PotDecorations sherds = stack.getOrDefault(DataComponents.POT_DECORATIONS, PotDecorations.EMPTY);
        if (!sherds.equals(PotDecorations.EMPTY)) {
            tooltip.add(CommonComponents.EMPTY);
            Stream.of(sherds.front(), sherds.left(), sherds.right(), sherds.back())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Item::getDefaultInstance)
                    .forEach(sherd -> tooltip.add(sherd.getHoverName().plainCopy().withStyle(ChatFormatting.GRAY)));
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockEntity(pos) instanceof UnfiredDecoratedPotBlockEntity unfiredDecoratedPotBlockEntity
                ? unfiredDecoratedPotBlockEntity.asStack()
                : super.getCloneItemStack(level, pos, state);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
