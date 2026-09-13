package com.bwt.blocks.turntable;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.mixin.MovableBlockEntityMixin;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.turntable.TurntableRecipe;
import com.bwt.recipes.turntable.TurntableRecipeInput;
import com.bwt.sounds.BwtSoundEvents;
import com.bwt.utils.BlockPosAndState;
import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

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
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.rotationTickCounter = nbt.getInt("rotationTickCounter");
        this.craftingTurnCounter = nbt.getInt("craftingTurnCounter");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.putInt("rotationTickCounter", rotationTickCounter);
        nbt.putInt("craftingTurnCounter", craftingTurnCounter);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TurntableBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }
        if (!state.is(BwtBlocks.turntableBlock) || !state.getValue(TurntableBlock.MECH_POWERED)) {
            blockEntity.rotationTickCounter = 0;
            return;
        }
        int tickSetting = state.getValue(TurntableBlock.TICK_SETTING);

        blockEntity.rotationTickCounter++;
        if (blockEntity.rotationTickCounter >= ticksToRotate[tickSetting]) {
            level.playSound(null, pos, BwtSoundEvents.TURNTABLE_TURNING_CLICK, SoundSource.BLOCKS, 0.05f, 1f);
            blockEntity.rotationTickCounter = 0;
            rotateTurnTable(level, pos, state, blockEntity);
        }
    }

    protected static void rotateTurnTable(Level level, BlockPos pos, BlockState state, TurntableBlockEntity blockEntity) {
        Rotation rotation = state.getValue(TurntableBlock.POWERED) ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90;
        List<BlockPosAndState> blocksToRotate = getBlocksToRotate(level, pos);
        List<BlockPosAndState> attachedBlocksBeingRotated = getAttachedBlocksBeingRotated(level, blocksToRotate);
        List<BlockPosAndState> attachedBlockDestinations = getAttachedBlockDestinations(level, pos, attachedBlocksBeingRotated, rotation);
        pickUpAttachedBlocks(level, attachedBlocksBeingRotated, attachedBlockDestinations);
        rotateCentralColumnBlocks(level, blocksToRotate, blockEntity, rotation);
        placeRotatedAttachedBlocks(level, attachedBlocksBeingRotated, attachedBlockDestinations, rotation);
        // Notify neighbors of the rotation
        state.updateNeighbourShapes(level, pos, Block.UPDATE_NEIGHBORS);
    }

    protected static List<BlockPosAndState> getBlocksToRotate(Level level, BlockPos turntablePos) {
        RecipeManager recipeManager = level.getRecipeManager();

        List<BlockPosAndState> blocksToRotate = new ArrayList<>();
        for (int j = 1; j <= blocksAboveToRotate; j++) {
            BlockPos blockAbovePos = turntablePos.above(j);
            BlockState blockAboveState = level.getBlockState(blockAbovePos);
            if (blockAboveState.is(BlockTags.AIR)) {
                break;
            }
            if (!CanRotateHelper.canRotate(level, blockAbovePos, blockAboveState)) {
                break;
            }

            BlockEntity blockAboveEntity = level.getBlockEntity(blockAbovePos);
            blocksToRotate.add(new BlockPosAndState(blockAbovePos, blockAboveState, blockAboveEntity));

            // Crafting
            TurntableRecipeInput recipeInput = new TurntableRecipeInput(blockAboveState.getBlock());
            boolean recipeExistsForBlock = recipeManager.getRecipeFor(
                    BwtRecipes.TURNTABLE_RECIPE_TYPE,
                    recipeInput,
                    level
            ).isPresent();

            if (recipeExistsForBlock) {
                // Don't propagate rotation upward for craftables
                break;
            }
            // The < check here is just a minor optimization, so we don't need to check propagation unnecessarily
            if (!VerticalBlockAttachmentHelper.canPropagateRotationUpwards(level, blockAbovePos, blockAboveState)) {
                break;
            }
        }
        return blocksToRotate;
    }

    protected static List<BlockPosAndState> getAttachedBlocksBeingRotated(Level level, List<BlockPosAndState> blocksToRotate) {
        ArrayList<BlockPosAndState> attachedBlocks = new ArrayList<>();
        for (BlockPosAndState centralColumnPosAndState : blocksToRotate) {
            BlockPos centralColumnPos = centralColumnPosAndState.pos();
            BlockState centralColumnState = centralColumnPosAndState.state();

            attachedBlocks.addAll(Arrays.stream(Direction.values())
                    .filter(direction -> direction.getAxis().isHorizontal())
                    .map(centralColumnPos::relative)
                    .map(attachedPos -> BlockPosAndState.of(level, attachedPos))
                    .filter(attachedPosAndState -> HorizontalBlockAttachmentHelper.isAttached(centralColumnPos, centralColumnState, attachedPosAndState.pos(), attachedPosAndState.state()))
                    .toList());
        }
        return attachedBlocks;
    }

    protected static List<BlockPosAndState> getAttachedBlockDestinations(Level level, BlockPos turntablePos, List<BlockPosAndState> attachedBlocksBeingRotated, Rotation rotation) {
        return attachedBlocksBeingRotated.stream().map(attachedPosAndState -> {
            BlockPos attachedPos = attachedPosAndState.pos();
            BlockPos centralColumnPos = new BlockPos(turntablePos.getX(), attachedPos.getY(), turntablePos.getZ());

            Vec3i directionVector = attachedPos.subtract(centralColumnPos);
            Direction direction = Direction.fromDelta(directionVector.getX(), 0, directionVector.getZ());
            BlockPos attachedDestinationPos = centralColumnPos.relative(rotation.equals(Rotation.CLOCKWISE_90) ? Objects.requireNonNull(direction).getClockWise() : Objects.requireNonNull(direction).getCounterClockWise());
            return BlockPosAndState.of(level, attachedDestinationPos);
        }).toList();
    }

    protected static void pickUpAttachedBlocks(Level level, List<BlockPosAndState> attachedBlocksBeingRotated, List<BlockPosAndState> destinations) {
        HashSet<BlockPos> attachedPositions = attachedBlocksBeingRotated.stream().map(BlockPosAndState::pos).collect(Collectors.toCollection(HashSet::new));

        for (int idx = 0; idx < attachedBlocksBeingRotated.size(); idx++) {
            BlockPosAndState attachedPosAndState = attachedBlocksBeingRotated.get(idx);
            BlockPosAndState destination = destinations.get(idx);

            BlockPos attachedPos = attachedPosAndState.pos();
            BlockState attachedState = attachedPosAndState.state();
            BlockEntity attachedBlockEntity = attachedPosAndState.blockEntity();

            if (attachedPositions.contains(destination.pos()) || destination.state().canBeReplaced()) {
                // Pick up block cleanly
                if (attachedBlockEntity != null) {
                    level.removeBlockEntity(attachedPos);
                }
                level.removeBlock(attachedPos, false);
                level.updateNeighbourForOutputSignal(attachedPos, attachedState.getBlock());
            }
            else {
                // Break block with drops
                level.destroyBlock(attachedPos, true);
            }
        }
    }

    protected static void rotateCentralColumnBlocks(Level level, List<BlockPosAndState> blocksToRotate, TurntableBlockEntity blockEntity, Rotation rotation) {
        RecipeManager recipeManager = level.getRecipeManager();
        boolean recipeFound = false;

        for (BlockPosAndState blockToRotate : blocksToRotate) {
            BlockPos blockToRotatePos = blockToRotate.pos();
            BlockState blockToRotateState = blockToRotate.state();
            BlockEntity blockToRotateEntity = blockToRotate.blockEntity();

            BlockState rotatedState = blockToRotateState.rotate(rotation);
            RotationProcessHelper.processRotation(level, blockToRotatePos, blockToRotateState, rotatedState, blockToRotateEntity);

            // Crafting
            TurntableRecipeInput recipeInput = new TurntableRecipeInput(blockToRotateState.getBlock());
            Optional<TurntableRecipe> recipe = recipeManager.getRecipeFor(
                    BwtRecipes.TURNTABLE_RECIPE_TYPE,
                    recipeInput,
                    level
            ).map(RecipeHolder::value);

            if (recipe.isPresent()) {
                recipeFound = true;
                blockEntity.craftingTurnCounter += 1;
                level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, blockToRotatePos, Block.getId(blockToRotateState));
                if (blockEntity.craftingTurnCounter >= TurntableBlockEntity.turnsToCraft) {
                    blockEntity.craftingTurnCounter = 0;
                    level.setBlockAndUpdate(blockToRotatePos, recipe.get().getOutput().defaultBlockState());
                    Containers.dropContents(level, blockToRotatePos, recipe.get().getDrops());
                }
            }
        }

        if (!recipeFound) {
            blockEntity.craftingTurnCounter = 0;
        }
    }

    protected static void placeRotatedAttachedBlocks(Level level, List<BlockPosAndState> attachedBlocksBeingRotated, List<BlockPosAndState> destinations, Rotation rotation) {
        HashSet<BlockPos> attachedPositions = attachedBlocksBeingRotated.stream().map(BlockPosAndState::pos).collect(Collectors.toCollection(HashSet::new));

        for (int idx = 0; idx < attachedBlocksBeingRotated.size(); idx++) {
            BlockPosAndState attachedPosAndState = attachedBlocksBeingRotated.get(idx);
            BlockPosAndState destination = destinations.get(idx);

            BlockPos attachedPos = attachedPosAndState.pos();
            BlockState attachedState = attachedPosAndState.state();
            BlockEntity attachedBlockEntity = attachedPosAndState.blockEntity();

            if (attachedPositions.contains(destination.pos()) || destination.state().canBeReplaced()) {
                BlockState attachedStateRotated = attachedState.rotate(rotation);
                level.setBlockAndUpdate(destination.pos(), attachedStateRotated);
                attachedStateRotated.getBlock().setPlacedBy(level, destination.pos(), attachedStateRotated, null, attachedStateRotated.getBlock().getCloneItemStack(level, destination.pos(), attachedStateRotated));
                if (attachedBlockEntity != null) {
                    ((MovableBlockEntityMixin) attachedBlockEntity).setPos(destination.pos());
                    attachedBlockEntity.setBlockState(attachedStateRotated);
                    attachedBlockEntity.setChanged();
                    level.setBlockEntity(attachedBlockEntity);
                }
            }
            else {
                // Break block with drops
                level.destroyBlock(attachedPos, true);
            }
        }
    }
}
