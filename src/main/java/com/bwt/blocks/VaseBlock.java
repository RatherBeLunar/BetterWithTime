package com.bwt.blocks;

import com.bwt.utils.Id;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import java.util.Arrays;
import java.util.HashMap;

public class VaseBlock extends Block {
    public static VoxelShape outlineShape = Block.createCuboidShape(3, 0, 3, 13, 16, 13);

    protected DyeColor dyeColor;
    public VaseBlock(DyeColor color, Settings settings) {
        super(settings);
        this.dyeColor = color;
    }

    public static void registerColors(HashMap<DyeColor, VaseBlock> vaseBlocks) {
        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            VaseBlock vaseBlock = new VaseBlock(dyeColor, Settings.create()
                    .nonOpaque()
                    .solidBlock(Blocks::never)
                    .sounds(BlockSoundGroup.GLASS)
                    .hardness(0f)
            );
            vaseBlocks.put(dyeColor, vaseBlock);
            Registry.register(Registries.BLOCK, Id.of("vase_" + dyeColor.getName()), vaseBlock);
            Registry.register(Registries.ITEM, Id.of("vase_" + dyeColor.getName()), new BlockItem(vaseBlock, new Item.Settings()));
        });
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return outlineShape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return outlineShape;
    }
}
