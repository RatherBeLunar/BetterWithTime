package com.bwt.blocks;

import com.bwt.utils.Id;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.Arrays;
import java.util.HashMap;

public class VaseBlock extends Block {
    public static final VoxelShape outlineShape = Block.box(3, 0, 3, 13, 16, 13);

    protected final DyeColor dyeColor;
    public VaseBlock(DyeColor color, Properties settings) {
        super(settings);
        this.dyeColor = color;
    }

    public static void registerColors(HashMap<DyeColor, VaseBlock> vaseBlocks) {
        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            VaseBlock vaseBlock = new VaseBlock(dyeColor, Properties.of()
                    .noOcclusion()
                    .isRedstoneConductor(Blocks::never)
                    .sound(SoundType.GLASS)
                    .destroyTime(0f)
            );
            vaseBlocks.put(dyeColor, vaseBlock);
            Registry.register(BuiltInRegistries.BLOCK, Id.of("vase_" + dyeColor.getName()), vaseBlock);
            Registry.register(BuiltInRegistries.ITEM, Id.of("vase_" + dyeColor.getName()), new BlockItem(vaseBlock, new Item.Properties()));
        });
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return outlineShape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return outlineShape;
    }
}
