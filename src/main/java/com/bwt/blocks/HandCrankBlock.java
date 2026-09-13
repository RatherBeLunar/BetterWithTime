package com.bwt.blocks;

import com.bwt.items.BwtItems;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class HandCrankBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty CLICK_TIMER = IntegerProperty.create("click_timer", 0, 7);
    private static final int tickRate = 3;
    private static final int delayBeforeReset = 15;
    private static final int baseHeight = 4;

    public HandCrankBlock(Properties settings) {
        super(settings);
    }

    public static boolean isPowered(BlockState state) {
        return state.getValue(CLICK_TIMER) > 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, CLICK_TIMER);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockPos = pos.below();
        return this.canPlaceAbove(level, blockPos, level.getBlockState(blockPos));
    }

    protected boolean canPlaceAbove(LevelReader level, BlockPos pos, BlockState state) {
        return state.isFaceSturdy(level, pos, Direction.UP, SupportType.RIGID);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(0f, 0, 0, 16f, baseHeight, 16f);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if ((hit.getLocation().y - pos.getY()) * 16 <= baseHeight) {
            return InteractionResult.FAIL;
        }

        int clickTimer = state.getValue(CLICK_TIMER);

        if (clickTimer != 0) {
            return InteractionResult.FAIL;
        }
        if (player.getFoodData().getFoodLevel() <= 8) {
            if (level.isClientSide) {
                player.displayClientMessage(Component.nullToEmpty("You're too exhausted for manual labor."), true);
            }
            return InteractionResult.FAIL;
        }
        player.causeFoodExhaustion( 2.0F ); // every two pulls results in a half pip of hunger

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!checkForOverpower(level, pos)) {
            level.setBlockAndUpdate(pos, state.setValue(CLICK_TIMER, 1));
            playClick(level, pos);
            level.scheduleTick(pos, this, tickRate);
        }
        else {
            breakWithDrop(level, pos);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
        int clickTimer = state.getValue(CLICK_TIMER);

        if (clickTimer <= 0) {
            return;
        }
        if (clickTimer >= 7) {
            level.setBlockAndUpdate(pos, state.setValue(CLICK_TIMER, 0));
            playClick(level, pos);
            return;
        }
        playClick(level, pos);

        if (clickTimer == 6) {
            level.scheduleTick(pos, this, delayBeforeReset);
        } else {
            level.scheduleTick(pos, this, tickRate + clickTimer);
        }

        // no notify here as it's not an actual state-change, just an internal timer update
        level.setBlock(pos, state.setValue(CLICK_TIMER, clickTimer + 1), 0, 0);
    }

    public boolean checkForOverpower(Level level, BlockPos pos) {
        int numPotentialDevicesToPower = 0;

        for (Direction direction : Direction.values()) {
            if (direction.equals(Direction.UP)) {
                continue;
            }

            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);
            Block neighborBlock = neighborState.getBlock();
            if (neighborBlock instanceof MechPowerBlockBase mechNeighborBlock) {
                if (mechNeighborBlock.getValidHandCrankFaces(neighborState, neighborPos).test(direction.getOpposite())) {
                    numPotentialDevicesToPower++;
                }
            }
        }

        return numPotentialDevicesToPower > 1;
    }

    public void playClick(Level level, BlockPos pos) {
        level.playSound(null, pos, BwtSoundEvents.HAND_CRANK_CLICK, SoundSource.BLOCKS, 1.0f,  2.0f);
    }

    public void breakWithDrop(Level level, BlockPos pos) {
        BlockState airState = Blocks.AIR.defaultBlockState();
        level.setBlockAndUpdate(pos, airState);
        Vec3 centerPos = pos.getCenter();
        level.playLocalSound(pos, BwtSoundEvents.MECH_EXPLODE, SoundSource.BLOCKS, 0.5f, 1, false);
        for (Item item : new Item[]{Items.STICK, Items.STONE, BwtItems.gearItem}) {
            ItemEntity itemEntity = new ItemEntity(level, centerPos.x, centerPos.y, centerPos.z, item.getDefaultInstance());
            itemEntity.setDeltaMovement(level.random.nextDouble() * -0.01 + 0.02, 0.2, level.random.nextDouble() * -0.01 + 0.02);
            level.addFreshEntity(itemEntity);
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (level.isClientSide || !level.getBlockState(pos).is(this)) {
            return;
        }
        if (canSurvive(state, level, pos)) {
            return;
        }
        dropResources(state, level, pos);
        level.removeBlock(pos, notify);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (moved) {
            return;
        }
        super.onRemove(state, level, pos, newState, moved);
    }
}
