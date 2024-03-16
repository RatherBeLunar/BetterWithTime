package com.bwt.blocks;

import com.bwt.BetterWithTime;
import com.bwt.items.BwtItems;
import net.minecraft.block.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class HandCrankBlock extends Block {
    public static DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static IntProperty CLICK_TIMER = IntProperty.of("click_timer", 0, 7);
    private static final int tickRate = 3;
    private static final int delayBeforeReset = 15;
    private static final int baseHeight = 4;

    public HandCrankBlock(Settings settings) {
        super(settings);
    }

    public static boolean isPowered(BlockState state) {
        return state.get(CLICK_TIMER) > 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, CLICK_TIMER);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        return this.canPlaceAbove(world, blockPos, world.getBlockState(blockPos));
    }

    protected boolean canPlaceAbove(WorldView world, BlockPos pos, BlockState state) {
        return state.isSideSolid(world, pos, Direction.UP, SideShapeType.RIGID);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0f, 0, 0, 16f, baseHeight, 16f);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if ((hit.getPos().y - pos.getY()) * 16 <= baseHeight) {
            return ActionResult.FAIL;
        }

        int clickTimer = state.get(CLICK_TIMER);

        if (clickTimer == 0) {
            if (player.getHungerManager().getFoodLevel() > 8) {
                player.addExhaustion( 2.0F ); // every two pulls results in a half pip of hunger

                if (!world.isClient) {
                    if (!checkForOverpower(world, pos)) {
                        world.setBlockState(pos, state.with(CLICK_TIMER, 1));
                        playClick(world, pos);
                        world.scheduleBlockTick(pos, this, tickRate);
                    }
                    else {
                        breakWithDrop(world, pos);
                    }
                }
            }
            else {
                if( world.isClient) {
                    player.sendMessage(Text.of("You're too exhausted for manual labor."), true);
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        int clickTimer = state.get(CLICK_TIMER);

        if (clickTimer <= 0) {
            return;
        }
        if (clickTimer >= 7) {
            world.setBlockState(pos, state.with(CLICK_TIMER, 0));
            playClick(world, pos);
            return;
        }
        playClick(world, pos);

        if (clickTimer == 6) {
            world.scheduleBlockTick(pos, this, delayBeforeReset);
        } else {
            world.scheduleBlockTick(pos, this, tickRate + clickTimer);
        }

        // no notify here as it's not an actual state-change, just an internal timer update
        world.setBlockState(pos, state.with(CLICK_TIMER, clickTimer + 1), 0);
    }

    public boolean checkForOverpower(World world, BlockPos pos) {
        int numPotentialDevicesToPower = 0;

        for (Direction direction : Direction.values()) {
            if (direction.equals(Direction.UP)) {
                continue;
            }

            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            Block neighborBlock = neighborState.getBlock();
            if (neighborBlock instanceof MechPowerBlockBase mechNeighborBlock) {
                if (mechNeighborBlock.getValidHandCrankFaces(neighborState, neighborPos).contains(pos)) {
                    numPotentialDevicesToPower++;
                }
            }
        }

        return numPotentialDevicesToPower > 1;
    }

    public void playClick(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1.0f,  2.0f);
    }

    public void breakWithDrop(World world, BlockPos pos) {
        BlockState airState = Blocks.AIR.getDefaultState();
        world.setBlockState(pos, airState);
        Vec3d centerPos = pos.toCenterPos();
        world.playSoundAtBlockCenter(pos, BetterWithTime.MECH_BANG_SOUND, SoundCategory.BLOCKS, 0.5f, 1, false);
        for (Item item : new Item[]{Items.STICK, Items.STONE, BwtItems.gearItem}) {
            ItemEntity itemEntity = new ItemEntity(world, centerPos.x, centerPos.y, centerPos.z, item.getDefaultStack());
            itemEntity.setVelocity(world.random.nextDouble() * -0.01 + 0.02, 0.2, world.random.nextDouble() * -0.01 + 0.02);
            world.spawnEntity(itemEntity);
        }
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }
}
