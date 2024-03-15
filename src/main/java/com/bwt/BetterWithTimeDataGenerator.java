package com.bwt;

import com.bwt.generation.AdvancementsGenerator;
import com.bwt.generation.BlockTagGenerator;
import com.bwt.generation.EntityTypeTagGenerator;
import com.bwt.generation.ItemTagGenerator;
import com.bwt.generation.LangGenerator;
import com.bwt.generation.BlockLootTableGenerator;
import com.bwt.generation.ModelGenerator;
import com.bwt.generation.RecipeGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class BetterWithTimeDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();

		BlockTagGenerator blockTagGenerator = pack.addProvider(BlockTagGenerator::new);
		pack.addProvider((output, completableFuture) -> new ItemTagGenerator(output, completableFuture, blockTagGenerator));
		pack.addProvider(EntityTypeTagGenerator::new);
		pack.addProvider(RecipeGenerator::new);
		pack.addProvider(ModelGenerator::new);
		pack.addProvider(LangGenerator::new);
		pack.addProvider(BlockLootTableGenerator::new);
		pack.addProvider(AdvancementsGenerator::new);
	}
}
