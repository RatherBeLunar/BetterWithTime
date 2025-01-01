package com.bwt.blocks;

import com.bwt.mechanical.api.MechPowered;
import com.bwt.mechanical.api.PowerState;
import com.bwt.mechanical.impl.MachineBlock;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BellowsBlock extends Block implements MachineBlock {
    public static DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final float compressedHeight = 11;
    protected static final int tickRate = 37;

    protected static final VoxelShape COMPRESSED_SHAPE = Block.createCuboidShape(0f, 0f, 0f, 16f, compressedHeight, 16f);

    public BellowsBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(MechPowered.MECH_POWERED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return isPowered(state) ? COMPRESSED_SHAPE : VoxelShapes.fullCube();
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        MechPowered.appendProperties(builder);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void onPowerChanged(PowerState powerState) {
        var world = powerState.world();
        var pos = powerState.pos();
        var random = powerState.random();
        world.playSound(null, pos, BwtSoundEvents.BELLOWS_COMPRESS, SoundCategory.BLOCKS, 0.25f, random.nextFloat() * 0.1f + 0.2f);
        if (powerState.isPowered()) {
            stokeFire(world, pos, powerState.state());
        }
    }

    public void stokeFire(World world, BlockPos pos, BlockState state) {
        BlockPos center = pos.offset(state.get(FACING), 2);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos firePos = center.offset(Direction.Axis.X, x).offset(Direction.Axis.Z, z);
                BlockPos hibachiPos = firePos.down();
                BlockState fireState = world.getBlockState(firePos);
                if (!fireState.isIn(BlockTags.FIRE)) {
                    continue;
                }
                BlockState hibachiState = world.getBlockState(hibachiPos);
                if (!hibachiState.isOf(BwtBlocks.hibachiBlock)) {
                    continue;
                }
                world.setBlockState(firePos, BwtBlocks.stokedFireBlock.getPlacementState(world, firePos), Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public List<Direction> getInputFaces(World world, BlockPos pos, BlockState blockState) {
        return List.of(Direction.DOWN);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        world.scheduleBlockTick(pos, this, tickRate);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        this.onUpdate(state, world, pos, random);
    }


    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        schedulePowerUpdate(state, world, pos);
    }
}
