package com.bwt.blocks.block_dispenser;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.behavior.dispense.*;
import com.bwt.blocks.block_dispenser.behavior.inhale.BlockInhaleBehavior;
import com.bwt.blocks.block_dispenser.behavior.inhale.EntityInhaleBehavior;
import com.bwt.blocks.mining_charge.MiningChargeBlock;
import com.bwt.blocks.unfired_pottery.UnfiredDecoratedPotBlockEntity;
import com.bwt.entities.MiningChargeEntity;
import com.bwt.items.BwtItems;
import com.bwt.mixin.accessors.DecoratedPotPatternsAccessorMixin;
import com.bwt.recipes.block_dispenser_clump.BlockDispenserClumpRecipe;
import com.bwt.recipes.block_dispenser_clump.BlockDispenserClumpRecipeInput;
import com.bwt.recipes.BwtRecipes;
import com.bwt.sounds.BwtSoundEvents;
import com.bwt.tags.BwtBlockTags;
import com.bwt.tags.BwtEntityTags;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class BlockDispenserBlock extends DispenserBlock {
    public static final int tickRate = 4;

    private static final Map<Class<? extends Block>, DispenseItemBehavior> BLOCK_BEHAVIORS = Util.make(new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(BlockDispenserBehavior.DEFAULT));
    private static final Map<Item, DispenseItemBehavior> ITEM_BEHAVIORS = Util.make(new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(new DefaultItemDispenserBehavior()));
    private static final Map<Class<? extends Block>, BlockInhaleBehavior> BLOCK_INHALE_BEHAVIORS = Util.make(new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(BlockInhaleBehavior.DEFAULT));
    private static final Map<EntityType<? extends Entity>, EntityInhaleBehavior> ENTITY_INHALE_BEHAVIORS = Util.make(new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(EntityInhaleBehavior.NOOP));

    public BlockDispenserBlock(Properties settings) {
        super(settings);
    }

    protected void inheritItemBehavior(Item... items) {
        for (Item item : items) {
            ITEM_BEHAVIORS.put(item, invertStackResult(DispenserBlock.DISPENSER_REGISTRY.get(item)));
        }
    }

    protected static DispenseItemBehavior invertStackResult(DispenseItemBehavior behavior) {
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

        DefaultDispenseItemBehavior miningChargeBehavior = new DefaultDispenseItemBehavior(){
            @Override
            protected ItemStack execute(BlockSource pointer, ItemStack stack) {
                ServerLevel level = pointer.level();
                BlockPos blockPos = pointer.pos().relative(pointer.state().getValue(DispenserBlock.FACING));
                Direction direction = pointer.state().getValue(DispenserBlock.FACING);
                BlockState placementState = BwtBlocks.miningChargeBlock.getDispenserPlacmentState(
                        new BlockDispenserPlacementContext(pointer.level(), blockPos, direction, stack, direction)
                );
                MiningChargeEntity miningChargeEntity = new MiningChargeEntity(level, blockPos.getCenter().subtract(0, 0.5, 0), placementState, null);
                level.addFreshEntity(miningChargeEntity);
                level.playSound(null, miningChargeEntity.getX(), miningChargeEntity.getY(), miningChargeEntity.getZ(), BwtSoundEvents.MINING_CHARGE_PRIME, SoundSource.BLOCKS, 1.0f, 1.0f);
                level.gameEvent(null, GameEvent.ENTITY_PLACE, blockPos);
                stack.shrink(1);
                return stack;
            }
        };
        registerBlockDispenseBehavior(MiningChargeBlock.class, invertStackResult(miningChargeBehavior));
        DispenserBlock.registerBehavior(BwtBlocks.miningChargeBlock, miningChargeBehavior);

        registerBlockDispenseBehavior(ObserverBlock.class, new BlockDispenserBehavior() {
            @Override
            protected ItemStack execute(BlockSource pointer, ItemStack stack) {
                ItemStack result = super.execute(pointer, stack);
                if (isSuccess()) {
                    Direction direction = pointer.state().getValue(DispenserBlock.FACING);
                    BlockPos blockPos = pointer.pos().relative(direction);
                    pointer.level().neighborShapeChanged(
                            direction.getOpposite(),
                            pointer.state(),
                            blockPos,
                            pointer.pos(),
                            Block.UPDATE_ALL & ~(Block.UPDATE_NEIGHBORS | Block.UPDATE_SUPPRESS_DROPS),
                            511
                    );
                }
                return result;
            }
        });

        registerSherdBehaviors();
    }

    private void registerSherdBehaviors() {
        DefaultDispenseItemBehavior sherdBehavior = new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource pointer, ItemStack stack) {
                ServerLevel level = pointer.level();
                Direction direction = pointer.state().getValue(DispenserBlock.FACING);
                BlockPos blockPos = pointer.pos().relative(direction);
                BlockState blockState = level.getBlockState(blockPos);
                if (!blockState.is(BwtBlocks.unfiredDecoratedPotBlock) && !blockState.is(BwtBlocks.unfiredDecoratedPotBlockWithSherds)) {
                    return super.execute(pointer, stack);
                }
                if (blockState.is(BwtBlocks.unfiredDecoratedPotBlock)) {
                    blockState = BwtBlocks.unfiredDecoratedPotBlockWithSherds.defaultBlockState();
                    level.setBlock(blockPos, blockState, Block.UPDATE_CLIENTS, 0);
                }
                Direction side = direction.getOpposite();
                if (side.getAxis().isVertical()) {
                    return stack;
                }
                BlockEntity blockEntity = level.getBlockEntity(blockPos);
                if (!(blockEntity instanceof UnfiredDecoratedPotBlockEntity unfiredDecoratedPotBlockEntity)) {
                    return stack;
                }
                if (!level.isClientSide && unfiredDecoratedPotBlockEntity.tryAddSherd(side, stack.getItem())) {
                    stack.shrink(1);
                }
                return stack;
            }
        };
        ArrayList<Item> sherds = new ArrayList<>(DecoratedPotPatternsAccessorMixin.getITEM_TO_POT_TEXTURE().keySet());
        sherds.forEach(sherd -> DispenserBlock.registerBehavior(sherd, sherdBehavior));
        inheritItemBehavior(sherds.toArray(Item[]::new));
    }

    public static void registerEntityInhaleBehavior(EntityType<?> entityType, EntityInhaleBehavior behavior) {
        ENTITY_INHALE_BEHAVIORS.put(entityType, behavior);
    }

    public static void registerBlockInhaleBehavior(Class<? extends Block> blockClass, BlockInhaleBehavior behavior) {
        BLOCK_INHALE_BEHAVIORS.put(blockClass, behavior);
    }

    public static void registerItemDispenseBehavior(ItemLike item, DispenseItemBehavior behavior) {
        ITEM_BEHAVIORS.put(item.asItem(), behavior);
    }

    public static void registerBlockDispenseBehavior(Class<? extends Block> blockClass, DispenseItemBehavior behavior) {
        BLOCK_BEHAVIORS.put(blockClass, behavior);
    }

    public static void registerVanillaAndBDProjectileBehaviors(Item... items) {
        for (Item item : items) {
            DispenseItemBehavior projectileBehavior = new ProjectileDispenseBehavior(item);
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockDispenserBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite()).setValue(TRIGGERED, false);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);
        if (isReceivingPower(level, pos, state.getValue(FACING)) && !state.getValue(TRIGGERED)) {
            level.scheduleTick(pos, this, tickRate);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (level.isClientSide) {
            return;
        }
        if (state.getValue(TRIGGERED) != isReceivingPower(level, pos, state.getValue(FACING))) {
            level.scheduleTick(pos, this, tickRate);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        level.getBlockEntity(blockPos, BwtBlockEntities.blockDispenserBlockEntity).ifPresent(player::openMenu);
        return InteractionResult.CONSUME;
    }

    public boolean isReceivingPower(Level level, BlockPos pos, Direction facing) {
        return Arrays.stream(Direction.values())
                .filter(direction -> direction != facing)
                .anyMatch(direction -> level.hasSignal(pos.relative(direction), direction));
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean powered = isReceivingPower(level, pos, state.getValue(FACING));
        if (powered) {
            level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, true));
            dispenseBlockOrItem(level, state, pos);
        }
        else {
            level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, false));
            consumeBlockOrEntity(level, state, pos);
        }
    }

    public void dispenseBlockOrItem(ServerLevel level, BlockState state, BlockPos pos) {
        BlockDispenserBlockEntity blockEntity = ((BlockDispenserBlockEntity) level.getBlockEntity(pos));
        if (blockEntity == null) {
            return;
        }

        BlockPos targetPos = pos.relative(state.getValue(FACING));
        BlockState targetState = level.getBlockState(targetPos);

        ItemStack stackToPlace = blockEntity.getCurrentItemToDispense();
        if (stackToPlace.isEmpty()) {
            return;
        }

        BlockSource blockPointer = new BlockSource(level, pos, state, blockEntity);
        DispenseItemBehavior dispenserBehavior = this.getDispenseBehaviorForItem(level, targetState, blockEntity, stackToPlace);
        if (dispenserBehavior != DispenseItemBehavior.NOOP) {
            ItemStack takenOut = dispenserBehavior.dispense(blockPointer, stackToPlace);
            blockEntity.take(takenOut.getItem(), takenOut.getCount());
        }
        blockEntity.advanceSelectedSlot();
    }

    public void consumeBlockOrEntity(ServerLevel level, BlockState state, BlockPos pos) {
        BlockDispenserBlockEntity blockEntity = ((BlockDispenserBlockEntity) level.getBlockEntity(pos));
        if (blockEntity == null) {
            return;
        }

        BlockPos targetPos = pos.relative(state.getValue(FACING));
        BlockState targetState = level.getBlockState(targetPos);
        Optional<? extends Entity> optionalEntity = this.getInhaleableEntity(level, targetPos);
        if (optionalEntity.isPresent()) {
            Entity entity = optionalEntity.get();
            inhaleEntity(blockEntity, entity);
            return;
        }

        BlockSource blockPointer = new BlockSource(level, pos, state, blockEntity);
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

    protected DispenseItemBehavior getDispenseBehaviorForItem(Level level, BlockState targetState, BlockDispenserBlockEntity entity, ItemStack stack) {
        // Block Behavior. Block items will not clump
        if (stack.getItem() instanceof BlockItem blockItem) {
            if (!targetState.is(BlockTags.REPLACEABLE)) {
                return BlockDispenserBehavior.NOOP;
            }
            return BLOCK_BEHAVIORS.entrySet().stream()
                    .filter(entry -> entry.getKey().isInstance(blockItem.getBlock()))
                    .findAny()
                    .map(Map.Entry::getValue)
                    .orElse(BlockDispenserBehavior.DEFAULT);
        }

        BlockDispenserClumpRecipeInput recipeInput = new BlockDispenserClumpRecipeInput(entity.getItems());
        Optional<BlockDispenserClumpRecipe> match = level.getRecipeManager().getRecipeFor(
                BwtRecipes.BLOCK_DISPENSER_CLUMP_RECIPE_TYPE,
                recipeInput,
                level
        ).map(RecipeHolder::value);

        if (match.isEmpty()) {
            return ITEM_BEHAVIORS.get(stack.getItem());
        }

        // Proceeding with clump recipe behavior
        BlockDispenserClumpRecipe recipe = match.get();
        if (recipe.canAfford(entity) && targetState.is(BlockTags.REPLACEABLE)) {
            return new ItemClumpDispenserBehavior(recipe, stack.getItem());
        }
        else {
            return BlockDispenserBehavior.NOOP;
        }
    }

    protected Optional<Entity> getInhaleableEntity(Level level, BlockPos targetPos) {
        ArrayList<Entity> entities = Lists.newArrayList();
        level.getEntities(
                EntityTypeTest.forClass(Entity.class),
                new AABB(targetPos),
                EntitySelector.NO_SPECTATORS.and(entity ->
                        entity.getType().is(BwtEntityTags.BLOCK_DISPENSER_INHALE_ENTITIES)
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
        entityInhaleBehavior.getDroppedItems(entity).forEach(entity::spawnAtLocation);
    }

    protected BlockInhaleBehavior getInhaleBehaviorForItem(BlockState targetState) {
        if (targetState.is(BwtBlockTags.BLOCK_DISPENSER_INHALE_NOOP)) {
            return BlockInhaleBehavior.NOOP;
        }
        if (targetState.is(BwtBlockTags.BLOCK_DISPENSER_INHALE_VOID)) {
            return BlockInhaleBehavior.VOID;
        }
        return BLOCK_INHALE_BEHAVIORS.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(targetState.getBlock()))
                .findAny()
                .map(Map.Entry::getValue)
                .orElse(BlockInhaleBehavior.DEFAULT);
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
