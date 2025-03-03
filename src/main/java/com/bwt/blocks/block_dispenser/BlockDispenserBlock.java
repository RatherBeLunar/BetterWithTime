package com.bwt.blocks.block_dispenser;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.behavior.dispense.*;
import com.bwt.blocks.block_dispenser.behavior.inhale.BlockInhaleBehavior;
import com.bwt.blocks.block_dispenser.behavior.inhale.EntityInhaleBehavior;
import com.bwt.blocks.mining_charge.MiningChargeBlock;
import com.bwt.entities.MiningChargeEntity;
import com.bwt.items.BwtItems;
import com.bwt.recipes.block_dispenser_clump.BlockDispenserClumpRecipe;
import com.bwt.recipes.block_dispenser_clump.BlockDispenserClumpRecipeInput;
import com.bwt.recipes.BwtRecipes;
import com.bwt.sounds.BwtSoundEvents;
import com.bwt.tags.BwtBlockTags;
import com.bwt.tags.BwtEntityTags;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class BlockDispenserBlock extends DispenserBlock {
    public static final int tickRate = 4;

    private static final Map<Class<? extends Block>, DispenserBehavior> BLOCK_BEHAVIORS = Util.make(new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(BlockDispenserBehavior.DEFAULT));
    private static final Map<Item, DispenserBehavior> ITEM_BEHAVIORS = Util.make(new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(new DefaultItemDispenserBehavior()));
    private static final Map<Class<? extends Block>, BlockInhaleBehavior> BLOCK_INHALE_BEHAVIORS = Util.make(new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(BlockInhaleBehavior.DEFAULT));
    private static final Map<EntityType<? extends Entity>, EntityInhaleBehavior> ENTITY_INHALE_BEHAVIORS = Util.make(new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(EntityInhaleBehavior.NOOP));

    public BlockDispenserBlock(Settings settings) {
        super(settings);
    }

    protected void inheritItemBehavior(Item... items) {
        for (Item item : items) {
            ITEM_BEHAVIORS.put(item, invertStackResult(DispenserBlock.BEHAVIORS.get(item)));
        }
    }

    protected static DispenserBehavior invertStackResult(DispenserBehavior behavior) {
        return (pointer, stack) -> {
            int originalCount = stack.getCount();
            ItemStack overwriteStack = behavior.dispense(pointer, stack);
            int newCount = overwriteStack.getCount();
            stack.setCount(originalCount);
            return overwriteStack.copyWithCount(originalCount - newCount);
        };
    }

    public void registerItemDispenseBehaviors() {
        inheritItemBehavior(
                Items.ARMOR_STAND,

                Items.ARROW,
                Items.SPECTRAL_ARROW,
                Items.TIPPED_ARROW,
                Items.EGG,

                Items.SPLASH_POTION,
                Items.LINGERING_POTION
        );

        BoatDispenserBehavior boatDispenserBehavior = new BoatDispenserBehavior();
        Stream.of(
                Items.OAK_BOAT,
                Items.SPRUCE_BOAT,
                Items.BIRCH_BOAT,
                Items.JUNGLE_BOAT,
                Items.DARK_OAK_BOAT,
                Items.ACACIA_BOAT,
                Items.CHERRY_BOAT,
                Items.MANGROVE_BOAT,
                Items.BAMBOO_RAFT,
                Items.OAK_CHEST_BOAT,
                Items.SPRUCE_CHEST_BOAT,
                Items.BIRCH_CHEST_BOAT,
                Items.JUNGLE_CHEST_BOAT,
                Items.DARK_OAK_CHEST_BOAT,
                Items.ACACIA_CHEST_BOAT,
                Items.CHERRY_CHEST_BOAT,
                Items.MANGROVE_CHEST_BOAT,
                Items.BAMBOO_CHEST_RAFT
        ).forEach(item -> registerItemDispenseBehavior(item, boatDispenserBehavior));

        MinecartDispenserBehavior minecartDispenserBehavior = new MinecartDispenserBehavior();
        Stream.of(
                Items.MINECART,
                Items.CHEST_MINECART,
                Items.COMMAND_BLOCK_MINECART,
                Items.COMMAND_BLOCK_MINECART,
                Items.FURNACE_MINECART,
                Items.HOPPER_MINECART,
                Items.TNT_MINECART
        ).forEach(item -> registerItemDispenseBehavior(item, minecartDispenserBehavior));

        registerVanillaAndBDProjectileBehaviors(
                BwtItems.dynamiteItem,
                BwtItems.broadheadArrowItem,
                BwtItems.rottedArrowItem,
                BwtItems.soulUrnItem
        );

        ItemDispenserBehavior miningChargeBehavior = new ItemDispenserBehavior(){
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                ServerWorld world = pointer.world();
                BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
                Direction direction = pointer.state().get(DispenserBlock.FACING);
                BlockState placementState = BwtBlocks.miningChargeBlock.getDispenserPlacmentState(
                        new BlockDispenserPlacementContext(pointer.world(), blockPos, direction, stack, direction)
                );
                MiningChargeEntity miningChargeEntity = new MiningChargeEntity(world, blockPos.toCenterPos().subtract(0, 0.5, 0), placementState, null);
                world.spawnEntity(miningChargeEntity);
                world.playSound(null, miningChargeEntity.getX(), miningChargeEntity.getY(), miningChargeEntity.getZ(), BwtSoundEvents.MINING_CHARGE_PRIME, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(null, GameEvent.ENTITY_PLACE, blockPos);
                stack.decrement(1);
                return stack;
            }
        };
        registerBlockDispenseBehavior(MiningChargeBlock.class, invertStackResult(miningChargeBehavior));
        DispenserBlock.registerBehavior(BwtBlocks.miningChargeBlock, miningChargeBehavior);
    }

    public static void registerEntityInhaleBehavior(EntityType<?> entityType, EntityInhaleBehavior behavior) {
        ENTITY_INHALE_BEHAVIORS.put(entityType, behavior);
    }

    public static void registerBlockInhaleBehavior(Class<? extends Block> blockClass, BlockInhaleBehavior behavior) {
        BLOCK_INHALE_BEHAVIORS.put(blockClass, behavior);
    }

    public static void registerItemDispenseBehavior(ItemConvertible item, DispenserBehavior behavior) {
        ITEM_BEHAVIORS.put(item.asItem(), behavior);
    }

    public static void registerBlockDispenseBehavior(Class<? extends Block> blockClass, DispenserBehavior behavior) {
        BLOCK_BEHAVIORS.put(blockClass, behavior);
    }

    public static void registerVanillaAndBDProjectileBehaviors(Item... items) {
        for (Item item : items) {
            DispenserBehavior projectileBehavior = new ProjectileDispenserBehavior(item);
            registerItemDispenseBehavior(item, invertStackResult(projectileBehavior));
            DispenserBlock.registerBehavior(item, projectileBehavior);
        }
    }

    public void registerBehaviors() {
        EntityInhaleBehavior.registerBehaviors();
        BlockInhaleBehavior.registerBehaviors();
        BlockDispenserBehavior.registerBehaviors();
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
            world.scheduleBlockTick(pos, this, tickRate);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        if (state.get(TRIGGERED) != isReceivingPower(world, pos)) {
            world.scheduleBlockTick(pos, this, tickRate);
        }
    }

    @Override
    protected ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, BlockHitResult hit) {
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
        boolean powered = isReceivingPower(world, pos);
        if (powered) {
            world.setBlockState(pos, state.with(TRIGGERED, true));
            dispenseBlockOrItem(world, state, pos);
        }
        else {
            world.setBlockState(pos, state.with(TRIGGERED, false));
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

        ItemStack stackToPlace = blockEntity.getCurrentItemToDispense();
        if (stackToPlace.isEmpty()) {
            return;
        }

        BlockPointer blockPointer = new BlockPointer(world, pos, state, blockEntity);
        DispenserBehavior dispenserBehavior = this.getDispenseBehaviorForItem(world, targetState, blockEntity, stackToPlace);
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

    protected DispenserBehavior getDispenseBehaviorForItem(World world, BlockState targetState, BlockDispenserBlockEntity entity, ItemStack stack) {
        // Block Behavior. Block items will not clump
        if (stack.getItem() instanceof BlockItem blockItem) {
            if (!targetState.isIn(BlockTags.REPLACEABLE)) {
                return BlockDispenserBehavior.NOOP;
            }
            return BLOCK_BEHAVIORS.entrySet().stream()
                    .filter(entry -> entry.getKey().isInstance(blockItem.getBlock()))
                    .findAny()
                    .map(Map.Entry::getValue)
                    .orElse(BlockDispenserBehavior.DEFAULT);
        }

        BlockDispenserClumpRecipeInput recipeInput = new BlockDispenserClumpRecipeInput(entity.getItems());
        Optional<BlockDispenserClumpRecipe> match = world.getRecipeManager().getFirstMatch(
                BwtRecipes.BLOCK_DISPENSER_CLUMP_RECIPE_TYPE,
                recipeInput,
                world
        ).map(RecipeEntry::value);

        if (match.isEmpty()) {
            return ITEM_BEHAVIORS.get(stack.getItem());
        }

        // Proceeding with clump recipe behavior
        BlockDispenserClumpRecipe recipe = match.get();
        if (recipe.canAfford(entity) && targetState.isIn(BlockTags.REPLACEABLE)) {
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
                        entity.getType().isIn(BwtEntityTags.BLOCK_DISPENSER_INHALE_ENTITIES)
                        && ENTITY_INHALE_BEHAVIORS.getOrDefault(entity.getType(), EntityInhaleBehavior.NOOP).canInhale(entity)
                ),
                entities
        );
        return entities.stream().findAny();
    }

    protected <T extends Entity> void inhaleEntity(BlockDispenserBlockEntity blockEntity, T entity) {
        EntityInhaleBehavior entityInhaleBehavior = ENTITY_INHALE_BEHAVIORS.get(entity.getType());
        ItemStack inhaledItems = entityInhaleBehavior.getInhaledItems(entity).copy();
        if (!blockEntity.hasRoomFor(inhaledItems)) {
            return;
        }
        entityInhaleBehavior.inhale(entity);
        blockEntity.insert(inhaledItems);
        entityInhaleBehavior.getDroppedItems(entity).forEach(entity::dropStack);
    }

    protected BlockInhaleBehavior getInhaleBehaviorForItem(BlockState targetState) {
        if (targetState.isIn(BwtBlockTags.BLOCK_DISPENSER_INHALE_NOOP)) {
            return BlockInhaleBehavior.NOOP;
        }
        return BLOCK_INHALE_BEHAVIORS.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(targetState.getBlock()))
                .findAny()
                .map(Map.Entry::getValue)
                .orElse(BlockInhaleBehavior.DEFAULT);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }
}
