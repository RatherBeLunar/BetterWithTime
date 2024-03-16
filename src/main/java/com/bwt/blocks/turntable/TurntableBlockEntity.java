package com.bwt.blocks.turntable;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.mixin.MovableBlockEntityMixin;
import com.bwt.utils.BlockPosAndState;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TurntableBlockEntity extends BlockEntity {
    protected static final int blocksAboveToRotate = 2;
    protected static final int[] ticksToRotate = new int[] {10, 20, 40, 80};


    public int rotationTickCounter;

    public TurntableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public TurntableBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.turntableBlockEntity, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.rotationTickCounter = nbt.getInt("rotationTickCounter");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("rotationTickCounter", rotationTickCounter);
    }

    public static void tick(World world, BlockPos pos, BlockState state, TurntableBlockEntity blockEntity) {
        if (!state.isOf(BwtBlocks.turntableBlock) || !state.get(TurntableBlock.MECH_POWERED)) {
            blockEntity.rotationTickCounter = 0;
            return;
        }
        int tickSetting = state.get(TurntableBlock.TICK_SETTING);

        blockEntity.rotationTickCounter++;
        if (blockEntity.rotationTickCounter >= ticksToRotate[tickSetting]) {
            world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 0.05f, 1f);
            blockEntity.rotationTickCounter = 0;
            rotateTurnTable(world, pos, state, blockEntity);
        }
    }

    protected static void rotateTurnTable(World world, BlockPos pos, BlockState state, TurntableBlockEntity blockEntity) {
        BlockRotation rotation = state.get(TurntableBlock.POWERED) ? BlockRotation.CLOCKWISE_90 : BlockRotation.COUNTERCLOCKWISE_90;
        for (int j = 1; j < blocksAboveToRotate + 1; j++) {
            BlockPos blockAbovePos = pos.up(j);
            BlockState blockAboveState = world.getBlockState(blockAbovePos);
            BlockState rotatedState = blockAboveState.getBlock().rotate(blockAboveState, rotation);
            if (rotatedState != blockAboveState) {
                world.setBlockState(blockAbovePos, rotatedState);
            }
            rotateAttachedBlocks(world, blockAbovePos, blockAboveState, rotation);
        }
    }

    protected static void rotateAttachedBlocks(World world, BlockPos blockAbovePos, BlockState blockAboveState, BlockRotation rotation) {
        List<BlockPosAndState> attachedBlocks = Arrays.stream(Direction.values())
                .filter(direction -> direction.getAxis().isHorizontal())
                .map(blockAbovePos::offset)
                .map(attachedPos -> BlockPosAndState.of(world, attachedPos))
                .filter(attachedPosAndState -> HorizontalBlockAttachmentHelper.isAttached(blockAbovePos, blockAboveState, attachedPosAndState.pos(), attachedPosAndState.state()))
                .toList();
        HashSet<BlockPos> attachedPositions = attachedBlocks.stream().map(BlockPosAndState::pos).collect(Collectors.toCollection(HashSet::new));
        HashSet<BlockPosAndState> destinationStates = new HashSet<>();
        HashSet<BlockPos> destinationPositions = new HashSet<>();

        for (BlockPosAndState attachedPosAndState : attachedBlocks) {
            BlockPos attachedPos = attachedPosAndState.pos();
            BlockState attachedState = attachedPosAndState.state();
            BlockEntity attachedBlockEntity = attachedPosAndState.blockEntity();

            Vec3i directionVector = attachedPos.subtract(blockAbovePos);
            Direction direction = Direction.fromVector(directionVector.getX(), 0, directionVector.getZ());
            BlockPos attachedDestinationPos = blockAbovePos.offset(rotation.equals(BlockRotation.CLOCKWISE_90) ? Objects.requireNonNull(direction).rotateYClockwise() : Objects.requireNonNull(direction).rotateYCounterclockwise());
            if (attachedPositions.contains(attachedDestinationPos) || world.getBlockState(attachedDestinationPos).isReplaceable()) {
                BlockState attachedStateRotated = attachedState.rotate(rotation);
                if (attachedBlockEntity != null) {
                    world.removeBlockEntity(attachedPos);
                }
                destinationStates.add(new BlockPosAndState(attachedDestinationPos, attachedStateRotated, attachedBlockEntity));
                destinationPositions.add(attachedDestinationPos);
            }
            else {
                world.breakBlock(attachedPos, true);
            }
        }
        for (BlockPosAndState blockPosAndState : destinationStates) {
            world.setBlockState(blockPosAndState.pos(), blockPosAndState.state());
            BlockEntity blockEntity = blockPosAndState.blockEntity();
            if (blockEntity != null) {
                ((MovableBlockEntityMixin) blockEntity).setPos(blockPosAndState.pos());
                blockEntity.setCachedState(blockPosAndState.state());
                blockEntity.markDirty();
                world.addBlockEntity(blockEntity);
            }
        }
        // Turn the source positions set into a set of positions that weren't just replaced
        attachedPositions.removeAll(destinationPositions);
        attachedPositions.forEach(attachedPosition -> {
            FluidState fluidState = world.getFluidState(attachedPosition);
            world.setBlockState(attachedPosition, fluidState.getBlockState());
        });

    }
}
