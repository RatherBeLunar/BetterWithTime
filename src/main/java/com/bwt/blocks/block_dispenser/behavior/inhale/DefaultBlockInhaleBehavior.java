package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class DefaultBlockInhaleBehavior implements BlockInhaleBehavior {
    @Override
    public ItemStack getInhaledItems(BlockSource blockPointer) {
        Level level = blockPointer.level();
        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        BlockPos facingPos = blockPointer.pos().relative(blockPointer.state().getValue(BlockDispenserBlock.FACING));
        BlockState facingState = level.getBlockState(facingPos);
        BlockEntity facingBlockEntity = level.getBlockEntity(facingPos);
        ItemStack bestCandidate = ItemStack.EMPTY;
        Vec3 centerPos = blockPointer.pos().getCenter();
        for (Item tool : new Item[]{Items.NETHERITE_PICKAXE, Items.SHEARS}) {
            ItemStack itemStack = new ItemStack(tool);
            Holder.Reference<Enchantment> silkTouch = enchantmentRegistry.getOrThrow(Enchantments.SILK_TOUCH);
            itemStack.enchant(silkTouch, 1);
            LootParams.Builder builder = new LootParams.Builder(blockPointer.level())
                    .withParameter(LootContextParams.ORIGIN, centerPos)
                    .withParameter(LootContextParams.TOOL, itemStack)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, facingBlockEntity)
                    .withParameter(LootContextParams.THIS_ENTITY, new ItemEntity(level, centerPos.x(), centerPos.y(), centerPos.z(), itemStack));
            List<ItemStack> drops = facingState.getDrops(builder);
            Optional<ItemStack> drop = drops.stream().filter(dropStack -> dropStack.is(facingState.getBlock().asItem())).findFirst();
            if (drop.isPresent()) {
                return drop.get();
            }
            if (!drops.isEmpty()) {
                bestCandidate = drops.get(0);
            }
        }
        return bestCandidate;
    }

    @Override
    public void inhale(BlockSource blockPointer) {
        breakBlockNoItems(blockPointer);
    }
}
