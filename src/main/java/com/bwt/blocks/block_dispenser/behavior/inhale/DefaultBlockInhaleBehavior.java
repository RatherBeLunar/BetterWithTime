package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class DefaultBlockInhaleBehavior implements BlockInhaleBehavior {
    @Override
    public ItemStack getInhaledItems(BlockPointer blockPointer) {
        World world = blockPointer.world();
        BlockPos facingPos = blockPointer.pos().offset(blockPointer.state().get(BlockDispenserBlock.FACING));
        BlockState facingState = world.getBlockState(facingPos);
        BlockEntity facingBlockEntity = world.getBlockEntity(facingPos);
        ItemStack bestCandidate = ItemStack.EMPTY;
        Vec3d centerPos = blockPointer.pos().toCenterPos();
        for (Item tool : new Item[]{Items.NETHERITE_PICKAXE, Items.SHEARS}) {
            ItemStack itemStack = new ItemStack(tool);
            itemStack.addEnchantment(Enchantments.SILK_TOUCH, 1);
            LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(blockPointer.world())
                    .add(LootContextParameters.ORIGIN, centerPos)
                    .add(LootContextParameters.TOOL, itemStack)
                    .addOptional(LootContextParameters.BLOCK_ENTITY, facingBlockEntity)
                    .add(LootContextParameters.THIS_ENTITY, new ItemEntity(world, centerPos.getX(), centerPos.getY(), centerPos.getZ(), itemStack));
            List<ItemStack> drops = facingState.getBlock().getDroppedStacks(facingState, builder);
            Optional<ItemStack> drop = drops.stream().filter(dropStack -> dropStack.isOf(facingState.getBlock().asItem())).findFirst();
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
    public void inhale(BlockPointer blockPointer) {
        breakBlockNoItems(blockPointer.world(), blockPointer.pos().offset(blockPointer.state().get(BlockDispenserBlock.FACING)));
    }
}
