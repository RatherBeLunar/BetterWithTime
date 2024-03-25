package com.bwt.blocks.turntable;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.mixin.MovableBlockEntityMixin;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.TurntableRecipe;
import com.bwt.sounds.BwtSoundEvents;
import com.bwt.utils.BlockPosAndState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.*;
import java.util.stream.Collectors;

public class TurntableBlockEntity extends BlockEntity {
    protected static final int blocksAboveToRotate = 2;
    protected static final int[] ticksToRotate = new int[] {10, 20, 40, 80};
    protected static final int turnsToCraft = 8;

    public int rotationTickCounter;
    public int craftingTurnCounter;


    public TurntableBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.turntableBlockEntity, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.rotationTickCounter = nbt.getInt("rotationTickCounter");
        this.craftingTurnCounter = nbt.getInt("craftingTurnCounter");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("rotationTickCounter", rotationTickCounter);
        nbt.putInt("craftingTurnCounter", craftingTurnCounter);
    }

    public static void tick(World world, BlockPos pos, BlockState state, TurntableBlockEntity blockEntity) {
        if (world.isClient) {
            return;
        }
        if (!state.isOf(BwtBlocks.turntableBlock) || !state.get(TurntableBlock.MECH_POWERED)) {
            blockEntity.rotationTickCounter = 0;
            return;
        }
        int tickSetting = state.get(TurntableBlock.TICK_SETTING);

        blockEntity.rotationTickCounter++;
        if (blockEntity.rotationTickCounter >= ticksToRotate[tickSetting]) {
            world.playSound(null, pos, BwtSoundEvents.TURNTABLE_TURNING_CLICK, SoundCategory.BLOCKS, 0.05f, 1f);
            blockEntity.rotationTickCounter = 0;
            rotateTurnTable(world, pos, state, blockEntity);
        }
    }

    protected static void rotateTurnTable(World world, BlockPos pos, BlockState state, TurntableBlockEntity blockEntity) {
        BlockRotation rotation = state.get(TurntableBlock.POWERED) ? BlockRotation.CLOCKWISE_90 : BlockRotation.COUNTERCLOCKWISE_90;
        for (int j = 1; j <= blocksAboveToRotate; j++) {
            BlockPos blockAbovePos = pos.up(j);
            BlockState blockAboveState = world.getBlockState(blockAbovePos);
            BlockEntity blockAboveEntity = world.getBlockEntity(blockAbovePos);
            BlockState rotatedState = blockAboveState;

            if (blockAboveState.isAir()) {
                blockEntity.craftingTurnCounter = 0;
                return;
            }

            if (CanRotateHelper.canRotate(world, blockAbovePos, blockAboveState)) {
                rotatedState = blockAboveState.getBlock().rotate(blockAboveState, rotation);
            }
            rotateAttachedBlocks(world, blockAbovePos, rotatedState, rotation);
            if (rotatedState != blockAboveState) {
                RotationProcessHelper.processRotation(world, blockAbovePos, blockAboveState, rotatedState, blockAboveEntity, state);
            }

            // Crafting
            RecipeManager recipeManager = world.getRecipeManager();
            Optional<TurntableRecipe> recipe = recipeManager.listAllOfType(BwtRecipes.TURNTABLE_RECIPE_TYPE).stream()
                    .map(RecipeEntry::value)
                    .filter(turntableRecipe -> turntableRecipe.matches(blockAboveState.getBlock()))
                    .findFirst();
            if (recipe.isPresent()) {
                blockEntity.craftingTurnCounter += 1;
                world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, blockAbovePos, Block.getRawIdFromState(blockAboveState));
                if (blockEntity.craftingTurnCounter >= turnsToCraft) {
                    blockEntity.craftingTurnCounter = 0;
                    world.setBlockState(blockAbovePos, recipe.get().getOutput().getDefaultState());
                    ItemScatterer.spawn(world, blockAbovePos, recipe.get().getDrops());
                }
                // Don't propagate rotation upward for craftables
                break;
            }
            else {
                blockEntity.craftingTurnCounter = 0;
            }


            // The < check here is just a minor optimization, so we don't need to check propagation unnecessarily
            if (j < blocksAboveToRotate && !VerticalBlockAttachmentHelper.canPropagateRotationUpwards(world, blockAbovePos, blockAboveState)) {
                break;
            }
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
