package org.zornco.miners.datagen;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zornco.miners.ZornCoMiners;

@Mod.EventBusSubscriber(modid = ZornCoMiners.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        final var generator = event.getGenerator();
        final var helper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new LangGenerator(generator));
        generator.addProvider(event.includeServer(), new RecipeGenerator(generator));
        //generator.addProvider(event.includeServer(), new LootTableGenerator(generator));
        generator.addProvider(event.includeServer(), new BlockTagGenerator(generator, helper));

        generator.addProvider(event.includeClient(), new BlockStateGenerator(generator, helper));
        generator.addProvider(event.includeClient(), new ItemStateGenerator(generator, helper));
    }
}