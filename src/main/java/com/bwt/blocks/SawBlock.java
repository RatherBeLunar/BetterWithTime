package com.bwt.blocks;

import com.bwt.damage_types.BwtDamageTypes;
import com.bwt.items.BwtItems;
import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.SawRecipe;
import com.bwt.tags.BwtBlockTags;
import com.bwt.utils.BlockUtils;
import com.bwt.utils.CustomItemScatterer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class SawBlock extends Block implements MechPowerBlockBase {
    public static final DirectionProperty FACING = Properties.FACING;


    private static final int m_iPowerChangeTickRate = 10;

    private static final int m_iSawTimeBaseTickRate = 15;
    private static final int m_iSawTimeTickRateVariance = 4;

    // This base height prevents chickens slipping through grinders, while allowing items to pass

    public static final float m_fBaseHeight = 16f - 4f;

    public static final float m_fBladeLength = 10f;
    public static final float m_fBladeHalfLength = m_fBladeLength * 0.5F;

    public static final float m_fBladeWidth = 0.25f;
    public static final float m_fBladeHalfWidth = m_fBladeWidth * 0.5F;

    public static final float m_fBladeHeight = 16F - m_fBaseHeight;
    protected static final Box UPWARD_BASE_BOX = new Box(0f, 0f, 0f, 16f, m_fBaseHeight, 16F);
    protected static final Box UPWARD_BLADE_BOX = new Box(8f - m_fBladeHalfLength, m_fBaseHeight, 8f - m_fBladeHalfWidth, 8f + m_fBladeHalfLength, m_fBaseHeight + m_fBladeHeight, 8f + m_fBladeHalfWidth);
    protected static final Box DOWNWARD_BLADE_BOX = new Box(8f - m_fBladeHalfLength, 0, 8f - m_fBladeHalfWidth, 8f + m_fBladeHalfLength, m_fBladeHeight, 8f + m_fBladeHalfWidth);
    protected static final Box NORTH_BLADE_BOX = new Box(
            8f - m_fBladeHalfLength, 8f - m_fBladeHalfWidth, 16f - m_fBaseHeight,
            8f + m_fBladeHalfLength, 8f + m_fBladeHalfWidth, 16f - m_fBaseHeight - m_fBladeHeight
    );
    protected static final Box SOUTH_BLADE_BOX = new Box(
            8f - m_fBladeHalfLength, 8f - m_fBladeHalfWidth, m_fBaseHeight,
            8f + m_fBladeHalfLength, 8f + m_fBladeHalfWidth, m_fBaseHeight + m_fBladeHeight
    );
    protected static final Box EAST_BLADE_BOX = new Box(
            16f - m_fBaseHeight, 8f - m_fBladeHalfWidth, 8f - m_fBladeHalfLength,
            16f - m_fBaseHeight - m_fBladeHeight, 8f + m_fBladeHalfWidth, 8f + m_fBladeHalfLength
    );
    protected static final Box WEST_BLADE_BOX = new Box(
            (m_fBaseHeight), 8f - m_fBladeHalfWidth, 8f - m_fBladeHalfLength,
            m_fBaseHeight + m_fBladeHeight, 8f + m_fBladeHalfWidth, 8f + m_fBladeHalfLength
    );

    protected static final List<VoxelShape> COLLISION_SHAPES = Arrays.stream(Direction.values())
            .map(direction -> BlockUtils.rotateCuboidFromUp(direction, UPWARD_BASE_BOX))
            .toList();

    protected static final List<VoxelShape> BLADE_SHAPES = Stream.of(
            DOWNWARD_BLADE_BOX,
            UPWARD_BLADE_BOX,
            NORTH_BLADE_BOX,
            SOUTH_BLADE_BOX,
            EAST_BLADE_BOX,
            WEST_BLADE_BOX
    ).map(box -> Block.createCuboidShape(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)).toList();

    protected static final List<VoxelShape> OUTLINE_SHAPES = Arrays.stream(Direction.values())
            .map(direction -> BlockUtils.rotateCuboidFromUp(direction, UPWARD_BASE_BOX)).toList();

    public SawBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        MechPowerBlockBase.super.appendProperties(builder);
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite()).with(MECH_POWERED, false);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        // note that we can't validate if the update is required here as the block will have
        // its facing set after being added
        world.scheduleBlockTick(pos, this, m_iPowerChangeTickRate);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPES.get(state.get(FACING).getId());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPES.get(state.get(FACING).getId());
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        scheduleUpdateIfRequired(world, state, pos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        super.scheduledTick(state, world, pos, random);

        boolean bReceivingPower = isReceivingMechPower(world, state, pos);
        boolean bOn = isMechPowered(state);

        if (bOn != bReceivingPower) {
            emitSawParticles(world, state, pos);

            world.setBlockState(pos, state.with(MECH_POWERED, bReceivingPower));

            if (bReceivingPower) {
                playBangSound(world, pos);
                // the saw doesn't cut on the update in which it is powered, so check if another
                // update is required
                scheduleUpdateIfRequired(world, state, pos);
            }
        }
        else if (bOn) {
            sawBlockToFront(world, state, pos);
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        if (!isMechPowered(state)) {
            return;
        }
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        // construct bounding box from saw
        if (BLADE_SHAPES.get(state.get(FACING).getId()).getBoundingBox().offset(pos)
                .intersects(livingEntity.getBoundingBox(entity.getPose()).offset(livingEntity.getPos()))) {

            DamageSource damageSource = BwtDamageTypes.of(world, BwtDamageTypes.SAW_DAMAGE_TYPE);
            livingEntity.damage(damageSource, 4.0f);
        }
    }

    public void dropItemsOnBreak(World world, BlockPos pos) {
        ItemScatterer.spawn(world, pos, DefaultedList.copyOf(
                ItemStack.EMPTY,
                new ItemStack(BwtItems.gearItem, 1),
                new ItemStack(Items.STICK, 2),
                new ItemStack(BwtItems.sawDustItem, 2),
                new ItemStack(Items.IRON_INGOT, 2),
                new ItemStack(BwtItems.strapItem, 2)
        ));
    }

    protected void scheduleUpdateIfRequired(World world, BlockState state, BlockPos pos) {
        if (isMechPowered(state) != isReceivingMechPower(world, state, pos)) {
            world.scheduleBlockTick(pos, this, m_iPowerChangeTickRate);
            return;
        }
        if (!isMechPowered(state)) {
            return;
        }

        // check if we have something to cut in front of us
        BlockPos targetPos = pos.offset(state.get(FACING));
        BlockState targetState = world.getBlockState(targetPos);
        if (!targetState.isAir()) {
            world.scheduleBlockTick(pos, this, m_iSawTimeBaseTickRate + world.random.nextInt(m_iSawTimeTickRateVariance));
        }
    }


    void emitSawParticles(World world, BlockState state, BlockPos pos) {
        // compute position of saw blade
        Direction facing = state.get(FACING);
        VoxelShape bladeFace = BLADE_SHAPES.get(facing.getId()).asCuboid().offset(pos.getX(), pos.getY(), pos.getZ());
        double bladeMaxX = bladeFace.getMax(Direction.Axis.X);
        double bladeMaxY = bladeFace.getMax(Direction.Axis.Y);
        double bladeMaxZ = bladeFace.getMax(Direction.Axis.Z);
        double bladeMinX = bladeFace.getMin(Direction.Axis.X);
        double bladeMinY = bladeFace.getMin(Direction.Axis.Y);
        double bladeMinZ = bladeFace.getMin(Direction.Axis.Z);
        double fBladeXPos = (bladeMaxX + bladeMinX) / 2;
        double fBladeYPos = (bladeMaxY + bladeMinY) / 2;
        double fBladeZPos = (bladeMaxZ + bladeMinZ) / 2;

        for (int counter = 0; counter < 5; counter++) {
            double smokeX = fBladeXPos + ((world.random.nextFloat() - 0.5f) * (bladeMaxX - bladeMinX));
            double smokeY = fBladeYPos + ((world.random.nextFloat() * 0.10f) * (bladeMaxY - bladeMinY));
            double smokeZ = fBladeZPos + ((world.random.nextFloat() - 0.5f) * (bladeMaxZ - bladeMinZ));
            world.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0d, 0d, 0d);
        }
    }

    protected void sawBlockToFront(World world, BlockState state, BlockPos pos) {
        BlockPos targetPos = pos.offset(state.get(FACING));
        BlockState targetState = world.getBlockState(targetPos);

        if (targetState.isAir()) {
            return;
        }
        RecipeManager recipeManager = world.getRecipeManager();
        Optional<SawRecipe> recipe = recipeManager.listAllOfType(BwtRecipes.SAW_RECIPE_TYPE).stream()
                .map(RecipeEntry::value)
                .filter(sawRecipeRecipe -> sawRecipeRecipe.matches(targetState.getBlock()))
                .findFirst();
        // Cutting
        if (recipe.isEmpty()) {
            if (targetState.isIn(BwtBlockTags.SAW_BREAKS_NO_DROPS)) {
                world.breakBlock(targetPos, false);
                return;
            }
            if (targetState.isIn(BwtBlockTags.SAW_BREAKS_DROPS_LOOT)) {
                world.breakBlock(targetPos, true);
                return;
            }
            if (!targetState.isIn(BwtBlockTags.SURVIVES_SAW_BLOCK)) {
                breakSaw(world, pos);
            }
            return;
        }

        List<ItemStack> results = recipe.get().getResults();
        BlockIngredient blockIngredient = recipe.get().getIngredient();

        // The companion slab is the only partial block that doesn't just get cut regardless of collision
        if (blockIngredient.test(BwtBlocks.companionSlabBlock) && state.get(FACING).getAxis().isHorizontal()) {
            return;
        }

        if (blockIngredient.test(BwtBlocks.companionCubeBlock)) {
            world.playSound(null, pos, SoundEvents.ENTITY_WOLF_DEATH, SoundCategory.BLOCKS, 1, 1);
            if (state.get(FACING).getAxis().isHorizontal()) {
                results.get(0).setCount(1);
                world.setBlockState(targetPos, BwtBlocks.companionSlabBlock.getDefaultState());
            }
        } else {
            world.breakBlock(targetPos, false);
        }
        playBangSound(world, pos);

        if (targetState.contains(Properties.SLAB_TYPE) && targetState.get(Properties.SLAB_TYPE).equals(SlabType.DOUBLE)) {
            results.forEach(stack -> stack.setCount(stack.getCount() * 2));
        }

        CustomItemScatterer.spawn(world, targetPos, DefaultedList.copyOf(ItemStack.EMPTY, results.toArray(new ItemStack[0])));
    }

    public void breakSaw(World world, BlockPos pos) {
        dropItemsOnBreak(world, pos);
        world.breakBlock(pos, false);
        playBangSound(world, pos, 1);
    }

    @Override
    public List<BlockPos> getValidAxleInputFaces(BlockState state, BlockPos pos) {
        return Arrays.stream(Direction.values())
                .filter(direction -> !direction.equals(state.get(FACING)))
                .map(pos::offset)
                .toList();
    }

    @Override
    public List<BlockPos> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return List.of();
    }

//    @Override
//    public void overpower(World world, BlockPos pos) {
//        breakSaw(world, pos);
//    }

    //----------- Client Side Functionality -----------//


    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (isMechPowered(state)) {
            emitSawParticles(world, state, pos);
        }
    }
}