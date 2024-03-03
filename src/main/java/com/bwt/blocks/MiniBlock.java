package com.bwt.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.List;

public abstract class MiniBlock extends Block implements Waterloggable, RotateWithEmptyHand {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public Block fullBlock;

    protected MiniBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false));
    }

    public MiniBlock(Settings settings, Block fullBlock) {
        this(settings);
        this.fullBlock = fullBlock;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
    }
    
    public static void registerMiniBlocks(ArrayList<SidingBlock> sidingBlocks, ArrayList<MouldingBlock> mouldingBlocks) {
        WoodType.stream().forEach(woodType -> {
            sidingBlocks.add(SidingBlock.ofWoodType(woodType));
            mouldingBlocks.add(MouldingBlock.ofWoodType(woodType));
        });
        List<Block> blockSlabPairs = List.of(
                Blocks.STONE, Blocks.STONE_SLAB,
                Blocks.STONE_BRICKS, Blocks.STONE_BRICK_SLAB
        );
        for (int i = 0; i < blockSlabPairs.size() - 1; i += 2) {
            sidingBlocks.add(SidingBlock.ofBlock(blockSlabPairs.get(i), blockSlabPairs.get(i + 1)));
            mouldingBlocks.add(MouldingBlock.ofBlock(blockSlabPairs.get(i), blockSlabPairs.get(i + 1)));
        }
        for (int i = 0; i < sidingBlocks.size(); i++) {
            SidingBlock sidingBlock = sidingBlocks.get(i);
            MouldingBlock mouldingBlock = mouldingBlocks.get(i);
            Identifier blockId = Registries.BLOCK.getId(sidingBlock.fullBlock);
            Identifier sidingId = new Identifier("bwt", blockId.getPath() + "_siding");
            Identifier mouldingId = new Identifier("bwt", blockId.getPath() + "_moulding");
            Registry.register(Registries.BLOCK, sidingId, sidingBlock);
            Registry.register(Registries.BLOCK, mouldingId, mouldingBlock);
            Registry.register(Registries.ITEM, sidingId, new BlockItem(sidingBlock, new FabricItemSettings()));
            Registry.register(Registries.ITEM, mouldingId, new BlockItem(mouldingBlock, new FabricItemSettings()));
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return type.equals(NavigationType.WATER) && world.getFluidState(pos).isIn(FluidTags.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockState updatedState = onUseRotate(state, world, pos, player);
        if (updatedState == state) {
            return ActionResult.PASS;
        }
        world.setBlockState(pos, updatedState);
        return ActionResult.SUCCESS;
    }
}
