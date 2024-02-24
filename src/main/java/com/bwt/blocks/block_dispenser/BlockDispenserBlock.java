package com.bwt.blocks.block_dispenser;

import com.bwt.blocks.block_dispenser.behavior.*;
import com.bwt.recipes.BlockDispenserClumpRecipe;
import com.bwt.recipes.BwtRecipes;
import com.bwt.tags.BwtTags;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class BlockDispenserBlock extends DispenserBlock {
    private static final Map<Item, DispenserBehavior> BLOCK_BEHAVIORS = Util.make(new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(new BlockDispenserBehavior()));
    private static final Map<Item, DispenserBehavior> ITEM_BEHAVIORS = Util.make(new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(new DefaultItemDispenserBehavior()));
    private static final Map<Block, BlockInhaleBehavior> BLOCK_INHALE_BEHAVIORS = Util.make(new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(new DefaultBlockInhaleBehavior()));
    private static final Map<EntityType<? extends Entity>, EntityInhaleBehavior> ENTITY_INHALE_BEHAVIORS = Util.make(new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(null));

    public BlockDispenserBlock(Settings settings) {
        super(settings);
    }

    public static void registerEntityInhaleBehavior(EntityType<?> entityType, EntityInhaleBehavior behavior) {
        ENTITY_INHALE_BEHAVIORS.put(entityType, behavior);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockDispenserBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite()).with(TRIGGERED, false);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (isReceivingPower(world, pos)) {
            world.scheduleBlockTick(pos, this, 4);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        if (state.get(TRIGGERED) != isReceivingPower(world, pos)) {
            world.setBlockState(pos, state.cycle(TRIGGERED), Block.NOTIFY_LISTENERS);
            world.scheduleBlockTick(pos, this, 4);
        }
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) return ActionResult.SUCCESS;
        //This will call the createScreenHandlerFactory method from BlockWithEntity, which will return our blockEntity casted to
        //a namedScreenHandlerFactory. If your block class does not extend BlockWithEntity, it needs to implement createScreenHandlerFactory.
        NamedScreenHandlerFactory screenHandlerFactory = blockState.createScreenHandlerFactory(world, blockPos);

        if (screenHandlerFactory != null) {
            player.openHandledScreen(screenHandlerFactory);
        }

        return ActionResult.CONSUME;
    }

    public boolean isReceivingPower(World world, BlockPos pos) {
        return world.getReceivedStrongRedstonePower(pos) > 0 || world.getReceivedStrongRedstonePower(pos.up()) > 0;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean powered = state.get(TRIGGERED);
        if (powered) {
            dispenseBlockOrItem(world, state, pos);
        }
        else {
            consumeBlockOrEntity(world, state, pos);
        }
    }

    public void dispenseBlockOrItem(ServerWorld world, BlockState state, BlockPos pos) {
        BlockDispenserBlockEntity blockEntity = ((BlockDispenserBlockEntity) world.getBlockEntity(pos));
        if (blockEntity == null) {
            return;
        }
        
        BlockPos targetPos = pos.offset(state.get(FACING));
        BlockState targetState = world.getBlockState(targetPos);

        if (!targetState.isIn(BlockTags.REPLACEABLE)) {
            return;
        }

        ItemStack stackToPlace = blockEntity.getCurrentItemToDispense();
        if (stackToPlace.isEmpty()) {
            return;
        }

        BlockPointer blockPointer = new BlockPointer(world, pos, state, blockEntity);
        DispenserBehavior dispenserBehavior = this.getDispenseBehaviorForItem(world, blockEntity, stackToPlace);
        if (dispenserBehavior != DispenserBehavior.NOOP) {
            ItemStack takenOut = dispenserBehavior.dispense(blockPointer, stackToPlace);
            blockEntity.take(takenOut.getItem(), takenOut.getCount());
        }
        blockEntity.advanceSelectedSlot();
    }

    public void consumeBlockOrEntity(ServerWorld world, BlockState state, BlockPos pos) {
        BlockDispenserBlockEntity blockEntity = ((BlockDispenserBlockEntity) world.getBlockEntity(pos));
        if (blockEntity == null) {
            return;
        }

        BlockPos targetPos = pos.offset(state.get(FACING));
        BlockState targetState = world.getBlockState(targetPos);
        Optional<? extends Entity> optionalEntity = this.getInhaleableEntity(world, targetPos);
        if (optionalEntity.isPresent()) {
            Entity entity = optionalEntity.get();
            inhaleEntity(blockEntity, entity);
            return;
        }

        BlockPointer blockPointer = new BlockPointer(world, pos, state, blockEntity);
        BlockInhaleBehavior inhaleBehavior = this.getInhaleBehaviorForItem(targetState);
        if (inhaleBehavior == BlockInhaleBehavior.NOOP) {
            return;
        }
        ItemStack inhaledItems = inhaleBehavior.getInhaledItems(blockPointer);
        if (!blockEntity.hasRoomFor(inhaledItems)) {
            return;
        }
        inhaleBehavior.inhale(blockPointer);
        blockEntity.insert(inhaledItems.copy());
    }

    protected DispenserBehavior getDispenseBehaviorForItem(World world, BlockDispenserBlockEntity entity, ItemStack stack) {
        // Block Behavior. Block items will not clump
        if (stack.getItem() instanceof BlockItem) {
            return BLOCK_BEHAVIORS.get(stack.getItem());
        }

        Optional<BlockDispenserClumpRecipe> match = world.getRecipeManager().getFirstMatch(
                BwtRecipes.BLOCK_DISPENSER_CLUMP_RECIPE_TYPE,
                entity,
                world
        ).map(RecipeEntry::value);

        if (match.isEmpty()) {
            return ITEM_BEHAVIORS.get(stack.getItem());
        }

        // Proceeding with clump recipe behavior
        BlockDispenserClumpRecipe recipe = match.get();
        if (recipe.canAfford(entity)) {
            return new ItemClumpDispenserBehavior(recipe, stack.getItem());
        }
        else {
            return BlockDispenserBehavior.NOOP;
        }
    }

    protected Optional<Entity> getInhaleableEntity(World world, BlockPos targetPos) {
        ArrayList<Entity> entities = Lists.newArrayList();
        world.collectEntitiesByType(
                TypeFilter.instanceOf(Entity.class),
                new Box(targetPos),
                EntityPredicates.EXCEPT_SPECTATOR.and(entity ->
                        entity.getType().isIn(BwtTags.BLOCK_DISPENSER_INHALE_ENTITIES)
                        && ENTITY_INHALE_BEHAVIORS.getOrDefault(entity.getType(), EntityInhaleBehavior.NOOP).canInhale(entity)
                ),
                entities
        );
        return entities.stream().findAny();
    }

    protected <T extends Entity> void inhaleEntity(BlockDispenserBlockEntity blockEntity, T entity) {
        EntityInhaleBehavior entityInhaleBehavior = ENTITY_INHALE_BEHAVIORS.get(entity.getType());
        entityInhaleBehavior.inhale(entity);
        blockEntity.insert(entityInhaleBehavior.getInhaledItems(entity).copy());
        entityInhaleBehavior.getDroppedItems(entity).forEach(entity::dropStack);
    }

    protected BlockInhaleBehavior getInhaleBehaviorForItem(BlockState targetState) {
        if (targetState.isIn(BwtTags.BLOCK_DISPENSER_INHALE_NOOP)) {
            return BlockInhaleBehavior.NOOP;
        }
        if (targetState.isOf(Blocks.NETHER_PORTAL)) {
            return new VoidInhaleBehavior();
        }
        if (targetState.getBlock() instanceof CropBlock) {
            return new CropInhaleBehavior();
        }
        return BLOCK_INHALE_BEHAVIORS.get(targetState.getBlock());
    }
}
