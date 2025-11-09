package com.bwt.blocks;

import com.bwt.sounds.BwtSoundEvents;
import com.bwt.utils.RadiusAroundBlockStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class BellowsBlock extends Block implements MechPowerBlockBase {
    public static DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final float compressedHeight = 11;
    protected static final int tickRate = 37;

    protected static final VoxelShape COMPRESSED_SHAPE = Block.createCuboidShape(0f, 0f, 0f, 16f, compressedHeight, 16f);

    public BellowsBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(MECH_POWERED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return state.get(MECH_POWERED) ? COMPRESSED_SHAPE : VoxelShapes.fullCube();
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MECH_POWERED, FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public Predicate<Direction> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return direction -> direction != blockState.get(FACING) && direction != Direction.UP;
    }

    @Override
    public Predicate<Direction> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return direction -> direction.getAxis().isHorizontal();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        schedulePowerUpdate(state, world, pos);
    }

    public void schedulePowerUpdate(BlockState state, World world, BlockPos pos) {
        if (isReceivingMechPower(world, state, pos) != isMechPowered(state)) {
            world.scheduleBlockTick(pos, this, tickRate);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        schedulePowerUpdate(state, world, pos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean isReceivingMechPower = isReceivingMechPower(world, state, pos);
        if (isReceivingMechPower == isMechPowered(state)) {
            return;
        }
        world.setBlockState(pos, state.with(MECH_POWERED, isReceivingMechPower));
        world.playSound(null, pos, BwtSoundEvents.BELLOWS_COMPRESS, SoundCategory.BLOCKS, 0.25f, random.nextFloat() * 0.1f + 0.2f);
        if (isReceivingMechPower) {
            stokeFire(world, pos, state);
        }
        else {
            liftEntities(world, pos);
        }
    }

    public void stokeFire(World world, BlockPos pos, BlockState state) {
        BlockPos center = pos.offset(state.get(FACING), 2);
        RadiusAroundBlockStream.allBlocksInHorizontalRadius(center, 1).forEach(firePos -> {
            BlockPos hibachiPos = firePos.down();
            BlockState fireState = world.getBlockState(firePos);
            if (!fireState.isIn(BlockTags.FIRE)) {
                return;
            }
            BlockState hibachiState = world.getBlockState(hibachiPos);
            if (!hibachiState.isOf(BwtBlocks.hibachiBlock)) {
                return;
            }
            world.setBlockState(firePos, BwtBlocks.stokedFireBlock.getPlacementState(world, firePos), Block.NOTIFY_ALL);
        });
    }

    public void liftEntities(ServerWorld world, BlockPos pos) {
        Box intersectionBox = new Box(0.01, 0.5, 0.01, 0.99, 0.99, 0.99).offset(pos);
        List<Entity> list =  world.getEntitiesByClass(
                Entity.class,
                intersectionBox,
                EntityPredicates.EXCEPT_SPECTATOR
        );
        list.stream().filter(Entity::isPushable).forEach(entity -> {
            entity.setOnGround(false);
            entity.setPosition(entity.getX(), pos.getY() + 1, entity.getZ());
            if(entity instanceof ServerPlayerEntity){
                ((ServerPlayerEntity) entity).networkHandler.sendPacket(new EntityPositionS2CPacket(entity));
            }
        });
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(FACING, mirror.apply(state.get(FACING)));
    }
}
