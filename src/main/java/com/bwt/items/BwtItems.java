package com.bwt.items;

import com.bwt.blocks.BwtBlocks;
import com.bwt.entities.WaterWheelEntity;
import com.bwt.entities.WindmillEntity;
import com.bwt.tags.BwtPaintingVariantTags;
import com.bwt.utils.RegistrationBase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import java.util.Comparator;
import java.util.function.Predicate;

public class BwtItems extends RegistrationBase implements ModInitializer {
    public static final Item cementBucketItem = registerItem("cement_bucket", CementBucketItem::new);
	public static final Item armorPlateItem = registerItem("armor_plate");
	public static final Item beltItem = registerItem("belt");
	public static final Item breedingHarnessItem = registerItem("breeding_harness");
	public static final Item broadheadItem = registerItem("broadhead");
	public static final Item broadheadArrowItem = registerItem("broadhead_arrow", BroadheadArrowItem::new);
//	public static final Item candleItem = Registry.register(Registries.ITEM, Id.of("candle"), new CandleItem(new Item.Settings()));
	public static final Item canvasItem = registerItem("canvas", CanvasItem::new);
	public static final Item coalDustItem = registerItem("coal_dust");
	public static final Item compositeBowItem = registerItem("composite_bow", CompositeBowItem::new, new Item.Properties().durability(576));
	public static final Item concentratedHellfireItem = registerItem("concentrated_hellfire");
    public static final Item cookedWolfChopItem = registerItem("cooked_wolf_chop", new Item.Properties().food(Foods.COOKED_PORKCHOP));
	public static final Item donutItem = registerItem(
            "donut",
            new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(1)
                            .saturationModifier(0.5f)
                            .alwaysEdible()
                            .build(),
                    Consumables.defaultFood().consumeSeconds(0.8f).build()
            )
    );
	public static final DyeItem dungItem = registerItem("dung", DungItem::new);
	public static final Item dynamiteItem = registerItem("dynamite", DynamiteItem::new, new Item.Properties().useCooldown(1.0f));
//	public static final Item enderSpectaclesItem = Registry.register(Registries.ITEM, Id.of("ender_spectacles"), new EnderSpectaclesItem(new Item.Settings()));
	public static final Item fabricItem = registerItem("fabric");
	public static final Item filamentItem = registerItem("filament");
	public static final Item flourItem = registerItem("flour");
	public static final Item foulFoodItem = registerItem("foul_food", new Item.Properties()
            .food(
                    new FoodProperties.Builder().nutrition(1).build(),
                    Consumables.defaultFood()
                            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.POISON, 20 * 30, 0), 0.8f))
                            .build()
            )
    );
    public static final Item friedEggItem = registerItem("fried_egg", new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.25f).build()));
//	public static final Item fuseItem = Registry.register(Registries.ITEM, Id.of("fuse"), new FuseItem(new Item.Settings()));
	public static final Item gearItem = registerItem("gear");
	public static final Item glueItem = registerItem("glue");
	public static final Item groundNetherrackItem = registerItem("ground_netherrack");
	public static final Item haftItem = registerItem("haft");
	public static final Item hellfireDustItem = registerItem("hellfire_dust");
	public static final Item hempFiberItem = registerItem("hemp_fiber");
	public static final Item hempItem = registerItem("hemp");
	public static final Item hempSeedsItem = registerItem("hemp_seeds", createBlockItemWithCustomItemName(BwtBlocks.hempCropBlock));
	public static final Item kibbleItem = registerItem("kibble");
//	public static final Item netherBrickItem = Registry.register(Registries.ITEM, Id.of("nether_brick"), new NetherBrickItem(new Item.Settings()));
	public static final Item nethercoalItem = registerItem("nethercoal");
//	public static final Item nitreItem = Registry.register(Registries.ITEM, Id.of("nitre"), new NitreItem(new Item.Settings()));
	public static final Item paddingItem = registerItem("padding");
	public static final Item poachedEggItem = registerItem("poached_egg", new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.25f).build()));
	public static final Item potashItem = registerItem("potash");
    public static final Item rawEggItem = registerItem("raw_egg", new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.25f).build()));
    public static final Item redstoneEyeItem = registerItem("redstone_eye");
    public static final Item netheriteMattockItem = registerItem(
            "netherite_mattock",
            properties -> new MattockItem(ToolMaterial.NETHERITE, 1, -3.0f, properties),
            new Item.Properties().fireResistant()
    );
    public static final Item netheriteBattleAxeItem = registerItem(
            "netherite_battle_axe",
             properties -> new BattleAxeItem(ToolMaterial.NETHERITE, 3, -2.4f, properties),
            new Item.Properties().fireResistant()
    );
	public static final Item ropeItem = registerItem("rope", RopeItem::new);
	public static final Item rottedArrowItem = registerItem("rotted_arrow", RottedArrowItem::new);
	public static final Item sailItem = registerItem("sail", new Item.Properties().stacksTo(1));
	public static final Item sawDustItem = registerItem("saw_dust");
	public static final Item scouredLeatherItem = registerItem("scoured_leather");
	public static final Item screwItem = registerItem("screw");
    public static final Item soapItem = registerItem("soap");
    public static final Item soulDustItem = registerItem("soul_dust");
	public static final Item soulUrnItem = registerItem("soul_urn", SoulUrnItem::new);
	public static final Item strapItem = registerItem("strap");
	public static final Item tallowItem = registerItem("tallow");
	public static final Item tannedLeatherItem = registerItem("tanned_leather");
//	public static final Item tannedLeatherBootsItem = Registry.register(Registries.ITEM, Id.of("tanned_leather_boots"), new TannedLeatherBootsItem(new Item.Settings()));
//	public static final Item tannedLeatherCapItem = Registry.register(Registries.ITEM, Id.of("tanned_leather_cap"), new TannedLeatherCapItem(new Item.Settings()));
//	public static final Item tannedLeatherPantsItem = Registry.register(Registries.ITEM, Id.of("tanned_leather_pants"), new TannedLeatherPantsItem(new Item.Settings()));
//	public static final Item tannedLeatherTunicItem = Registry.register(Registries.ITEM, Id.of("tanned_leather_tunic"), new TannedLeatherTunicItem(new Item.Settings()));
    public static final Item waterWheelItem = registerItem(
            "water_wheel",
            properties -> new HorizontalMechPowerSourceItem(WaterWheelEntity::new, properties),
            new Item.Properties().stacksTo(1)
    );
    public static final Item windmillItem = registerItem(
            "windmill",
            properties -> new HorizontalMechPowerSourceItem(WindmillEntity::new, properties),
            new Item.Properties().stacksTo(1)
    );
	public static final Item wolfChopItem = registerItem("wolf_chop", new Item.Properties().food(Foods.PORKCHOP));
	public static final Item woodBladeItem = registerItem("wood_blade");

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
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(content -> content.addAfter(Items.WHEAT_SEEDS, hempSeedsItem));
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
                                    registryWrapper,
                                    registryEntry -> registryEntry.is(BwtPaintingVariantTags.CANVAS_PLACEABLE)
                            )
                    );
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.OP_BLOCKS).register(content ->
            content.getContext().holders()
                .lookup(Registries.PAINTING_VARIANT)
                .ifPresent(
                        registryWrapper -> addCanvases(
                                content,
                                registryWrapper,
                                registryEntry -> !registryEntry.is(BwtPaintingVariantTags.CANVAS_PLACEABLE)
                        )
                ));
    }

    private static void addCanvases(
            FabricItemGroupEntries entries,
            HolderLookup.RegistryLookup<PaintingVariant> registryLookup,
            Predicate<Holder<PaintingVariant>> filter
    ) {
        registryLookup.listElements()
                .filter(filter)
                .sorted(Comparator.comparing(
                        Holder::value,
                        Comparator.comparingInt(PaintingVariant::area).thenComparing(PaintingVariant::width)
                ))
                .forEach(reference -> {
                    ItemStack itemStack = new ItemStack(canvasItem);
                    itemStack.set(DataComponents.PAINTING_VARIANT, reference);
                    entries.addAfter(canvasItem, itemStack);
                });

    }
}
