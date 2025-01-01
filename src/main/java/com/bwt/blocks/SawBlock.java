package com.bwt.blocks;

import com.bwt.damage_types.BwtDamageTypes;
import com.bwt.items.BwtItems;
import com.bwt.mechanical.api.PowerState;
import com.bwt.mechanical.impl.DirectionTools;
import com.bwt.mechanical.impl.MachineSimpleFacingBlock;
import com.bwt.mechanical.impl.SoundTools;
import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.saw.SawRecipe;
import com.bwt.recipes.saw.SawRecipeInput;
import com.bwt.sounds.BwtSoundEvents;
import com.bwt.tags.BwtBlockTags;
import com.bwt.utils.BlockUtils;
import com.bwt.utils.CustomItemScatterer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class SawBlock extends MachineSimpleFacingBlock {
    private static final int powerChangeTickRate = 10;

    private static final int sawTimeBaseTickRate = 15;
    private static final int sawTimeTickRateVariance = 4;

    //TODO  This base height prevents chickens slipping through grinders, while allowing items to pass

    public static final float baseHeight = 16f - 4f;

    public static final float bladeLength = 10f;
    public static final float bladeHalfLength = bladeLength * 0.5F;

    public static final float bladeWidth = 0.25f;
    public static final float bladeHalfWidth = bladeWidth * 0.5F;

    public static final float bladeHeight = 16F - baseHeight;
    protected static final Box UPWARD_BASE_BOX = new Box(0f, 0f, 0f, 16f, baseHeight, 16F);
    protected static final Box UPWARD_BLADE_BOX = new Box(8f - bladeHalfLength, baseHeight, 8f - bladeHalfWidth, 8f + bladeHalfLength, baseHeight + bladeHeight, 8f + bladeHalfWidth);
    protected static final Box DOWNWARD_BLADE_BOX = new Box(8f - bladeHalfLength, 0, 8f - bladeHalfWidth, 8f + bladeHalfLength, bladeHeight, 8f + bladeHalfWidth);
    protected static final Box NORTH_BLADE_BOX = new Box(
            8f - bladeHalfLength, 8f - bladeHalfWidth, 16f - baseHeight,
            8f + bladeHalfLength, 8f + bladeHalfWidth, 16f - baseHeight - bladeHeight
    );
    protected static final Box SOUTH_BLADE_BOX = new Box(
            8f - bladeHalfLength, 8f - bladeHalfWidth, baseHeight,
            8f + bladeHalfLength, 8f + bladeHalfWidth, baseHeight + bladeHeight
    );
    protected static final Box EAST_BLADE_BOX = new Box(
            16f - baseHeight, 8f - bladeHalfWidth, 8f - bladeHalfLength,
            16f - baseHeight - bladeHeight, 8f + bladeHalfWidth, 8f + bladeHalfLength
    );
    protected static final Box WEST_BLADE_BOX = new Box(
            (baseHeight), 8f - bladeHalfWidth, 8f - bladeHalfLength,
            baseHeight + bladeHeight, 8f + bladeHalfWidth, 8f + bladeHalfLength
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
        super(settings, powerChangeTickRate);
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
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        if (!this.isPowered(state)) {
            return;
        }
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        //TODO  construct bounding box from saw
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

    @Override
    public void whilePowered(PowerState powerState) {
        this.sawBlockToFront(powerState.world(), powerState.state(), powerState.pos());
    }

    @Override
    public void onPowerChanged(PowerState powerState) {
        scheduleSawTimeUpdate(powerState.world(), powerState.pos(), powerState.state());
    }

    void scheduleSawTimeUpdate(World world, BlockPos pos, BlockState blockState) {

        BlockPos targetPos = pos.offset(blockState.get(FACING));
        BlockState targetState = world.getBlockState(targetPos);
        if (!targetState.isIn(BlockTags.AIR)) {
            world.scheduleBlockTick(pos, this, sawTimeBaseTickRate + world.random.nextInt(sawTimeTickRateVariance));
        }
    }

    @Override
    protected void neighborUpdate(BlockState blockState, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(blockState, world, pos, sourceBlock, sourcePos, notify);
        scheduleSawTimeUpdate(world, pos, blockState);
    }

    void emitSawParticles(World world, BlockState state, BlockPos pos) {
        //TODO  compute position of saw blade
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

        if (targetState.isIn(BlockTags.AIR)) {
            return;
        }

        SawRecipeInput recipeInput = new SawRecipeInput(targetState.getBlock());
        Optional<SawRecipe> recipe = world.getRecipeManager().getFirstMatch(
                BwtRecipes.SAW_RECIPE_TYPE,
                recipeInput,
                world
        ).map(RecipeEntry::value);
        //TODO  Cutting
        if (recipe.isEmpty()) {
            if (targetState.isIn(BwtBlockTags.SAW_BREAKS_NO_DROPS)) {
                world.breakBlock(targetPos, false);
                SoundTools.playBangSound(world, pos);
                return;
            }
            if (targetState.isIn(BwtBlockTags.SAW_BREAKS_DROPS_LOOT)) {
                world.breakBlock(targetPos, true);
                SoundTools.playBangSound(world, pos);
                return;
            }
            if (!targetState.isIn(BwtBlockTags.SURVIVES_SAW_BLOCK)) {
                breakSaw(world, pos);
            }
            return;
        }

        List<ItemStack> results = recipe.get().getResults();
        if (targetState.getBlock() instanceof SlabBlock && targetState.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
            results.forEach(result -> result.setCount(result.getCount() * 2));
        }
        BlockIngredient blockIngredient = recipe.get().getIngredient();

        //TODO  The companion slab is the only partial block that doesn't just get cut regardless of collision
        if (blockIngredient.test(BwtBlocks.companionSlabBlock) && state.get(FACING).getAxis().isHorizontal()) {
            return;
        }

        if (blockIngredient.test(BwtBlocks.companionCubeBlock)) {
            world.playSound(null, pos, BwtSoundEvents.COMPANION_CUBE_DEATH, SoundCategory.BLOCKS, 1, 1);
            if (state.get(FACING).getAxis().isHorizontal()) {
                results.get(0).setCount(1);
                world.setBlockState(targetPos, BwtBlocks.companionSlabBlock.getDefaultState());
            } else {
                world.breakBlock(targetPos, false);
            }
        } else {
            world.breakBlock(targetPos, false);
        }
        SoundTools.playBangSound(world, pos);

        if (targetState.contains(Properties.SLAB_TYPE) && targetState.get(Properties.SLAB_TYPE).equals(SlabType.DOUBLE)) {
            results.forEach(stack -> stack.setCount(stack.getCount() * 2));
        }

        CustomItemScatterer.spawn(world, targetPos, DefaultedList.copyOf(ItemStack.EMPTY, results.toArray(new ItemStack[0])));
    }

    public void breakSaw(World world, BlockPos pos) {
        dropItemsOnBreak(world, pos);
        world.breakBlock(pos, false);
        SoundTools.playBangSound(world, pos, 1);
    }

     @Override
     public void overpower(PowerState powerState) {
         breakSaw(powerState.world(), powerState.pos());
     }

    //TODO ----------- Client Side Functionality -----------//TODO 


    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (this.isPowered(state)) {
            emitSawParticles(world, state, pos);
        }
    }

    @Override
    public List<Direction> getInputFaces(World world, BlockPos pos, BlockState blockState) {
        return DirectionTools.filter(dir -> !dir.equals(blockState.get(FACING)));
    }

}