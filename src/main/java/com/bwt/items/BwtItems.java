package com.bwt.items;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BwtItems implements ModInitializer {
    public static final Item cementBucketItem = Registry.register(Registries.ITEM, new Identifier("bwt", "cement_bucket"), new CementBucketItem(new FabricItemSettings()));
//	public static final Item armorPlateItem = Registry.register(Registries.ITEM, new Identifier("bwt", "armor_plate"), new ArmorPlateItem(new FabricItemSettings()));
//	public static final Item beltItem = Registry.register(Registries.ITEM, new Identifier("bwt", "belt"), new BeltItem(new FabricItemSettings()));
//	public static final Item broadheadItem = Registry.register(Registries.ITEM, new Identifier("bwt", "broadhead"), new BroadheadItem(new FabricItemSettings()));
//	public static final Item broadheadArrowItem = Registry.register(Registries.ITEM, new Identifier("bwt", "broadhead_arrow"), new BroadheadArrowItem(new FabricItemSettings()));
//	public static final Item candleItem = Registry.register(Registries.ITEM, new Identifier("bwt", "candle"), new CandleItem(new FabricItemSettings()));
//	public static final Item coalDustItem = Registry.register(Registries.ITEM, new Identifier("bwt", "coal_dust"), new CoalDustItem(new FabricItemSettings()));
//	public static final Item compositeBowItem = Registry.register(Registries.ITEM, new Identifier("bwt", "composite_bow"), new CompositeBowItem(new FabricItemSettings()));
//	public static final Item concentratedHellfireItem = Registry.register(Registries.ITEM, new Identifier("bwt", "concentrated_hellfire"), new ConcentratedHellfireItem(new FabricItemSettings()));
//	public static final Item donutItem = Registry.register(Registries.ITEM, new Identifier("bwt", "donut"), new DonutItem(new FabricItemSettings()));
//	public static final Item dungItem = Registry.register(Registries.ITEM, new Identifier("bwt", "dung"), new DungItem(new FabricItemSettings()));
//	public static final Item dynamiteItem = Registry.register(Registries.ITEM, new Identifier("bwt", "dynamite"), new DynamiteItem(new FabricItemSettings()));
//	public static final Item elementItem = Registry.register(Registries.ITEM, new Identifier("bwt", "element"), new ElementItem(new FabricItemSettings()));
//	public static final Item enderSpectaclesItem = Registry.register(Registries.ITEM, new Identifier("bwt", "ender_spectacles"), new EnderSpectaclesItem(new FabricItemSettings()));
//	public static final Item fabricItem = Registry.register(Registries.ITEM, new Identifier("bwt", "fabric"), new FabricItem(new FabricItemSettings()));
//	public static final Item filamentItem = Registry.register(Registries.ITEM, new Identifier("bwt", "filament"), new FilamentItem(new FabricItemSettings()));
//	public static final Item flourItem = Registry.register(Registries.ITEM, new Identifier("bwt", "flour"), new FlourItem(new FabricItemSettings()));
//	public static final Item foulFoodItem = Registry.register(Registries.ITEM, new Identifier("bwt", "foul_food"), new FoulFoodItem(new FabricItemSettings()));
//	public static final Item fuseItem = Registry.register(Registries.ITEM, new Identifier("bwt", "fuse"), new FuseItem(new FabricItemSettings()));
//	public static final Item gearItem = Registry.register(Registries.ITEM, new Identifier("bwt", "gear"), new GearItem(new FabricItemSettings()));
//	public static final Item glueItem = Registry.register(Registries.ITEM, new Identifier("bwt", "glue"), new GlueItem(new FabricItemSettings()));
//	public static final Item groundNetherrackItem = Registry.register(Registries.ITEM, new Identifier("bwt", "ground_netherrack"), new GroundNetherrackItem(new FabricItemSettings()));
//	public static final Item haftItem = Registry.register(Registries.ITEM, new Identifier("bwt", "haft"), new HaftItem(new FabricItemSettings()));
//	public static final Item hellfireDustItem = Registry.register(Registries.ITEM, new Identifier("bwt", "hellfire_dust"), new HellfireDustItem(new FabricItemSettings()));
//	public static final Item hempFiberItem = Registry.register(Registries.ITEM, new Identifier("bwt", "hemp_fiber"), new HempFiberItem(new FabricItemSettings()));
//	public static final Item hempSeedItem = Registry.register(Registries.ITEM, new Identifier("bwt", "hemp_seed"), new HempSeedItem(new FabricItemSettings()));
//	public static final Item kibbleItem = Registry.register(Registries.ITEM, new Identifier("bwt", "kibble"), new KibbleItem(new FabricItemSettings()));
//	public static final Item mouldItem = Registry.register(Registries.ITEM, new Identifier("bwt", "mould"), new MouldItem(new FabricItemSettings()));
//	public static final Item netherBrickItem = Registry.register(Registries.ITEM, new Identifier("bwt", "nether_brick"), new NetherBrickItem(new FabricItemSettings()));
//	public static final Item nethercoalItem = Registry.register(Registries.ITEM, new Identifier("bwt", "nethercoal"), new NethercoalItem(new FabricItemSettings()));
//	public static final Item netherSludgeItem = Registry.register(Registries.ITEM, new Identifier("bwt", "nether_sludge"), new NetherSludgeItem(new FabricItemSettings()));
//	public static final Item nitreItem = Registry.register(Registries.ITEM, new Identifier("bwt", "nitre"), new NitreItem(new FabricItemSettings()));
//	public static final Item paddingItem = Registry.register(Registries.ITEM, new Identifier("bwt", "padding"), new PaddingItem(new FabricItemSettings()));
//	public static final Item plateArmorItem = Registry.register(Registries.ITEM, new Identifier("bwt", "plate_armor"), new PlateArmorItem(new FabricItemSettings()));
//	public static final Item potashItem = Registry.register(Registries.ITEM, new Identifier("bwt", "potash"), new PotashItem(new FabricItemSettings()));
//	public static final Item redstoneEyeItem = Registry.register(Registries.ITEM, new Identifier("bwt", "redstone_eye"), new RedstoneEyeItem(new FabricItemSettings()));
//	public static final Item refinedToolsItem = Registry.register(Registries.ITEM, new Identifier("bwt", "refined_tools"), new RefinedToolsItem(new FabricItemSettings()));
//	public static final Item rottedArrowItem = Registry.register(Registries.ITEM, new Identifier("bwt", "rotted_arrow"), new RottedArrowItem(new FabricItemSettings()));
//	public static final Item sailItem = Registry.register(Registries.ITEM, new Identifier("bwt", "sail"), new SailItem(new FabricItemSettings()));
//	public static final Item sawDustItem = Registry.register(Registries.ITEM, new Identifier("bwt", "saw_dust"), new SawDustItem(new FabricItemSettings()));
//	public static final Item scouredLeatherItem = Registry.register(Registries.ITEM, new Identifier("bwt", "scoured_leather"), new ScouredLeatherItem(new FabricItemSettings()));
//	public static final Item soulforgedSteelItem = Registry.register(Registries.ITEM, new Identifier("bwt", "soulforged_steel"), new SoulforgedSteelItem(new FabricItemSettings()));
//	public static final Item soulDustItem = Registry.register(Registries.ITEM, new Identifier("bwt", "soul_dust"), new SoulDustItem(new FabricItemSettings()));
//	public static final Item soapItem = Registry.register(Registries.ITEM, new Identifier("bwt", "soap"), new SoapItem(new FabricItemSettings()));
//	public static final Item soulUrnItem = Registry.register(Registries.ITEM, new Identifier("bwt", "soul_urn"), new SoulUrnItem(new FabricItemSettings()));
//	public static final Item strapItem = Registry.register(Registries.ITEM, new Identifier("bwt", "strap"), new StrapItem(new FabricItemSettings()));
//	public static final Item tallowItem = Registry.register(Registries.ITEM, new Identifier("bwt", "tallow"), new TallowItem(new FabricItemSettings()));
//	public static final Item tannedLeatherItem = Registry.register(Registries.ITEM, new Identifier("bwt", "tanned_leather"), new TannedLeatherItem(new FabricItemSettings()));
//	public static final Item tannedLeatherBootsItem = Registry.register(Registries.ITEM, new Identifier("bwt", "tanned_leather_boots"), new TannedLeatherBootsItem(new FabricItemSettings()));
//	public static final Item tannedLeatherCapItem = Registry.register(Registries.ITEM, new Identifier("bwt", "tanned_leather_cap"), new TannedLeatherCapItem(new FabricItemSettings()));
//	public static final Item tannedLeatherPantsItem = Registry.register(Registries.ITEM, new Identifier("bwt", "tanned_leather_pants"), new TannedLeatherPantsItem(new FabricItemSettings()));
//	public static final Item tannedLeatherTunicItem = Registry.register(Registries.ITEM, new Identifier("bwt", "tanned_leather_tunic"), new TannedLeatherTunicItem(new FabricItemSettings()));
    public static final Item windmillItem = Registry.register(Registries.ITEM, new Identifier("bwt", "windmill"), new WindmillItem(
            new FabricItemSettings().maxCount(1)
    ));
//	public static final Item wolfChopItem = Registry.register(Registries.ITEM, new Identifier("bwt", "wolf_chop"), new WolfChopItem(new FabricItemSettings()));
//	public static final Item woodBladeItem = Registry.register(Registries.ITEM, new Identifier("bwt", "wood_blade"), new WoodBladeItem(new FabricItemSettings()));




    @Override
    public void onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> content.addAfter(net.minecraft.item.Items.WATER_BUCKET, cementBucketItem));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> content.add(windmillItem));
    }
}
