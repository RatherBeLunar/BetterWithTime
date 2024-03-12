package com.bwt;

import com.bwt.generation.AdvancementsGenerator;
import com.bwt.generation.BlockTagGenerator;
import com.bwt.generation.EntityTypeTagGenerator;
import com.bwt.generation.ItemTagGenerator;
import com.bwt.generation.LangGenerator;
import com.bwt.generation.LootTableGenerator;
import com.bwt.generation.ModelGenerator;
import com.bwt.generation.RecipeGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
//import net.minecraft.SharedConstants;
//import net.minecraft.data.DataGenerator;
//
//import java.io.IOException;

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
		pack.addProvider(LootTableGenerator::new);
		pack.addProvider(AdvancementsGenerator::new);


//		DataGenerator vanillaGenerator = new DataGenerator(generator.getModContainer().getRootPaths().get(0), SharedConstants.getGameVersion(), true);
//		DataGenerator.Pack vanillaPack = vanillaGenerator.createVanillaPack(true);
//		vanillaPack.addProvider(VanillaRecipeGenerator::new);
//		try {
//			vanillaGenerator.run();
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
	}
}
