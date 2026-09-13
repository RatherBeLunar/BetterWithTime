package com.bwt.blocks;

import com.bwt.sounds.BwtSoundEvents;
import com.bwt.utils.RadiusAroundBlockStream;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BellowsBlock extends Block implements MechPowerBlockBase {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final float compressedHeight = 11;
    protected static final int tickRate = 37;

    protected static final VoxelShape COMPRESSED_SHAPE = Block.box(0f, 0f, 0f, 16f, compressedHeight, 16f);

    public BellowsBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(MECH_POWERED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return state.getValue(MECH_POWERED) ? COMPRESSED_SHAPE : Shapes.block();
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MECH_POWERED, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public Predicate<Direction> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return direction -> direction != blockState.getValue(FACING) && direction != Direction.UP;
    }

    @Override
    public Predicate<Direction> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return direction -> direction.getAxis().isHorizontal();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);
        schedulePowerUpdate(state, level, pos);
    }

    public void schedulePowerUpdate(BlockState state, Level level, BlockPos pos) {
        if (isReceivingMechPower(level, state, pos) != isMechPowered(state)) {
            level.scheduleTick(pos, this, tickRate);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (level.isClientSide) {
            return;
        }
        schedulePowerUpdate(state, level, pos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean isReceivingMechPower = isReceivingMechPower(level, state, pos);
        if (isReceivingMechPower == isMechPowered(state)) {
            return;
        }
        level.setBlockAndUpdate(pos, state.setValue(MECH_POWERED, isReceivingMechPower));
        level.playSound(null, pos, BwtSoundEvents.BELLOWS_COMPRESS, SoundSource.BLOCKS, 0.25f, random.nextFloat() * 0.1f + 0.2f);
        if (isReceivingMechPower) {
            stokeFire(level, pos, state);
        }
        else {
            liftEntities(level, pos);
        }
    }

    public void stokeFire(Level level, BlockPos pos, BlockState state) {
        BlockPos center = pos.relative(state.getValue(FACING), 2);
        RadiusAroundBlockStream.allBlocksInHorizontalRadius(center, 1).forEach(firePos -> {
            BlockPos hibachiPos = firePos.below();
            BlockState fireState = level.getBlockState(firePos);
            if (!fireState.is(BlockTags.FIRE)) {
                return;
            }
            BlockState hibachiState = level.getBlockState(hibachiPos);
            if (!hibachiState.is(BwtBlocks.hibachiBlock)) {
                return;
            }
            level.setBlock(firePos, BwtBlocks.stokedFireBlock.getPlacementState(level, firePos), Block.UPDATE_ALL);
        });
    }

    public void liftEntities(ServerLevel level, BlockPos pos) {
        AABB intersectionBox = new AABB(0.01, 0.5, 0.01, 0.99, 0.99, 0.99).move(pos);
        List<Entity> list =  level.getEntitiesOfClass(
                Entity.class,
                intersectionBox,
                EntitySelector.NO_SPECTATORS
        );
        list.stream().filter(Entity::isPushable).forEach(entity -> {
            entity.setOnGround(false);
            entity.setPos(entity.getX(), pos.getY() + 1, entity.getZ());
            if(entity instanceof ServerPlayer){
                ((ServerPlayer) entity).connection.send(new ClientboundTeleportEntityPacket(entity));
            }
        });
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }
}
