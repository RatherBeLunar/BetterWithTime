# Release 2.0 for MC 1.21.1

### Major Changes

- BWT has a whole new look! Stohun's textures are now the default.
- The previous textures (mostly FC originals) remain available as a built-in resource pack
- Added the **Canvas** item, with 3 gorgeous new paintings made by Stohun, and more on the way!
- Added **Nether Groth**
  - Thanks to ivangeevo for the initial bits of code on this. I rewrote most of it, but having the initial code got me through the mental block to work on this
- Added the **Redstone Clutch**
  - All existing gear boxes will no longer respond to redstone. Any blocks in existing worlds that used redstone breaking will need to be replaced with redstone clutches
- Made decorated pots a pottery item
  - Unfired decorated pots come before crucibles in the turntable chain, and do not drop a clay ball when formed (making their cost 4 clay balls / 1 clay block)
  - Pottery sherds can be applied to the sides of the unfired pot by right clicking on a side, or using a Dispenser or Block Dispenser
  - When cooked in the kiln, the result will be a decorated pot with the same set of sherds
  - Unfired pots with sherds cannot be pushed by pistons because they're "too fragile" (block entities), but unfired pots without sherds can still be pushed by pistons
  - Disabled vanilla recipes for decorated pots
- Made flower pots a pottery item
  - Unfired flower pots come after urns in the turntable chain, replacing the mould item (RIP moulds)
  - Disabled vanilla recipe for flower pots
- Lenses can now fire and propagate their beams through glass just as they fire through air
- Rebalanced vanilla hoppers
  - Vanilla hoppers still work as normal, but they're now made from netherite/steel instead of iron.
  - The new recipe now also yields 2 hoppers instead of 1
  - The intent behind this change is to make BWT hoppers more enticing for certain use cases, and further incentivize netherite automation
  - The gamerule to tweak vanilla hopper behavior has been removed
- Block Dispensers can now be powered via weak redstone power via all sides except its face
  - This in contrast to the previous behavior of requiring strong power to it or to the block above it
  - BDs, unlike normal dispensers, do not have quasiconnectivity after this change
  - BDs not being powered via their face is similar to how pistons are powered. This keeps most counter/clock functionality of BDs fully intact
  - This will break existing builds that rely on BDs previously ignoring "weak" redstone power signals


### Minor Changes

- Added Mod Menu compatible links, credits, and attributions.
- Made Observers pulse when placed by a Block Dispenser. This is intended to make BD contraptions able to generate timed pulses more easily while phasing out the original Buddy Block
- Added cauldron recipe for cake using flour in place of wheat, raw egg in place of egg. Disabled vanilla cake recipe ([#14](https://github.com/RatherBeLunar/BetterWithTime/issues/14))
- Added cauldron recipe for cookies using flour in place of wheat. Disabled vanilla cookie recipe ([#15](https://github.com/RatherBeLunar/BetterWithTime/issues/15))
- Disabled the vanilla recipe for sugar to incentivize use of the millstone
- Made baby animals jump height shorter - they cannot clear a full block, but can barely clear half a block
- Added some "better than nothing" support for EMI interaction with the cauldron and crucible.
  - Recipes shown by EMI will take the current stoked/unstoked state of the cooking pots into account
  - Clicking a recipe in EMI to fill the crafting area will add the relevant items into the pot.
Clicking multiple times will not stack the items, but rather will add items as a separate stack.
This was the best I could do in terms of making this work
- Added a ton of "default recipe" configurations so EMI can better construct recipe trees for BWT items
- Added composting values for hemp and hemp seeds
- Tweaked the high efficiency recipes for pressure plates, since they were colliding with the recombining recipes for siding
- When the player holds up food in front of pet wolves, wolves will now only tilt their head if they're unfed, i.e. wolves that don't tilt their head are waiting to produce dung
- Removed useless nether sludge item
- Added recipe for nether bricks that skips the nether sludge step, as a sink for hellfire dust and potash
- Added mini-blocks (siding, moulding, corner, column, pedestal, table) for cobblestone. No idea how I missed doing this earlier
- Added a recipe for sticky pistons using glue in place of slime
- Renamed some IDs relating to blood wood "wood" blocks (the block with bark on all sides). This will remove all existing "Blood Wood Wood" blocks in worlds.
This was necessary for some issues with recipes and compatibility


### Bugfixes
- Fixed incompatibility with AdvancementInfo Reloaded ([#11](https://github.com/RatherBeLunar/BetterWithTime/issues/11))
- Fixed a bunch of compatibility issues with EMI
- Fixed wolves forgetting that they had been fed and should produce dung when reloading the world
- Fixed some EMI interactions with the soul forge. Still not perfect, but less entirely broken.
- Fixed tiny behavior where shift clicking results out of a soul forge wouldn't try to merge with a matching stack in the player's hotbar
- Fixed double slabs placed in front of a saw dropping 4x the results of a single slab instead of 2x
- Fixed issues with the Block Dispenser when Lithium was installed
- Fixed the mechanical hopper over-inserting items into composters
- Fixed the mechanical hopper sometimes duplicating items in specific inventory transfer circumstances
- Fixed windmills not breaking their connected gearbox/clutch in thunderstorms (again)
- Fixed some issues with pulley platforms breaking into pieces when not fully supported. This doesn't mean platforms are a fully working feature yet, sadly.

# Beta 1.9.4 for MC 1.21.1

- Fixed some issues with the lens and beam updating logic
- Fixed Battle Axe not behaving properly as a tool item
- Added unverified russian language translation from Zeykehasnt
- Added some tweaks from Primetoxinz to make BWT more compatible (less incompatible) with Sinyatra Connector

# Beta 1.9.3 for MC 1.21.1

This is a purely bugfix and minor changes update to clear the way for some bigger features soon.

- Added mod logo to the internal icon so it appears in mod lists
- Added missing Block Dispenser and vanilla Dispenser behavior for arrow variants and soul urns
- Made Battle Axe actually function like both an axe and a sword - sweeping attacks work, cobwebs are cut quickly like swords, etc
- Fixed behaviors and duplication glitches for some blocks that both require a supporting block and are piston pushable (unfired pottery, hand cranks)
- Lots of tweaks to detector logic blocks behind the scenes. No longer triggered by cave air, logic block changes no longer update neighboring observers, better handling when the detector is pushed by a piston
- Made hibachis conduct redstone when strongly powered, so neighboring hibachis are more easily lit
- Made hibachis destroy flammable blocks placed directly above them, as if immediately incinerated
- Gave planter variants proper collision and outline boxes. Empty planters no longer support some blocks like redstone dust, but filled planters still do
- Made mining charges require a supporting block when placed by hand, but still placeable in the air when dispensed by a dispenser/BD.
- Fixed mining charge loot table (wasn't dropping anything before)
- Removed the mechanical hopper's redstone output when its slots are full, since this functionality is superseded by comparators
- Fixed some blocks conducting redstone through them when they shouldn't
- Disallowed blocks requiring a supporting block from being placed on top of cooking pots (cauldron/crucible)
- Made cooking pots, when tipped on their side, able to dump their contents into a block that is occupied but has no collision.
- Made collision handlers for the windmill and water wheel less janky. They should feel more solid, and less "bounce you away from their center point"

# Beta 1.9.2 for MC 1.21.1

- Soul urns can now be thrown, and its occupant will escape where it lands
- Added functionality to nethercoal - cooking time, and efficient torch and soul torch crafting
- Changed detector block to take 2 game ticks to change state instead of instantly
- Reduced tick rate of the light block from 4 ticks to 2 to give it differentiate more from the redstone lamp
- Food that wolves pick up off the ground now heals them for more health to match a recent vanilla change

### Bugfixes
- Made special BTW tools (battle axe and mattock) properly enchantable. They each gain enchantability from both of their parent tools (shovel+pick, axe+sword). Sweeping edge on the battle axe doesn't work yet though; still gotta fix that.
- Added saw dust to the creative inventory, which also fixes it not appearing in EMI
- Fix recipe remainders (e.g. buckets) getting deleted in cooking pots. No recipes in BWT were affected by this, but it improves mod compatibility
- Removed blood wood sign variants, at least temporarily. They're more complicated than I thought, and I need to do a lot more work to implement them properly
- Fixed lens beams and invisible detector logic blocks being pushable by pistons

# Beta 1.9.1 for MC 1.21.1

- Added a bunch of new Advancements and tweaked descriptions to fill out the tech tree, and hopefully provide some guidance to new players
- Made hemp un-bonemeal-able for balance purposes. It's a slow start, which makes getting your first windmill more rewarding

### Bugfixes:
- Added missing translation for saw death message
- Added some missing items to the creative menu
- Fixed saws breaking when water flowed into them

# Beta 1.9 for MC 1.21.1

- Made mob spawner mossy cobble conversion recipe based, and added conversion for cobblestone walls, slabs, and stairs.
- Changed the default value of the gamerule that disables vanilla hopper item transfer to false. If you have an existing world, you may need to change this value manually, since only the default value changed. I need to do more work to balancing vanilla hoppers before I mess with this.
- Made windmills and water wheels visible from much further away
- Added the ability to plant small flowers on top of vases
- Added improved vase textures from Stohun
- Added custom kiln block cooking overlay texture from Stohun, which also fixes the bug of only one cooking overlay being visible at once
- Added improved stoked fire modeling from Stohun

- Fixed more soul forge bugs. Let's hope it works this time!
- Fixed lenses not always updating their lit state due to the way other blocks send neighbor updates
- Fixed corner blocks not being placeable in the top halves of blocks

# Beta 1.8 for MC 1.21.1

- Added dirt slabs. @primetoxinz  was at the forefront of this, thanks prime!
- Added recycling recipe for bows in a stoked cauldron
- Bellows now properly lift players/item entities sitting on top of them when they inflate
- Changed anchor recipe to use smooth stone, to avoid a conflict with the vanilla stonecutter
- Added a high efficiency recipe for bowls using corners
- Nether wart no longer grows in the overworld
- Added many, many missing item tags for corresponding block tags. This shouldn't have had a huge impact on gameplay, but it matters for mod interactions
- Added vine traps to the creative inventory, which were previously missing
- Added more missing translation entries
- Added missing block loot drops and tool efficiency tags
- Fixed stoked fire not immediately disappearing when the hibachi below is broken
- Changed hemp crop model to be appropriately lower on farmland
- Added compatibility tag for Farmer's Delight to prevent hemp from being OP on its rich soil
- Fixed axles transferring mechanical power to their sides
- Reworked internals to make the calculation of what counts as fire below a cauldron/crucible/kiln more moddable
- Fixed gearboxes breaking in thunderstorms even when stopped with redstone power
- Fixed saws breaking on short grass. Let me know if there are more things like this that I'm missing

# Beta 1.7.2 for MC 1.21.1

This is a hotfix for [the 1.7.1 release](https://discord.com/channels/252863009590870017/1218787262666375248/1316860222714679307) which fixes another crafting bug in the soul forge

# Beta 1.7.1 for MC 1.21.1

- Added block dispenser clumping and kiln cooking recipes for raw ore blocks
- Changed unfired pottery to no longer be washed away by flowing water
- Added high efficiency recipe for the axle
- Added a new stoked fire texture made by @ikbod, and updated the model to be two high when unobstructed
- Implemented a ton of modeling/texturing fixes and improvements done by @stohun.
- Made companion cubes whine when mined, as they do in BTW
- Fixed the soul forge not working by just obliterating the inventory. If you had stored items in your soul forge, remove them before updating or they will be lost. The soul forge will later get an automated cousin, akin to vanilla MC's autocrafter block.
- Fixed some vanilla recipes that were meant to be disabled still working
- Fixed saw not playing a sound when cutting certain blocks
- Fixed a companion cube duplication glitch with the saw
- Added some missing item translation key values

# Beta 1.7 for MC 1.21.1

- Updated the mod to 1.21.1
- Updated Fabric API dependency to [0.103.0+1.21.1](https://modrinth.com/mod/fabric-api/version/0.103.0+1.21.1)
- Added [EMI support](https://modrinth.com/mod/emi/version/1.1.13+1.21.1+fabric) (Thank you @primetoxinz !)
- Added the Lens block!
- Added proper implementation of rotted arrows (skeletons shoot them and drop them on death, they shatter on hit, and composite bows can't fire them)
- Changed Stoked fire to be distinct from soul fire (though it's still bad)
- Cauldrons and Crucibles can now be tipped over with mechanical power provided to their sides. They'll dump out their items when tipped
- Fixed some bugs with foul food generation
- Fixed SoulForge recipes bleeding over into the crafting table and its recipe book

# Beta 1.6 for MC 1.20.6

- Mob spawners slowly convert nearby cobblestone to mossy cobblestone
- Disabled crafting recipes for mossy cobblestone to incentivize usage of this mechanic. Mossy cobble still generates in all sorts of new places throughout the world compared to older MC versions, so hopefully this shouldn't be too much of a barrier between the player and Block Dispensers
- Adjust tallow recipes so pork is the most efficient producer of it
- Villagers now drop XP when killed
- Wolves can now pick up dropped food from the ground when they're hurt, not just when they're hungry
- Added a *ton* more decorative block types (siding, moulding, corners, tables, columns, etc)
  - mossy stone bricks
  - red sandstone
  - diorite
  - polished diorite
  - andesite
  - polished andesite
  - granite
  - polished granite
  - cobbled deepslate
  - tuff
  - mud bricks
  - prismarine
  - end stone bricks
  - purpur
- Added the **Vine Trap** block
- Added **Blood Wood**, and all of its related blocks and behaviors
  - **Please note**: Blood wood saplings have a modern recipe! The choices made are intended to reflect how blood wood grows like an oak tree, but spreads like a fungus. I'm open to feedback on this, but I'd like to move away from the "every sapling" recipe since vanilla has so many now.
  - In an unstoked cauldron: **Oak Sapling + Nether Wart + Red Mushroom + Brown Mushroom + Crimson Fungus + Warped Fungus + 8 Soul Urns**
  - Saplings only grow in the nether, and must be planted on soul sand, soul soil, or a soul soil planter
  - Planting a sapling manually angers pigmen
  - Saplings dropped by blood wood leaves will auto-plant themselves if they land on the right type of block
  - The wood of a naturally grown tree will continue to grow and spread with time. This behavior was hard to port; let me know if there are issues or it doesn't grow!
- Fixed z-fighting on the moving rope entity that moves with platforms
- Fixed some block tags for more consistent behavior in a few places
- Fix mining charge bugs:
  - explosion offset by 1 block
  - explosion dealing too much damage
  - turning into tnt when exploded
  - responding too immediately to redstone
- Improvements to Buddy block behavior consistency

# Beta 1.5 for MC 1.20.4

- **Major rework to Netherite/Steel**. What is netherite if not a sturdy metal, forged from hell, and full of trapped souls?
- The "steel" recipe (which produces netherite instead) now takes gold as well as its previous ingredients (iron + coal dust + soul urn) to fit the vanilla theme of adding gold to get netherite
- Netherite scrap can still be gathered manually and converted to ingots in the crucible, instead of the crafting table, via its vanilla recipe ingredients (4 scrap + 4 gold)
- Netherite scrap can be made from ancient debris in the kiln
- **Buddy Block**
  - Behavior should (\*fingers crossed\*) match BTW's original implementation.
- **Pulleys now lift anchors, and connected platforms**
  - Buggy or not, here we go! This feature will *absolutely* yield bugs for you.
  - Use at your own risk for now, until I have more time to improve this code
  - Horizontal collisions are not properly handled right now, only vertical. You'll snap up one or more blocks if you walk into the side of a moving platform.
  - Rails and Redstone dust on top of platforms can be lifted
  - USE AT YOUR OWN RISK. I know this is very buggy right now!
- Wool slabs
- Several compacted / "Aesthetic Opaque" blocks for storage and decoration
- Rope block
- Padding block
- Dung block
- Wicker block
- Concentrated Hellfire block
- Soap block
- Several "Aesthetic non-opaque" blocks
- Wicker slabs
- Columns
- Pedestals
- Tables

- Dynamite
  - Requires a flint and steel or fire charge to ignite from the player's inventory
  - Ignited automatically when dispensed from a BD or dispenser
- **Dynamite Fishing**
  - Go nuts!
- Mining charges
  - Can be placed/primed in all the same ways as TNT
  - Sticks to blocks it's placed against the solid face of
  - Doesn't blow up ore and ore-adjacent item entities
  - Cobble blocks are converted to gravel when blown up
- Raw, fried, and poached eggs
- Added a ton of high efficiency recipes using the mini blocks. If you think of more of these recipes for newer blocks, please gather them in a list and let me know!
- Windmills and Water wheels now break in survival mode in the same wobbly way that things like minecarts do instead of just breaking instantly
- Soap can now clean sticky pistons

### Bugfixes
- Fixed the mod crashing when using fabric-api 0.97.0 or later
- The turntable now sends out block updates to its neighbors when it rotates. Previously, only the blocks being rotated sent updates
- Fixed detector blocks not updating properly when certain crops grew into the block in front of them (vines, in this case)
- Hand crank now only sends out block updates when it turns on and off, and not in the middle of its clicking
- Unfired pottery now requires a supporting block, to aid in automation (this is BTW behavior, which I missed before)
- BDs and dispensers can now fire broadhead arrows
- BDs in general now have a few more inherited item dispensing behaviors from dispensers (splash potions, for example)

# Beta 1.4.1

- Attempted to fix a crash with wolves picking up food off the floor. I wasn't able to reproduce it, but I believe I understand the cause and changed that piece of code
- Fixed wolves eating too much food while sitting down

# Beta 1.4

- Moved obsidian pressure plate recipe to the soul forge
- Added missing localization strings for plate armor
- Fixed composite bow model's display, particularly in the left hand
- Added companion slab to the creative inventory
- Added honey to the list of blocks without a full cube hitbox that can still transfer rotation upwards on the turntable. I turned this into a block tag, so it should be simpler to do this in the future if more cases come up
- Added a missing loot table for the soul forge
- Fixed texture for the blue vase

# Beta 1.3

## New features!
- Soul Forge / Anvil / the 4x4 crafting grid.
- Refined tools and plate armor, in the place of netherite tools+armor.
- Netherite tools aren't gone or overridden behind the scenes, just a bit harder to access by default. As always, this is easily disabled for modpacks.
- Plate armor is trimmable!
- Mattock
- Battle Axe
- In modern MC, axes already have some combat ability. The battle axe has the advantage of swinging with full power much more quickly, like a sword
- Composite Bow
- Broadhead Arrows
- I haven't really applied any balancing to these two items, so they might be *really* overpowered right now
- Several non-wood-based mini-blocks (siding, moulding, corners), craftable and recombinable in the Soul Forge
- Block Dispenser is now accessible in survival mode via recipe
- Detector Block recipe is now accessible in survival mode via recipe
- Kiln recipes for terracotta and glazed terracotta
- The following animals now seek out, walk to, and pick up nearby food on the floor:
- Wolves
- Sheep
- Pigs
- Cows
- Chickens
- When animals pick up food, they will enter breeding mode as if they were hand fed, except for wolves that are sitting down.
- When wolves pick up food, they become "fed" and will later produce dung. As long as they are in the sitting pose, they won't pick up food again until they become "un-fed" when they produce dung.
- Windmills will now properly overpower and break connected gearboxes after 30 seconds spent in a thunderstorm.

## Bugfixes / Changes
- Collide-able entities will now be pushed away from the collision boxes of windmills and water wheels. This is to help prevent the player from being pushed inside of the collision box and breaking the device.
- Added fuel values to several burnable blocks/items. Burning away your gear boxes and saws in a furnace is not recommended, but you sure can do it now.
- Added all the mini blocks to the creative inventory

# Beta 1.2

- Block Dispenser changes
  - Removed block breaking particles when sucking up a block. I could add this back, as it's consistent with the target version, but it feels like a big potential lag source
  - Changed BD power update behavior to be consistent with the target version of BTW. Very short pulses no longer trigger the BD
  - Added proper pickup behavior for beds
  - Stopped BD from sucking up an entity when it doesn't have room in its inventory (block pickup already worked this way)
  - Fixed a lot of placement checks. It should be far harder to produce invalid placement states, though I'm sure we'll find some cases that I'll add special conditions for
  - Fixed not being able to place pointed dripstone
  - Allow BDs to place minecarts and boats without rails and water respectively.
  - If a BD places a minecart on a rail while facing horizontally, it will give the minecart velocity away from the BD
  - Allow BDs to pick up item frames and armor stands. Items held by these entities will drop on the ground
  - Add proper BD pickup behavior for cocoa beans, identical to crops
- Turntable changes
  - Did a lot more tweaking of the code. I changed my approach for this code a second time, and I'm seeing some better results from this. Let me know if there are still issues, or any blocks that don't rotate the way you think they should, either on the central column or attached to sides
  - Added special cases for mud and soul sand so they can transfer rotation to the block above them
  - Added attachment rotation behavior for:
      - ladders (was broken before)
      - vines
      - glow lichen
      - bells
- Fixed some fire and gear textures in inventory screens
- Added comparator outputs to several blocks that were missing it
- Changed stone detector rail to power when any type of minecart except a normal, unoccupied one passes over it. It previously only accepted minecarts with a passenger
- Cleaned up sorting of Vases everywhere to be by color instead of semi-random (most notably in the creative inventory)
- Fixed windmills and water wheels breaking adjacent axles when broken if they had been powering in two directions at once
- Add iron door resmelting recipe in the crucible
- Added missing flour->bread furnace recipe

# Beta 1.1

- Added missing localization strings
- Added/corrected all subtitles for mod block sounds
- Fixed culling on platform blocks, though this may need further tweaking in the future
- Fixed turntable block culling
- Fixed turntable not rendering the "nub" of its slider in the inventory
- Adding support for rotating several more types of blocks on the turntable
- Changed the turntable rotation code such that rotating certain blocks produces far fewer invalid states
- Made redstone logic gate blocks (repeaters+comparators) properly update all neighboring redstone when rotated on the turntable
- Fixed block dispenser picking up many blocks it shouldn't be able to
- Fixed block dispenser not preserving nbt data when picking up certain blocks (shulker boxes, beehives...)
- Fixed block dispenser's behavior when picking up and placing 2-tall blocks (doors, flowers, grass)
- Fixed the mechanical hopper block not dropping its filter item on break
- Added a couple more missing blocks to creative tabs, though the creative tab organization still needs a lot of work
- Improved water wheel rotation calculation code
- Removed debug number that was appearing above windmills and water wheels. You will need to replace existing entities in your world for the numbers to go away
- Added all vase dyeing recipes, which were missing
- Added all the dyeing recipes I could find that use brown dye to alternatively accept dung
- Fixed kibble not working as a food for wolves

# Better With Time Beta Test 1.0

This is a limited run test for the purpose of identifying game-breaking bugs, crashes, and anything that prevents you from playing the mod as intended.

## Installation

1. This mod is for 1.20.4. First, install and run a 1.20.4 Minecraft instance.
2. Install the Fabric mod loader here: https://fabricmc.net/use/installer/
3. Download the Fabric API, a required dependency, here: https://modrinth.com/mod/fabric-api
4. Download the Better With Time jar, hopefully attached to the message after this one as long as I don't hit a file size limit
5. Move both the Fabric API jar and BWT jar into your mods folder. The Fabric website has instructions for this if you need help

## Playing the game

- Use this wiki page as a guide for how the old version of mod plays. Remember, it's very different from even 10-year old versions of BTW: https://wiki.btwce.com/index.php?title=Ages&direction=next&oldid=6304
- The vanilla recipe book should help you identify crafting recipes, but the custom crafting/cooking vessels don't have recipe book support yet. You may need to use the wiki for those. For any wiki page, use the "view history" button, and go back to no later than october 2012 for an idea of what expected behaviors and recipes are.
- You should be able to play the mod and use all of the relevant tech to obtain Soulforged Steel Ingots. If you obtain one of these, feel free to explore some of the other features, but the tech tree does not go further, so feel free to stop your test and submit feedback
- Take notes! Please! Write notes, take screenshots, take videos, stream your playthrough and invite me to watch, all of the above. Especially if you encounter any issues, and especially if you're having a good time. I really need the information to make the mod better.