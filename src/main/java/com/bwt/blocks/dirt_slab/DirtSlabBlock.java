package com.bwt.blocks.dirt_slab;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

public class DirtSlabBlock extends Block implements Waterloggable {
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape BOTTOM_SHAPE, SNOW_LAYER;
    public static final BooleanProperty SNOWY;
    public Block fullBlock = Blocks.DIRT;

    public static boolean isPlayerLookingAtSnowLayer(PlayerEntity playerEntity, BlockPos pos) {
        float tickDelta = 0;
        double maxDistance = playerEntity.getBlockInteractionRange();
        Vec3d vec3d = playerEntity.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = playerEntity.getRotationVec(tickDelta);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
        BlockHitResult hit = SNOW_LAYER.raycast(vec3d, vec3d3, pos);
        return hit != null;
    }

    public DirtSlabBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState().with(WATERLOGGED, false).with(SNOWY, false));
    }

    public DirtSlabBlock(Settings settings, Block fullBlock) {
        this(settings);
        this.fullBlock = fullBlock;
    }

    public static final MapCodec<Block> CODEC = Block.createCodec(DirtSlabBlock::new);

    @Override
    public MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, SNOWY);
    }

    @Override
    protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return super.getCameraCollisionShape(state, world, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BOTTOM_SHAPE;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(SNOWY)) {
            if (context instanceof EntityShapeContext ec) {
                Entity entity = ec.getEntity();
                if (entity instanceof PlayerEntity playerEntity) {
                    if (isPlayerLookingAtSnowLayer(playerEntity, pos)) {
                        return SNOW_LAYER;
                    }
                }
            }
        }
        return BOTTOM_SHAPE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = context.getWorld().getBlockState(blockPos);
        // No double slab, just convert back to full block
        if (blockState.isOf(this)) {
            return fullBlock.getDefaultState();
        }
        FluidState fluidState = context.getWorld().getFluidState(blockPos);
        return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.get(Properties.WATERLOGGED) || fluidState.getFluid() != Fluids.WATER) {
            return false;
        }
        if (!world.isClient()) {
            // The key difference from the default Waterloggable behavior here is that we unset the snowy property
            world.setBlockState(pos, state.with(Properties.WATERLOGGED, Boolean.TRUE).with(SNOWY, false), Block.NOTIFY_ALL);
            world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
        }
        return true;
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }


    static {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (state.getBlock() instanceof DirtSlabBlock && state.get(DirtSlabBlock.SNOWY) && !player.isCreative()) {
                if (isPlayerLookingAtSnowLayer(player, pos)) {
                    world.setBlockState(pos, state.with(DirtSlabBlock.SNOWY, false));
                    BlockState snowLayer = Blocks.SNOW.getDefaultState();
                    ItemStack itemStack = player.getMainHandStack();
                    ItemStack itemStack2 = itemStack.copy();
                    itemStack.postMine(world, snowLayer, pos, player);
                    dropStacks(snowLayer, world, pos, null, player, itemStack2);
                    return false;
                }
            }
            return true;

        });
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (enableSnow() && state.get(SNOWY)) {
            HitResult hit = player.raycast(player.getBlockInteractionRange(), 0, false);
            if (hit instanceof BlockHitResult blockHitResult) {
                Direction direction = blockHitResult.getSide();
                if (direction == Direction.UP) {
                    return state.with(SNOWY, false);
                }
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack itemStack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Direction direction = hit.getSide();
        if (enableSnow() && itemStack.isOf(Blocks.SNOW.asItem()) && direction == Direction.UP && !state.get(SNOWY)) {
            world.setBlockState(pos, state.with(SNOWY, true), 11);
            var soundGroup = Blocks.SNOW.getDefaultState().getSoundGroup();
            world.playSound(player, pos, soundGroup.getPlaceSound(), SoundCategory.BLOCKS, (soundGroup.getVolume() + 1.0F) / 2.0F, soundGroup.getPitch() * 0.8F);
            world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(player, Blocks.SNOW.getDefaultState()));
            itemStack.decrementUnlessCreative(1, player);
            return ItemActionResult.SUCCESS;
        }
        return super.onUseWithItem(itemStack, state, world, pos, player, hand, hit);
    }

    @Override
    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        ItemStack itemStack = context.getStack();
        if (itemStack.isOf(this.asItem()) && context.canReplaceExisting()) {
            boolean bl = context.getHitPos().y - (double) context.getBlockPos().getY() > 0.5;
            Direction direction = context.getSide();
            return direction == Direction.UP || bl && direction.getAxis().isHorizontal();
        }
        return false;
    }

    @Override
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {

        if (this.enableSnow() && !state.get(SNOWY) && precipitation.equals(Biome.Precipitation.SNOW) && world.getLightLevel(LightType.BLOCK, pos) <= 11) {
            world.setBlockState(pos, state.with(SNOWY, true), 11);
        }
        super.precipitationTick(state, world, pos, precipitation);
    }

    public boolean enableSnow() {
        return true;
    }

    protected void meltSnowFromLight(World world, BlockPos pos, BlockState state) {
        if (world.getLightLevel(LightType.BLOCK, pos) > 11) {
            world.setBlockState(pos, state.with(SNOWY, false));
        }
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return super.hasRandomTicks(state) || state.get(SNOWY);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        meltSnowFromLight(world, pos, state);
    }

    static {
        WATERLOGGED = Properties.WATERLOGGED;
        SNOWY = Properties.SNOWY;
        BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
        SNOW_LAYER = Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 10.0, 16.0);
    }
}