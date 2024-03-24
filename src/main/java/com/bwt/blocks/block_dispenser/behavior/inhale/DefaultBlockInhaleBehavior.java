package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
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
        BlockState state = world.getBlockState(facingPos);
        ItemStack bestCandidate = ItemStack.EMPTY;
        for (Item tool : new Item[]{Items.NETHERITE_PICKAXE, Items.SHEARS}) {
            ItemStack itemStack = new ItemStack(tool);
            itemStack.addEnchantment(Enchantments.SILK_TOUCH, 1);
            LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(blockPointer.world())
                    .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPointer.pos()))
                    .add(LootContextParameters.TOOL, itemStack)
                    .addOptional(LootContextParameters.BLOCK_ENTITY, world.getBlockEntity(facingPos));
            List<ItemStack> drops = state.getBlock().getDroppedStacks(state, builder);
            Optional<ItemStack> drop = drops.stream().filter(dropStack -> dropStack.isOf(state.getBlock().asItem())).findFirst();
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
