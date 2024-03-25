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
        List<BlockPosAndState> blocksToRotate = getBlocksToRotate(world, pos);
        List<BlockPosAndState> attachedBlocksBeingRotated = getAttachedBlocksBeingRotated(world, blocksToRotate);
        List<BlockPosAndState> attachedBlockDestinations = getAttachedBlockDestinations(world, pos, attachedBlocksBeingRotated, rotation);
        pickUpAttachedBlocks(world, attachedBlocksBeingRotated, attachedBlockDestinations);
        rotateCentralColumnBlocks(world, blocksToRotate, blockEntity, rotation);
        placeRotatedAttachedBlocks(world, attachedBlocksBeingRotated, attachedBlockDestinations, rotation);
    }

    protected static List<BlockPosAndState> getBlocksToRotate(World world, BlockPos turntablePos) {
        RecipeManager recipeManager = world.getRecipeManager();
        List<RecipeEntry<TurntableRecipe>> recipeEntries = recipeManager.listAllOfType(BwtRecipes.TURNTABLE_RECIPE_TYPE);

        List<BlockPosAndState> blocksToRotate = new ArrayList<>();
        for (int j = 1; j <= blocksAboveToRotate; j++) {
            BlockPos blockAbovePos = turntablePos.up(j);
            BlockState blockAboveState = world.getBlockState(blockAbovePos);
            if (blockAboveState.isAir()) {
                break;
            }
            if (!CanRotateHelper.canRotate(world, blockAbovePos, blockAboveState)) {
                break;
            }

            BlockEntity blockAboveEntity = world.getBlockEntity(blockAbovePos);
            blocksToRotate.add(new BlockPosAndState(blockAbovePos, blockAboveState, blockAboveEntity));

            // Crafting
            boolean recipeExistsForBlock = recipeEntries.stream()
                    .map(RecipeEntry::value)
                    .anyMatch(turntableRecipe -> turntableRecipe.matches(blockAboveState.getBlock()));
            if (recipeExistsForBlock) {
                // Don't propagate rotation upward for craftables
                break;
            }
            // The < check here is just a minor optimization, so we don't need to check propagation unnecessarily
            if (!VerticalBlockAttachmentHelper.canPropagateRotationUpwards(world, blockAbovePos, blockAboveState)) {
                break;
            }
        }
        return blocksToRotate;
    }

    protected static List<BlockPosAndState> getAttachedBlocksBeingRotated(World world, List<BlockPosAndState> blocksToRotate) {
        ArrayList<BlockPosAndState> attachedBlocks = new ArrayList<>();
        for (BlockPosAndState centralColumnPosAndState : blocksToRotate) {
            BlockPos centralColumnPos = centralColumnPosAndState.pos();
            BlockState centralColumnState = centralColumnPosAndState.state();

            attachedBlocks.addAll(Arrays.stream(Direction.values())
                    .filter(direction -> direction.getAxis().isHorizontal())
                    .map(centralColumnPos::offset)
                    .map(attachedPos -> BlockPosAndState.of(world, attachedPos))
                    .filter(attachedPosAndState -> HorizontalBlockAttachmentHelper.isAttached(centralColumnPos, centralColumnState, attachedPosAndState.pos(), attachedPosAndState.state()))
                    .toList());
        }
        return attachedBlocks;
    }

    protected static List<BlockPosAndState> getAttachedBlockDestinations(World world, BlockPos turntablePos, List<BlockPosAndState> attachedBlocksBeingRotated, BlockRotation rotation) {
        return attachedBlocksBeingRotated.stream().map(attachedPosAndState -> {
            BlockPos attachedPos = attachedPosAndState.pos();
            BlockPos centralColumnPos = new BlockPos(turntablePos.getX(), attachedPos.getY(), turntablePos.getZ());

            Vec3i directionVector = attachedPos.subtract(centralColumnPos);
            Direction direction = Direction.fromVector(directionVector.getX(), 0, directionVector.getZ());
            BlockPos attachedDestinationPos = centralColumnPos.offset(rotation.equals(BlockRotation.CLOCKWISE_90) ? Objects.requireNonNull(direction).rotateYClockwise() : Objects.requireNonNull(direction).rotateYCounterclockwise());
            return BlockPosAndState.of(world, attachedDestinationPos);
        }).toList();
    }

    protected static void pickUpAttachedBlocks(World world, List<BlockPosAndState> attachedBlocksBeingRotated, List<BlockPosAndState> destinations) {
        HashSet<BlockPos> attachedPositions = attachedBlocksBeingRotated.stream().map(BlockPosAndState::pos).collect(Collectors.toCollection(HashSet::new));

        for (int idx = 0; idx < attachedBlocksBeingRotated.size(); idx++) {
            BlockPosAndState attachedPosAndState = attachedBlocksBeingRotated.get(idx);
            BlockPosAndState destination = destinations.get(idx);

            BlockPos attachedPos = attachedPosAndState.pos();
            BlockEntity attachedBlockEntity = attachedPosAndState.blockEntity();

            if (attachedPositions.contains(destination.pos()) || destination.state().isReplaceable()) {
                // Pick up block cleanly
                if (attachedBlockEntity != null) {
                    world.removeBlockEntity(attachedPos);
                }
                world.removeBlock(attachedPos, false);
            }
            else {
                // Break block with drops
                world.breakBlock(attachedPos, true);
            }
        }
    }

    protected static void rotateCentralColumnBlocks(World world, List<BlockPosAndState> blocksToRotate, TurntableBlockEntity blockEntity, BlockRotation rotation) {
        RecipeManager recipeManager = world.getRecipeManager();
        List<RecipeEntry<TurntableRecipe>> recipeEntries = recipeManager.listAllOfType(BwtRecipes.TURNTABLE_RECIPE_TYPE);
        boolean recipeFound = false;

        for (BlockPosAndState blockToRotate : blocksToRotate) {
            BlockPos blockToRotatePos = blockToRotate.pos();
            BlockState blockToRotateState = blockToRotate.state();
            BlockEntity blockToRotateEntity = blockToRotate.blockEntity();

            BlockState rotatedState = blockToRotateState.getBlock().rotate(blockToRotateState, rotation);
            RotationProcessHelper.processRotation(world, blockToRotatePos, blockToRotateState, rotatedState, blockToRotateEntity);

            // Crafting
            Optional<TurntableRecipe> recipe = recipeEntries.stream()
                    .map(RecipeEntry::value)
                    .filter(turntableRecipe -> turntableRecipe.matches(blockToRotateState.getBlock()))
                    .findFirst();
            if (recipe.isPresent()) {
                recipeFound = true;
                blockEntity.craftingTurnCounter += 1;
                world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, blockToRotatePos, Block.getRawIdFromState(blockToRotateState));
                if (blockEntity.craftingTurnCounter >= TurntableBlockEntity.turnsToCraft) {
                    blockEntity.craftingTurnCounter = 0;
                    world.setBlockState(blockToRotatePos, recipe.get().getOutput().getDefaultState());
                    ItemScatterer.spawn(world, blockToRotatePos, recipe.get().getDrops());
                }
            }
        }

        if (!recipeFound) {
            blockEntity.craftingTurnCounter = 0;
        }
    }

    protected static void placeRotatedAttachedBlocks(World world, List<BlockPosAndState> attachedBlocksBeingRotated, List<BlockPosAndState> destinations, BlockRotation rotation) {
        HashSet<BlockPos> attachedPositions = attachedBlocksBeingRotated.stream().map(BlockPosAndState::pos).collect(Collectors.toCollection(HashSet::new));

        for (int idx = 0; idx < attachedBlocksBeingRotated.size(); idx++) {
            BlockPosAndState attachedPosAndState = attachedBlocksBeingRotated.get(idx);
            BlockPosAndState destination = destinations.get(idx);

            BlockPos attachedPos = attachedPosAndState.pos();
            BlockState attachedState = attachedPosAndState.state();
            BlockEntity attachedBlockEntity = attachedPosAndState.blockEntity();

            if (attachedPositions.contains(destination.pos()) || destination.state().isReplaceable()) {
                BlockState attachedStateRotated = attachedState.rotate(rotation);
                world.setBlockState(destination.pos(), attachedStateRotated);
                attachedStateRotated.getBlock().onPlaced(world, destination.pos(), attachedStateRotated, null, attachedStateRotated.getBlock().getPickStack(world, destination.pos(), attachedStateRotated));
                if (attachedBlockEntity != null) {
                    ((MovableBlockEntityMixin) attachedBlockEntity).setPos(destination.pos());
                    attachedBlockEntity.setCachedState(attachedStateRotated);
                    attachedBlockEntity.markDirty();
                    world.addBlockEntity(attachedBlockEntity);
                }
            }
            else {
                // Break block with drops
                world.breakBlock(attachedPos, true);
            }
        }
    }
}
