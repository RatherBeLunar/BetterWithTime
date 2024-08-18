package com.bwt.items;

import com.bwt.blocks.BwtBlocks;
import com.bwt.entities.WaterWheelEntity;
import com.bwt.entities.WindmillEntity;
import com.bwt.items.components.ArcaneEnchantmentComponent;
import com.bwt.items.components.BwtDataComponents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BwtItems implements ModInitializer {
    public static final Item cementBucketItem = Registry.register(Registries.ITEM, new Identifier("bwt", "cement_bucket"), new CementBucketItem(new Item.Settings()));
	public static final Item armorPlateItem = Registry.register(Registries.ITEM, new Identifier("bwt", "armor_plate"), new Item(new Item.Settings()));
	public static final Item beltItem = Registry.register(Registries.ITEM, new Identifier("bwt", "belt"), new Item(new Item.Settings()));
	public static final Item breedingHarnessItem = Registry.register(Registries.ITEM, new Identifier("bwt", "breeding_harness"), new Item(new Item.Settings()));
	public static final Item broadheadItem = Registry.register(Registries.ITEM, new Identifier("bwt", "broadhead"), new Item(new Item.Settings()));
	public static final Item broadheadArrowItem = Registry.register(Registries.ITEM, new Identifier("bwt", "broadhead_arrow"), new BroadheadArrowItem(new Item.Settings()));
//	public static final Item candleItem = Registry.register(Registries.ITEM, new Identifier("bwt", "candle"), new CandleItem(new Item.Settings()));
	public static final Item canvasItem = Registry.register(Registries.ITEM, new Identifier("bwt", "canvas"), new Item(new Item.Settings()));
	public static final Item coalDustItem = Registry.register(Registries.ITEM, new Identifier("bwt", "coal_dust"), new Item(new Item.Settings()));
	public static final Item compositeBowItem = Registry.register(Registries.ITEM, new Identifier("bwt", "composite_bow"), new CompositeBowItem(new Item.Settings().maxDamage(576)));
	public static final Item concentratedHellfireItem = Registry.register(Registries.ITEM, new Identifier("bwt", "concentrated_hellfire"), new Item(new Item.Settings()));
    public static final Item cookedWolfChopItem = Registry.register(Registries.ITEM, new Identifier("bwt", "cooked_wolf_chop"), new Item(
            new Item.Settings()
                    .food(FoodComponents.COOKED_PORKCHOP))
    );
	public static final Item donutItem = Registry.register(Registries.ITEM, new Identifier("bwt", "donut"), new Item(new Item.Settings()
            .food(new FoodComponent.Builder()
                    .nutrition(1)
                    .saturationModifier(0.5f)
                    .snack()
                    .alwaysEdible()
                    .build())
    ));
	public static final DyeItem dungItem = Registry.register(Registries.ITEM, new Identifier("bwt", "dung"), new DungItem(new Item.Settings()));
	public static final Item dynamiteItem = Registry.register(Registries.ITEM, new Identifier("bwt", "dynamite"), new DynamiteItem(new Item.Settings()));
//	public static final Item enderSpectaclesItem = Registry.register(Registries.ITEM, new Identifier("bwt", "ender_spectacles"), new EnderSpectaclesItem(new Item.Settings()));
	public static final Item fabricItem = Registry.register(Registries.ITEM, new Identifier("bwt", "fabric"), new Item(new Item.Settings()));
	public static final Item filamentItem = Registry.register(Registries.ITEM, new Identifier("bwt", "filament"), new Item(new Item.Settings()));
	public static final Item flourItem = Registry.register(Registries.ITEM, new Identifier("bwt", "flour"), new Item(new Item.Settings()));
	public static final Item foulFoodItem = Registry.register(Registries.ITEM, new Identifier("bwt", "foul_food"), new Item(new Item.Settings()
            .food(new FoodComponent.Builder()
                    .nutrition(1)
                    .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 20 * 30, 0), 0.8f)
                    .build())
    ));
    public static final Item friedEggItem = Registry.register(Registries.ITEM, new Identifier("bwt", "fried_egg"), new Item(new Item.Settings().food(new FoodComponent.Builder().nutrition(3).saturationModifier(0.25f).build())));
//	public static final Item fuseItem = Registry.register(Registries.ITEM, new Identifier("bwt", "fuse"), new FuseItem(new Item.Settings()));
	public static final Item gearItem = Registry.register(Registries.ITEM, new Identifier("bwt", "gear"), new Item(new Item.Settings()));
	public static final Item glueItem = Registry.register(Registries.ITEM, new Identifier("bwt", "glue"), new Item(new Item.Settings()));
	public static final Item groundNetherrackItem = Registry.register(Registries.ITEM, new Identifier("bwt", "ground_netherrack"), new Item(new Item.Settings()));
	public static final Item haftItem = Registry.register(Registries.ITEM, new Identifier("bwt", "haft"), new Item(new Item.Settings()));
	public static final Item hellfireDustItem = Registry.register(Registries.ITEM, new Identifier("bwt", "hellfire_dust"), new Item(new Item.Settings()));
	public static final Item hempFiberItem = Registry.register(Registries.ITEM, new Identifier("bwt", "hemp_fiber"), new Item(new Item.Settings()));
	public static final Item hempItem = Registry.register(Registries.ITEM, new Identifier("bwt", "hemp"), new Item(new Item.Settings()));
	public static final Item hempSeedsItem = Registry.register(Registries.ITEM, new Identifier("bwt", "hemp_seeds"), new HempSeedsItem(BwtBlocks.hempCropBlock, new Item.Settings()));
	public static final Item kibbleItem = Registry.register(Registries.ITEM, new Identifier("bwt", "kibble"), new Item(new Item.Settings()));
	public static final Item mouldItem = Registry.register(Registries.ITEM, new Identifier("bwt", "mould"), new Item(new Item.Settings()));
//	public static final Item netherBrickItem = Registry.register(Registries.ITEM, new Identifier("bwt", "nether_brick"), new NetherBrickItem(new Item.Settings()));
	public static final Item netherSludgeItem = Registry.register(Registries.ITEM, new Identifier("bwt", "nether_sludge"), new Item(new Item.Settings()));
	public static final Item nethercoalItem = Registry.register(Registries.ITEM, new Identifier("bwt", "nethercoal"), new Item(new Item.Settings()));
//	public static final Item nitreItem = Registry.register(Registries.ITEM, new Identifier("bwt", "nitre"), new NitreItem(new Item.Settings()));
	public static final Item paddingItem = Registry.register(Registries.ITEM, new Identifier("bwt", "padding"), new Item(new Item.Settings()));
	public static final Item poachedEggItem = Registry.register(Registries.ITEM, new Identifier("bwt", "poached_egg"), new Item(new Item.Settings().food(new FoodComponent.Builder().nutrition(3).saturationModifier(0.25f).build())));
	public static final Item potashItem = Registry.register(Registries.ITEM, new Identifier("bwt", "potash"), new Item(new Item.Settings()));
    public static final Item rawEggItem = Registry.register(Registries.ITEM, new Identifier("bwt", "raw_egg"), new Item(new Item.Settings().food(new FoodComponent.Builder().nutrition(2).saturationModifier(0.25f).build())));
    public static final Item redstoneEyeItem = Registry.register(Registries.ITEM, new Identifier("bwt", "redstone_eye"), new Item(new Item.Settings()));
    public static final Item netheriteMattockItem = Registry.register(Registries.ITEM, new Identifier("bwt", "netherite_mattock"), new MattockItem(ToolMaterials.NETHERITE, new Item.Settings().fireproof().attributeModifiers(PickaxeItem.createAttributeModifiers(ToolMaterials.NETHERITE, 1, -3.0f))));
    public static final Item netheriteBattleAxeItem = Registry.register(Registries.ITEM, new Identifier("bwt", "netherite_battle_axe"), new BattleAxeItem(ToolMaterials.NETHERITE, new Item.Settings().fireproof().attributeModifiers(AxeItem.createAttributeModifiers(ToolMaterials.NETHERITE, 3, -2.4f))));
	public static final Item ropeItem = Registry.register(Registries.ITEM, new Identifier("bwt", "rope"), new RopeItem(new Item.Settings()));
	public static final Item rottedArrowItem = Registry.register(Registries.ITEM, new Identifier("bwt", "rotted_arrow"), new RottedArrowItem(new Item.Settings()));
	public static final Item sailItem = Registry.register(Registries.ITEM, new Identifier("bwt", "sail"), new Item(new Item.Settings().maxCount(1)));
	public static final Item sawDustItem = Registry.register(Registries.ITEM, new Identifier("bwt", "saw_dust"), new Item(new Item.Settings()));
	public static final Item scouredLeatherItem = Registry.register(Registries.ITEM, new Identifier("bwt", "scoured_leather"), new Item(new Item.Settings()));
	public static final Item screwItem = Registry.register(Registries.ITEM, new Identifier("bwt", "screw"), new Item(new Item.Settings()));
    public static final Item soapItem = Registry.register(Registries.ITEM, new Identifier("bwt", "soap"), new Item(new Item.Settings()));
    public static final Item soulDustItem = Registry.register(Registries.ITEM, new Identifier("bwt", "soul_dust"), new Item(new Item.Settings()));
	public static final Item soulUrnItem = Registry.register(Registries.ITEM, new Identifier("bwt", "soul_urn"), new SoulUrnItem(new Item.Settings()));
	public static final Item strapItem = Registry.register(Registries.ITEM, new Identifier("bwt", "strap"), new Item(new Item.Settings()));
	public static final Item tallowItem = Registry.register(Registries.ITEM, new Identifier("bwt", "tallow"), new Item(new Item.Settings()));
	public static final Item tannedLeatherItem = Registry.register(Registries.ITEM, new Identifier("bwt", "tanned_leather"), new Item(new Item.Settings()));
//	public static final Item tannedLeatherBootsItem = Registry.register(Registries.ITEM, new Identifier("bwt", "tanned_leather_boots"), new TannedLeatherBootsItem(new Item.Settings()));
//	public static final Item tannedLeatherCapItem = Registry.register(Registries.ITEM, new Identifier("bwt", "tanned_leather_cap"), new TannedLeatherCapItem(new Item.Settings()));
//	public static final Item tannedLeatherPantsItem = Registry.register(Registries.ITEM, new Identifier("bwt", "tanned_leather_pants"), new TannedLeatherPantsItem(new Item.Settings()));
//	public static final Item tannedLeatherTunicItem = Registry.register(Registries.ITEM, new Identifier("bwt", "tanned_leather_tunic"), new TannedLeatherTunicItem(new Item.Settings()));
    public static final Item waterWheelItem = Registry.register(Registries.ITEM, new Identifier("bwt", "water_wheel"), new HorizontalMechPowerSourceItem(
            WaterWheelEntity::new,
            new Item.Settings().maxCount(1)
    ));
    public static final Item windmillItem = Registry.register(Registries.ITEM, new Identifier("bwt", "windmill"), new HorizontalMechPowerSourceItem(
            WindmillEntity::new,
            new Item.Settings().maxCount(1)
    ));
	public static final Item wolfChopItem = Registry.register(Registries.ITEM, new Identifier("bwt", "wolf_chop"), new Item(
            new Item.Settings().food(FoodComponents.PORKCHOP))
    );
	public static final Item woodBladeItem = Registry.register(Registries.ITEM, new Identifier("bwt", "wood_blade"), new Item(new Item.Settings()));
    public static final ArcaneTomeItem arcaneTome = Registry.register(Registries.ITEM, new Identifier("bwt", "arcane_tome"), new ArcaneTomeItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).component(BwtDataComponents.ARCANE_ENCHANTMENT_COMPONENT, ArcaneEnchantmentComponent.DEFAULT).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)));

    @Override
    public void onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.addAfter(Items.NETHERITE_PICKAXE, BwtItems.netheriteMattockItem);
            content.addAfter(Items.NETHERITE_AXE, BwtItems.netheriteBattleAxeItem);
            content.addAfter(Items.WATER_BUCKET, cementBucketItem);
            content.add(breedingHarnessItem);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.addAfter(Items.NETHERITE_AXE, BwtItems.netheriteBattleAxeItem);

            content.addAfter(Items.BOW, compositeBowItem);
            content.addAfter(Items.ARROW, broadheadArrowItem, rottedArrowItem);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
            content.add(windmillItem);
            content.add(waterWheelItem);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> {
            content.addAfter(Items.COOKED_PORKCHOP, wolfChopItem);
            content.addAfter(wolfChopItem, cookedWolfChopItem);
            content.addAfter(Items.BREAD, donutItem);
            content.add(kibbleItem);
            content.addAfter(Items.DRIED_KELP, rawEggItem, poachedEggItem, friedEggItem);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(content -> {
            content.addAfter(Items.WHEAT_SEEDS, hempSeedsItem);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
            content.addAfter(Items.WHEAT, hempItem);
            content.add(hempFiberItem);
            content.add(dungItem);
            content.add(ropeItem);
            content.add(gearItem);
            content.add(flourItem);
            content.add(scouredLeatherItem);
            content.add(tannedLeatherItem);
            content.add(filamentItem);
            content.add(fabricItem);
            content.add(sailItem);
            content.add(groundNetherrackItem);
            content.add(soulDustItem);
            content.add(potashItem);
            content.add(coalDustItem);
            content.add(broadheadItem);
            content.add(nethercoalItem);
            content.add(redstoneEyeItem);
            content.add(haftItem);
            content.add(armorPlateItem);
            content.add(dynamiteItem);
            content.add(glueItem);
            content.add(mouldItem);
            content.add(netherSludgeItem);
            content.add(paddingItem);
            content.add(screwItem);
            content.add(strapItem);
            content.add(beltItem);
            content.add(soulUrnItem);
            content.add(soapItem);
            content.add(tallowItem);
            content.add(woodBladeItem);
            ArcaneTomeItem.forEnchantments().forEach(content::add);
        });
    }

    public void replaceItem(FabricItemGroupEntries content, ItemConvertible itemToReplace, ItemConvertible newItem) {
        Item anchorItem = itemToReplace.asItem();
        for (List<ItemStack> addTo : List.of(content.getDisplayStacks(), content.getSearchTabStacks())) {
            for (int i = 0; i < addTo.size(); i++) {
                if (addTo.get(i).isOf(anchorItem)) {
                    addTo.set(i, new ItemStack(newItem));
                    break;
                }
            }
        }

        content.add(newItem);
    }
}
