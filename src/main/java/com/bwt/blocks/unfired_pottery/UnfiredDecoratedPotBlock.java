package com.bwt.blocks.unfired_pottery;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class UnfiredDecoratedPotBlock extends UnfiredPotteryBlock {
    public static final VoxelShape BASE_SHAPE = Block.column(14.0, 0.0, 16.0);
    public static final VoxelShape NECK_SHAPE = Block.column(6.0, 16.0, 17.0);
    public static final VoxelShape HEAD_SHAPE = Block.column(8.0, 17.0, 20.0);
    public static final VoxelShape ENTIRE_SHAPE = Shapes.or(BASE_SHAPE, NECK_SHAPE, HEAD_SHAPE);
    public static Set<Vector3f> EXTENTS = ENTIRE_SHAPE.toAabbs().stream().flatMap(aabb -> {
        ArrayList<Vector3f> extents = new ArrayList<>();
        // Using binary counting to get the extents
        for (int i = 0; i < 8; i++) {
            float x = (float) ((i & 4) != 0 ? aabb.minX : aabb.maxX);
            float y = (float) ((i & 2) != 0 ? aabb.minY : aabb.maxY);
            float z = (float) ((i & 1) != 0 ? aabb.minZ : aabb.maxZ);
            extents.add(new Vector3f(x, y, z));
        }
        return extents.stream();
    }).collect(Collectors.toSet());

    public UnfiredDecoratedPotBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BASE_SHAPE;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(ItemTags.DECORATED_POT_SHERDS)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }
        BlockState replacementState = BwtBlocks.unfiredDecoratedPotBlockWithSherds.defaultBlockState().setValue(UnfiredDecoratedPotBlockWithSherds.FACING, hit.getDirection());
        level.setBlock(pos, replacementState, Block.UPDATE_CLIENTS, 0);
        return replacementState.useItemOn(stack, level, player, hand, hit);
    }
}
