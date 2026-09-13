package com.bwt.items;

import com.bwt.blocks.BwtBlocks;
import com.bwt.entities.WaterWheelEntity;
import com.bwt.entities.WindmillEntity;
import com.bwt.entities.CanvasEntity;
import com.bwt.tags.BwtPaintingVariantTags;
import com.bwt.utils.Id;
import com.bwt.utils.LockableItemSettings;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class BwtItems implements ModInitializer {
    public static final Item cementBucketItem = Registry.register(BuiltInRegistries.ITEM, Id.of("cement_bucket"), new CementBucketItem(new Item.Properties()));
	public static final Item armorPlateItem = Registry.register(BuiltInRegistries.ITEM, Id.of("armor_plate"), new Item(new Item.Properties()));
	public static final Item beltItem = Registry.register(BuiltInRegistries.ITEM, Id.of("belt"), new Item(new Item.Properties()));
	public static final Item breedingHarnessItem = Registry.register(BuiltInRegistries.ITEM, Id.of("breeding_harness"), new Item(new Item.Properties()));
	public static final Item broadheadItem = Registry.register(BuiltInRegistries.ITEM, Id.of("broadhead"), new Item(new Item.Properties()));
	public static final Item broadheadArrowItem = Registry.register(BuiltInRegistries.ITEM, Id.of("broadhead_arrow"), new BroadheadArrowItem(new Item.Properties()));
//	public static final Item candleItem = Registry.register(Registries.ITEM, Id.of("candle"), new CandleItem(new Item.Settings()));
	public static final Item canvasItem = Registry.register(BuiltInRegistries.ITEM, Id.of("canvas"), new CanvasItem(new Item.Properties()));
	public static final Item coalDustItem = Registry.register(BuiltInRegistries.ITEM, Id.of("coal_dust"), new Item(new Item.Properties()));
	public static final Item compositeBowItem = Registry.register(BuiltInRegistries.ITEM, Id.of("composite_bow"), new CompositeBowItem(new Item.Properties().durability(576)));
	public static final Item concentratedHellfireItem = Registry.register(BuiltInRegistries.ITEM, Id.of("concentrated_hellfire"), new Item(new Item.Properties()));
    public static final Item cookedWolfChopItem = Registry.register(BuiltInRegistries.ITEM, Id.of("cooked_wolf_chop"), new Item(
            new Item.Properties()
                    .food(Foods.COOKED_PORKCHOP))
    );
	public static final Item donutItem = Registry.register(BuiltInRegistries.ITEM, Id.of("donut"), new Item(new Item.Properties()
            .food(new FoodProperties.Builder()
                    .nutrition(1)
                    .saturationModifier(0.5f)
                    .fast()
                    .alwaysEdible()
                    .build())
    ));
	public static final DyeItem dungItem = Registry.register(BuiltInRegistries.ITEM, Id.of("dung"), new DungItem(new Item.Properties()));
	public static final Item dynamiteItem = Registry.register(BuiltInRegistries.ITEM, Id.of("dynamite"), new DynamiteItem(new Item.Properties()));
//	public static final Item enderSpectaclesItem = Registry.register(Registries.ITEM, Id.of("ender_spectacles"), new EnderSpectaclesItem(new Item.Settings()));
	public static final Item fabricItem = Registry.register(BuiltInRegistries.ITEM, Id.of("fabric"), new Item(new Item.Properties()));
	public static final Item filamentItem = Registry.register(BuiltInRegistries.ITEM, Id.of("filament"), new Item(new Item.Properties()));
	public static final Item flourItem = Registry.register(BuiltInRegistries.ITEM, Id.of("flour"), new Item(new Item.Properties()));
	public static final Item foulFoodItem = Registry.register(BuiltInRegistries.ITEM, Id.of("foul_food"), new Item(new Item.Properties()
            .food(new FoodProperties.Builder()
                    .nutrition(1)
                    .effect(new MobEffectInstance(MobEffects.POISON, 20 * 30, 0), 0.8f)
                    .build())
    ));
    public static final Item friedEggItem = Registry.register(BuiltInRegistries.ITEM, Id.of("fried_egg"), new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.25f).build())));
//	public static final Item fuseItem = Registry.register(Registries.ITEM, Id.of("fuse"), new FuseItem(new Item.Settings()));
	public static final Item gearItem = Registry.register(BuiltInRegistries.ITEM, Id.of("gear"), new Item(new Item.Properties()));
	public static final Item glueItem = Registry.register(BuiltInRegistries.ITEM, Id.of("glue"), new Item(new Item.Properties()));
	public static final Item groundNetherrackItem = Registry.register(BuiltInRegistries.ITEM, Id.of("ground_netherrack"), new Item(new Item.Properties()));
	public static final Item haftItem = Registry.register(BuiltInRegistries.ITEM, Id.of("haft"), new Item(new Item.Properties()));
	public static final Item hellfireDustItem = Registry.register(BuiltInRegistries.ITEM, Id.of("hellfire_dust"), new Item(new Item.Properties()));
	public static final Item hempFiberItem = Registry.register(BuiltInRegistries.ITEM, Id.of("hemp_fiber"), new Item(new Item.Properties()));
	public static final Item hempItem = Registry.register(BuiltInRegistries.ITEM, Id.of("hemp"), new Item(new Item.Properties()));
	public static final Item hempSeedsItem = Registry.register(BuiltInRegistries.ITEM, Id.of("hemp_seeds"), new HempSeedsItem(BwtBlocks.hempCropBlock, new Item.Properties()));
	public static final Item kibbleItem = Registry.register(BuiltInRegistries.ITEM, Id.of("kibble"), new Item(new Item.Properties()));
//	public static final Item netherBrickItem = Registry.register(Registries.ITEM, Id.of("nether_brick"), new NetherBrickItem(new Item.Settings()));
	public static final Item nethercoalItem = Registry.register(BuiltInRegistries.ITEM, Id.of("nethercoal"), new Item(new Item.Properties()));
//	public static final Item nitreItem = Registry.register(Registries.ITEM, Id.of("nitre"), new NitreItem(new Item.Settings()));
	public static final Item paddingItem = Registry.register(BuiltInRegistries.ITEM, Id.of("padding"), new Item(new Item.Properties()));
	public static final Item poachedEggItem = Registry.register(BuiltInRegistries.ITEM, Id.of("poached_egg"), new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.25f).build())));
	public static final Item potashItem = Registry.register(BuiltInRegistries.ITEM, Id.of("potash"), new Item(new Item.Properties()));
    public static final Item rawEggItem = Registry.register(BuiltInRegistries.ITEM, Id.of("raw_egg"), new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.25f).build())));
    public static final Item redstoneEyeItem = Registry.register(BuiltInRegistries.ITEM, Id.of("redstone_eye"), new Item(new Item.Properties()));
    public static final Item netheriteMattockItem = Registry.register(BuiltInRegistries.ITEM, Id.of("netherite_mattock"), new MattockItem(Tiers.NETHERITE, new Item.Properties().fireResistant().attributes(PickaxeItem.createAttributes(Tiers.NETHERITE, 1, -3.0f))));
    public static final Item netheriteBattleAxeItem = Registry.register(BuiltInRegistries.ITEM, Id.of("netherite_battle_axe"), new BattleAxeItem(Tiers.NETHERITE, new LockableItemSettings().attributes(AxeItem.createAttributes(Tiers.NETHERITE, 3, -2.4f))));
	public static final Item ropeItem = Registry.register(BuiltInRegistries.ITEM, Id.of("rope"), new RopeItem(new Item.Properties()));
	public static final Item rottedArrowItem = Registry.register(BuiltInRegistries.ITEM, Id.of("rotted_arrow"), new RottedArrowItem(new Item.Properties()));
	public static final Item sailItem = Registry.register(BuiltInRegistries.ITEM, Id.of("sail"), new Item(new Item.Properties().stacksTo(1)));
	public static final Item sawDustItem = Registry.register(BuiltInRegistries.ITEM, Id.of("saw_dust"), new Item(new Item.Properties()));
	public static final Item scouredLeatherItem = Registry.register(BuiltInRegistries.ITEM, Id.of("scoured_leather"), new Item(new Item.Properties()));
	public static final Item screwItem = Registry.register(BuiltInRegistries.ITEM, Id.of("screw"), new Item(new Item.Properties()));
    public static final Item soapItem = Registry.register(BuiltInRegistries.ITEM, Id.of("soap"), new Item(new Item.Properties()));
    public static final Item soulDustItem = Registry.register(BuiltInRegistries.ITEM, Id.of("soul_dust"), new Item(new Item.Properties()));
	public static final Item soulUrnItem = Registry.register(BuiltInRegistries.ITEM, Id.of("soul_urn"), new SoulUrnItem(new Item.Properties()));
	public static final Item strapItem = Registry.register(BuiltInRegistries.ITEM, Id.of("strap"), new Item(new Item.Properties()));
	public static final Item tallowItem = Registry.register(BuiltInRegistries.ITEM, Id.of("tallow"), new Item(new Item.Properties()));
	public static final Item tannedLeatherItem = Registry.register(BuiltInRegistries.ITEM, Id.of("tanned_leather"), new Item(new Item.Properties()));
//	public static final Item tannedLeatherBootsItem = Registry.register(Registries.ITEM, Id.of("tanned_leather_boots"), new TannedLeatherBootsItem(new Item.Settings()));
//	public static final Item tannedLeatherCapItem = Registry.register(Registries.ITEM, Id.of("tanned_leather_cap"), new TannedLeatherCapItem(new Item.Settings()));
//	public static final Item tannedLeatherPantsItem = Registry.register(Registries.ITEM, Id.of("tanned_leather_pants"), new TannedLeatherPantsItem(new Item.Settings()));
//	public static final Item tannedLeatherTunicItem = Registry.register(Registries.ITEM, Id.of("tanned_leather_tunic"), new TannedLeatherTunicItem(new Item.Settings()));
    public static final Item waterWheelItem = Registry.register(BuiltInRegistries.ITEM, Id.of("water_wheel"), new HorizontalMechPowerSourceItem(
            WaterWheelEntity::new,
            new Item.Properties().stacksTo(1)
    ));
    public static final Item windmillItem = Registry.register(BuiltInRegistries.ITEM, Id.of("windmill"), new HorizontalMechPowerSourceItem(
            WindmillEntity::new,
            new Item.Properties().stacksTo(1)
    ));
	public static final Item wolfChopItem = Registry.register(BuiltInRegistries.ITEM, Id.of("wolf_chop"), new Item(
            new Item.Properties().food(Foods.PORKCHOP))
    );
	public static final Item woodBladeItem = Registry.register(BuiltInRegistries.ITEM, Id.of("wood_blade"), new Item(new Item.Properties()));

    @Override
    public void onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
            content.addAfter(Items.NETHERITE_PICKAXE, BwtItems.netheriteMattockItem);
            content.addAfter(Items.NETHERITE_AXE, BwtItems.netheriteBattleAxeItem);
//            content.addAfter(Items.WATER_BUCKET, cementBucketItem);
//            content.add(breedingHarnessItem);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> {
            content.addAfter(Items.NETHERITE_AXE, BwtItems.netheriteBattleAxeItem);

            content.addAfter(Items.BOW, compositeBowItem);
            content.addAfter(Items.ARROW, broadheadArrowItem, rottedArrowItem);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
            content.accept(windmillItem);
            content.accept(waterWheelItem);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(content -> {
            content.addAfter(Items.COOKED_PORKCHOP, wolfChopItem);
            content.addAfter(wolfChopItem, cookedWolfChopItem);
            content.addAfter(Items.BREAD, donutItem);
            content.accept(kibbleItem);
            content.addAfter(Items.DRIED_KELP, rawEggItem, poachedEggItem, friedEggItem);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(content -> {
            content.addAfter(Items.WHEAT_SEEDS, hempSeedsItem);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(content -> {
            content.addAfter(Items.WHEAT, hempItem);
            content.accept(hempFiberItem);
            content.accept(dungItem);
            content.accept(ropeItem);
            content.accept(gearItem);
            content.accept(flourItem);
            content.accept(scouredLeatherItem);
            content.accept(tannedLeatherItem);
            content.accept(filamentItem);
            content.accept(fabricItem);
            content.accept(sailItem);
            content.accept(groundNetherrackItem);
            content.accept(sawDustItem);
            content.accept(soulDustItem);
            content.accept(hellfireDustItem);
            content.accept(concentratedHellfireItem);
            content.accept(potashItem);
            content.accept(coalDustItem);
            content.accept(broadheadItem);
            content.accept(nethercoalItem);
            content.accept(redstoneEyeItem);
            content.accept(haftItem);
            content.accept(armorPlateItem);
            content.accept(dynamiteItem);
            content.accept(glueItem);
            content.accept(paddingItem);
            content.accept(screwItem);
            content.accept(strapItem);
            content.accept(beltItem);
            content.accept(soulUrnItem);
            content.accept(soapItem);
            content.accept(tallowItem);
            content.accept(woodBladeItem);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(content -> {
            content.addAfter(Items.GLOW_ITEM_FRAME, canvasItem);
            content.getContext().holders()
                    .lookup(Registries.PAINTING_VARIANT)
                    .ifPresent(
                            registryWrapper -> addCanvases(
                                    content,
                                    content.getContext().holders(),
                                    registryWrapper,
                                    registryEntry -> registryEntry.is(BwtPaintingVariantTags.CANVAS_PLACEABLE)
                            )
                    );
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.OP_BLOCKS).register(content -> {
            content.getContext().holders()
                    .lookup(Registries.PAINTING_VARIANT)
                    .ifPresent(
                            registryWrapper -> addCanvases(
                                    content,
                                    content.getContext().holders(),
                                    registryWrapper,
                                    registryEntry -> !registryEntry.is(BwtPaintingVariantTags.CANVAS_PLACEABLE)
                            )
                    );
        });
    }

    public void replaceItem(FabricItemGroupEntries content, ItemLike itemToReplace, ItemLike newItem) {
        Item anchorItem = itemToReplace.asItem();
        for (List<ItemStack> addTo : List.of(content.getDisplayStacks(), content.getSearchTabStacks())) {
            for (int i = 0; i < addTo.size(); i++) {
                if (addTo.get(i).is(anchorItem)) {
                    addTo.set(i, new ItemStack(newItem));
                    break;
                }
            }
        }

        content.accept(newItem);
    }

    private static void addCanvases(
            FabricItemGroupEntries entries,
            HolderLookup.Provider registryLookup,
            HolderLookup.RegistryLookup<PaintingVariant> registryWrapper,
            Predicate<Holder<PaintingVariant>> filter
    ) {
        RegistryOps<Tag> registryOps = registryLookup.createSerializationContext(NbtOps.INSTANCE);
        registryWrapper.listElements()
                .filter(filter)
                .sorted(Comparator.comparing(
                        Holder::value,
                        Comparator.comparingInt(PaintingVariant::area).thenComparing(PaintingVariant::width)
                ))
                .forEach(
                        canvasVariantEntry -> {
                            CustomData nbtComponent = CustomData.EMPTY
                                    .update(registryOps, CanvasEntity.VARIANT_MAP_CODEC, canvasVariantEntry)
                                    .getOrThrow()
                                    .update(nbt -> nbt.putString("id", "bwt:canvas"));
                            ItemStack itemStack = new ItemStack(canvasItem);
                            itemStack.set(DataComponents.ENTITY_DATA, nbtComponent);
                            entries.addAfter(canvasItem, itemStack);
                        }
                );
    }
}
