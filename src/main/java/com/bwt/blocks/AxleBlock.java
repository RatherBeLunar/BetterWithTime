package com.bwt.blocks;

import com.bwt.items.BwtItems;
import com.bwt.mechanical.api.ArcProvider;
import com.bwt.mechanical.api.digraph.Arc;
import com.bwt.mechanical.impl.Axle;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AxleBlock extends PillarBlock implements ArcProvider {


    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0f, 6f, 6f, 16f, 10f, 10f);
    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(6f, 0f, 6f, 10f, 16f, 10f);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6f, 6f, 0f, 10f, 10f, 16f);

    private final Axle arc;

    public AxleBlock(Settings settings) {
        super(settings);
        this.arc = new Axle() {
            @Override
            public Direction.Axis getAxis(World world, BlockPos pos, BlockState blockState) {
                return blockState.get(AXIS);
            }
        };
        this.setDefaultState(this.getDefaultState().with(AXIS, Direction.Axis.Z).with(Axle.MECH_POWER, 0));
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        updatePowerStates(state, world, pos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.scheduleBlockTick(pos, this, 1);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        Axle.appendProperties(builder);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        Direction.Axis axis = state.get(AXIS);
        return switch (axis) {
            case X -> X_SHAPE;
            case Y -> Y_SHAPE;
            case Z -> Z_SHAPE;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return super.getCollisionShape(state, world, pos, context);
    }

    public BlockState getNextOrientation(BlockState blockState) {
        return blockState.with(AXIS, switch (blockState.get(AXIS)) {
            case X -> Direction.Axis.Z;
            case Z -> Direction.Axis.Y;
            case Y -> Direction.Axis.X;
        });
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.getMainHandStack().isEmpty()) {
            return ActionResult.PASS;
        }
        BlockState updatedState = getNextOrientation(state);
        world.setBlockState(pos, updatedState);
        world.playSound(null, pos, updatedState.getSoundGroup().getPlaceSound(),
                SoundCategory.BLOCKS, 0.25f, world.random.nextFloat() * 0.25F + 0.25F);

        updatePowerStates(updatedState, world, pos);


        return ActionResult.SUCCESS;
    }

    public void breakAxle(World world, BlockPos pos) {
        world.removeBlock(pos, false);
        world.playSound(null, pos, BwtSoundEvents.MECH_EXPLODE, SoundCategory.BLOCKS, 0.5f, 1);
        dropStack(world, pos, Items.STICK.getDefaultStack());
        dropStack(world, pos, BwtItems.hempFiberItem.getDefaultStack());
    }


    public void updatePowerStates(BlockState state, World world, BlockPos pos) {

        var response = this.arc.calculatePowerLevel(world, state, pos);
        var powerState = response.state();
        var newPowerLevel = response.power();
        if (powerState == Axle.PowerState.UNPOWERED || powerState == Axle.PowerState.POWERED) {
            world.setBlockState(pos, state.with(Axle.MECH_POWER, newPowerLevel));
        } else if (powerState == Axle.PowerState.OVERPOWERED) {
            breakAxle(world, pos);
        }
    }


    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        updatePowerStates(state, world, pos);
    }


    public Direction.Axis getAxis(BlockState state) {
        return state.get(AXIS);
    }

    @Override
    public Arc getArc() {
        return this.arc;
    }
}
