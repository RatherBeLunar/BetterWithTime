package com.bwt.blocks;

import com.bwt.blocks.pulley.PulleyBlockEntity;
import com.bwt.entities.MovingAnchorEntity;
import com.bwt.items.BwtItems;
import com.bwt.sounds.BwtSoundEvents;
import com.bwt.utils.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AnchorBlock extends Block {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final Box baseBox = new Box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);
    protected static final VoxelShape NUB_SHAPE = Block.createCuboidShape(6, 6, 6, 10, 10, 10);
    protected static final List<VoxelShape> SHAPES = Arrays.stream(Direction.values())
            .map(direction -> VoxelShapes.union(BlockUtils.rotateCuboidFromUp(direction, baseBox), NUB_SHAPE))
            .collect(Collectors.toList());

    public AnchorBlock(Settings settings) {
        super(settings);
    }

    public static boolean notifyAnchorOfAttachedPulleyStateChange(World world, BlockPos pos, BlockState state, PulleyBlockEntity pulleyBlockEntity) {
        int movementDirection = 0;

        if (pulleyBlockEntity.isRaising(pulleyBlockEntity.getCachedState())) {
            if (world.getBlockState(pos.up()).isOf(BwtBlocks.ropeBlock)) {
                movementDirection = 1;
            }
        }
        else if (pulleyBlockEntity.isLowering(pulleyBlockEntity.getCachedState())) {
            BlockState downState = world.getBlockState(pos.down());
            if (downState.isReplaceable() || downState.isOf(BwtBlocks.platformBlock)) {
                movementDirection = -1;
            }
        }

        if (movementDirection != 0) {
            convertAnchorToEntity(world, pos, pulleyBlockEntity, movementDirection);
            return true;
        }

        return false;
    }

    protected static void convertAnchorToEntity(World world, BlockPos pos, PulleyBlockEntity pulleyBlockEntity, int movementDirection) {
        BlockPos pulleyPos = pulleyBlockEntity.getPos();

        MovingAnchorEntity anchorEntity = new MovingAnchorEntity(world, pulleyPos, pos, movementDirection);
        world.removeBlock(pos, false);
        world.spawnEntity(anchorEntity);

//        ConvertConnectedPlatformsToEntities( world, i, j, k, entityAnchor );

    }

//    private void ConvertConnectedPlatformsToEntities( World world, int i, int j, int k, FCEntityMovingAnchor associatedAnchorEntity )
//    {
//        int iTargetJ = j - 1;
//
//        int iTargetBlockID = world.getBlockId( i, iTargetJ, k );
//
//        if ( iTargetBlockID == FCBetterThanWolves.fcPlatform.blockID )
//        {
//            ( (FCBlockPlatform)FCBetterThanWolves.fcPlatform ).CovertToEntitiesFromThisPlatform(
//                    world, i, iTargetJ, k, associatedAnchorEntity );
//        }
//    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.get(FACING).getId());
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getMainHandStack().isOf(BwtItems.ropeItem)) {
            return super.onUse(state, world, pos, player, hand, hit);
        }
        BlockPos.Mutable mutablePos = pos.mutableCopy().move(Direction.DOWN);
        while (world.getBlockState(mutablePos.down()).isOf(BwtBlocks.ropeBlock)) {
            mutablePos.move(Direction.DOWN);
        }
        if (!world.getBlockState(mutablePos).isOf(BwtBlocks.ropeBlock)) {
            return ActionResult.FAIL;
        }
        player.getInventory().offerOrDrop(BwtItems.ropeItem.getDefaultStack());
        world.setBlockState(mutablePos, Blocks.AIR.getDefaultState());
        world.playSound(null, pos, BwtSoundEvents.ANCHOR_RETRACT, SoundCategory.PLAYERS, 0.2f, (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 1.4f + 2.0f);

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public static boolean isHorizontal(BlockState state) {
        return state.get(FACING).getAxis().isHorizontal();
    }
}
