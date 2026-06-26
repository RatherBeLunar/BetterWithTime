package com.bwt;

import com.bwt.features.BwtConfiguredFeatures;
import com.bwt.generation.*;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;


public class BetterWithTimeDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack programmerArtPack = generator.createBuiltinResourcePack(Id.PROGRAMMER_ART_PACK_ID);
        programmerArtPack.addProvider(ProgrammerArtModelGenerator::new);
        
        FabricDataGenerator.Pack pack = generator.createPack();

		BlockTagGenerator blockTagGenerator = pack.addProvider(BlockTagGenerator::new);
		pack.addProvider((output, completableFuture) -> new ItemTagGenerator(output, completableFuture, blockTagGenerator));
        pack.addProvider(FluidTagGenerator::new);
		pack.addProvider(EntityTypeTagGenerator::new);
		pack.addProvider(RecipeGenerator::new);
		pack.addProvider(ModelGenerator::new);
		pack.addProvider(LangGenerator::new);
		pack.addProvider(BlockLootTableGenerator::new);
		pack.addProvider(EntityLootTableGenerator::new);
		pack.addProvider(AdvancementsGenerator::new);
		pack.addProvider(BwtConfiguredFeatures::new);
	}
}
