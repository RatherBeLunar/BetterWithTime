package com.bwt.utils;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class RegistrationBase {
    public static ResourceKey<Block> blockKey(String string) {
        return ResourceKey.create(Registries.BLOCK, Id.of(string));
    }

    public static Block registerBlock(String string, BlockBehaviour.Properties properties) {
        return registerBlock(string, Block::new, properties);
    }

    public static <B extends Block> B registerBlock(String string, Function<BlockBehaviour.Properties, B> function, BlockBehaviour.Properties properties) {
        return registerBlock(blockKey(string), function, properties);
    }

    public static <B extends Block> B registerBlock(ResourceKey<Block> resourceKey, Function<BlockBehaviour.Properties, B> function, BlockBehaviour.Properties properties) {
        properties = properties.setId(resourceKey);
        B block = function.apply(properties);
        return Registry.register(BuiltInRegistries.BLOCK, resourceKey, block);
    }

    public static Block registerBlockWithItem(String string, BlockBehaviour.Properties properties) {
        return registerBlockWithItem(string, Block::new, properties);
    }

    public static <B extends Block> B registerBlockWithItem(String string, Function<BlockBehaviour.Properties, B> function, BlockBehaviour.Properties properties) {
        return registerBlockWithItem(blockKey(string), function, properties);
    }

    public static <B extends Block> B registerBlockWithItem(ResourceKey<Block> resourceKey, Function<BlockBehaviour.Properties, B> function, BlockBehaviour.Properties properties) {
        return registerBlockWithItem(resourceKey, function, properties, new Item.Properties());
    }

    public static <B extends Block> B registerBlockWithItem(String string, Function<BlockBehaviour.Properties, B> function, BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        return registerBlockWithItem(blockKey(string), function, properties, itemProperties);
    }

    public static <B extends Block> B registerBlockWithItem(ResourceKey<Block> resourceKey, Function<BlockBehaviour.Properties, B> function, BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        properties = properties.setId(resourceKey);
        B block = Registry.register(BuiltInRegistries.BLOCK, resourceKey, function.apply(properties));
        // Register the BlockItem and deliberately don't store it in a variable
        registerItem(itemKey(resourceKey), itemProps -> new BlockItem(block, itemProperties), itemProperties);
        return block;
    }

    public static Function<Item.Properties, Item> createBlockItemWithCustomItemName(Block block) {
        return properties -> new BlockItem(block, properties.useItemDescriptionPrefix());
    }


    public static ResourceKey<Item> itemKey(String string) {
        return ResourceKey.create(Registries.ITEM, Id.of(string));
    }

    public static ResourceKey<Item> itemKey(ResourceKey<Block> resourceKey) {
        return ResourceKey.create(Registries.ITEM, resourceKey.location());
    }

    public static Item registerItem(String string) {
        return registerItem(string, new Item.Properties());
    }

    public static Item registerItem(ResourceKey<Item> resourceKey) {
        return registerItem(resourceKey, new Item.Properties());
    }

    public static Item registerItem(String string, Item.Properties properties) {
        return registerItem(string, Item::new, properties);
    }

    public static Item registerItem(ResourceKey<Item> resourceKey, Item.Properties properties) {
        return registerItem(resourceKey, Item::new, properties);
    }

    public static <T extends Item> T registerItem(String string, Function<Item.Properties, T> function) {
        return registerItem(string, function, new Item.Properties());
    }

    public static <T extends Item> T registerItem(ResourceKey<Item> resourceKey, Function<Item.Properties, T> function) {
        return registerItem(resourceKey, function, new Item.Properties());
    }

    public static <T extends Item> T registerItem(String string, Function<Item.Properties, T> function, Item.Properties properties) {
        return registerItem(itemKey(string), function, properties);
    }

    public static <T extends Item> T registerItem(ResourceKey<Item> resourceKey, Function<Item.Properties, T> function, Item.Properties properties) {
        return registerItem(
                resourceKey,
                function,
                properties,
                Item.Properties::setId
        );
    }

    public static <T extends Item, P extends Item.Properties> T registerItem(String string, Function<P, T> function, P properties, BiFunction<P, ResourceKey<Item>, P> idSetter) {
        return registerItem(itemKey(string), function, properties, idSetter);
    }

    public static <T extends Item, P extends Item.Properties> T registerItem(ResourceKey<Item> resourceKey, Function<P, T> function, P properties, BiFunction<P, ResourceKey<Item>, P> idSetter) {
        properties = idSetter.apply(properties, resourceKey);
        T item = function.apply(properties);
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }

        Registry.register(BuiltInRegistries.ITEM, resourceKey, item);
        return item;
    }


    public static ResourceKey<EntityType<?>> entityKey(String string) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Id.of(string));
    }

    public static <T extends Entity> EntityType<T> registerEntity(String string, EntityType.Builder<T> builder) {
        return registerEntity(entityKey(string), builder);
    }

    public static <T extends Entity> EntityType<T> registerEntity(ResourceKey<EntityType<?>> resourceKey, EntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, resourceKey, builder.build(resourceKey));
    }


    public static ResourceKey<Recipe<?>> recipeKey(String string) {
        return ResourceKey.create(Registries.RECIPE, Id.of(string));
    }
}
