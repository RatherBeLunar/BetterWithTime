package com.bwt.blocks.dirt_slab;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DirtSlabBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape BOTTOM_SHAPE, SNOW_LAYER;
    public static final BooleanProperty SNOWY;
    public Block fullBlock = Blocks.DIRT;

    public static boolean isPlayerLookingAtSnowLayer(Player playerEntity, BlockPos pos) {
        float tickDelta = 0;
        double maxDistance = playerEntity.blockInteractionRange();
        Vec3 vec3d = playerEntity.getEyePosition(tickDelta);
        Vec3 vec3d2 = playerEntity.getViewVector(tickDelta);
        Vec3 vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
        BlockHitResult hit = SNOW_LAYER.clip(vec3d, vec3d3, pos);
        return hit != null;
    }

    public DirtSlabBlock(Properties settings) {
        super(settings);
        registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false).setValue(SNOWY, false));
    }

    public DirtSlabBlock(Properties settings, Block fullBlock) {
        this(settings);
        this.fullBlock = fullBlock;
    }

    public static final MapCodec<Block> CODEC = Block.simpleCodec(DirtSlabBlock::new);

    @Override
    public MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, SNOWY);
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getVisualShape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BOTTOM_SHAPE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(SNOWY)) {
            if (context instanceof EntityCollisionContext ec) {
                Entity entity = ec.getEntity();
                if (entity instanceof Player playerEntity) {
                    if (isPlayerLookingAtSnowLayer(playerEntity, pos)) {
                        return SNOW_LAYER;
                    }
                }
            }
        }
        return BOTTOM_SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = context.getLevel().getBlockState(blockPos);
        // No double slab, just convert back to full block
        if (blockState.is(this)) {
            return fullBlock.defaultBlockState();
        }
        FluidState fluidState = context.getLevel().getFluidState(blockPos);
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.getValue(BlockStateProperties.WATERLOGGED) || fluidState.getType() != Fluids.WATER) {
            return false;
        }
        if (!level.isClientSide()) {
            // The key difference from the default Waterloggable behavior here is that we unset the snowy property
            level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, Boolean.TRUE).setValue(SNOWY, false), Block.UPDATE_ALL);
            level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
        }
        return true;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }


    static {
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            if (state.getBlock() instanceof DirtSlabBlock && state.getValue(DirtSlabBlock.SNOWY) && !player.isCreative()) {
                if (isPlayerLookingAtSnowLayer(player, pos)) {
                    level.setBlockAndUpdate(pos, state.setValue(DirtSlabBlock.SNOWY, false));
                    BlockState snowLayer = Blocks.SNOW.defaultBlockState();
                    ItemStack itemStack = player.getMainHandItem();
                    ItemStack itemStack2 = itemStack.copy();
                    itemStack.mineBlock(level, snowLayer, pos, player);
                    dropResources(snowLayer, level, pos, null, player, itemStack2);
                    return false;
                }
            }
            return true;

        });
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (enableSnow() && state.getValue(SNOWY)) {
            HitResult hit = player.pick(player.blockInteractionRange(), 0, false);
            if (hit instanceof BlockHitResult blockHitResult) {
                Direction direction = blockHitResult.getDirection();
                if (direction == Direction.UP) {
                    return state.setValue(SNOWY, false);
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Direction direction = hit.getDirection();
        if (enableSnow() && itemStack.is(Blocks.SNOW.asItem()) && direction == Direction.UP && !state.getValue(SNOWY)) {
            level.setBlock(pos, state.setValue(SNOWY, true), 11);
            var soundGroup = Blocks.SNOW.defaultBlockState().getSoundType();
            level.playSound(player, pos, soundGroup.getPlaceSound(), SoundSource.BLOCKS, (soundGroup.getVolume() + 1.0F) / 2.0F, soundGroup.getPitch() * 0.8F);
            level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, Blocks.SNOW.defaultBlockState()));
            itemStack.consume(1, player);
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hit);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        ItemStack itemStack = context.getItemInHand();
        if (itemStack.is(this.asItem()) && context.replacingClickedOnBlock()) {
            boolean bl = context.getClickLocation().y - (double) context.getClickedPos().getY() > 0.5;
            Direction direction = context.getClickedFace();
            return direction == Direction.UP || bl && direction.getAxis().isHorizontal();
        }
        return false;
    }

    @Override
    public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {

        if (this.enableSnow() && !state.getValue(SNOWY) && precipitation.equals(Biome.Precipitation.SNOW) && level.getBrightness(LightLayer.BLOCK, pos) <= 11) {
            level.setBlock(pos, state.setValue(SNOWY, true), 11);
        }
        super.handlePrecipitation(state, level, pos, precipitation);
    }

    public boolean enableSnow() {
        return true;
    }

    protected void meltSnowFromLight(Level level, BlockPos pos, BlockState state) {
        if (level.getBrightness(LightLayer.BLOCK, pos) > 11) {
            level.setBlockAndUpdate(pos, state.setValue(SNOWY, false));
        }
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return super.isRandomlyTicking(state) || state.getValue(SNOWY);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        meltSnowFromLight(level, pos, state);
    }

    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        SNOWY = BlockStateProperties.SNOWY;
        BOTTOM_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
        SNOW_LAYER = Block.box(0.0, 8.0, 0.0, 16.0, 10.0, 16.0);
    }
}