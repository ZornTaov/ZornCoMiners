package org.zornco.miners.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.zornco.miners.common.core.Registration;
import org.zornco.miners.ZornCoMiners;

public class BlockStateGenerator extends BlockStateProvider {
    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ZornCoMiners.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
//        ModelFile controllerModel = models().orientable(Objects.requireNonNull(Registration.ENERGY_CONTROLLER_BLOCK.getId()).getPath(),
//                new ResourceLocation(ZornCoMiners.MOD_ID, "block/controller_side"),
//                new ResourceLocation(ZornCoMiners.MOD_ID, "block/controller_screen"),
//                new ResourceLocation(ZornCoMiners.MOD_ID, "block/controller_side")
//        );
//        horizontalBlock(Registration.ENERGY_CONTROLLER_BLOCK.get(), controllerModel);
//        simpleBlockItem(Registration.ENERGY_CONTROLLER_BLOCK.get(), controllerModel);
        simpleBlock(Registration.MINER_BLOCK.get());
        simpleBlockItem(Registration.MINER_BLOCK.get(),cubeAll(Registration.MINER_BLOCK.get()));

        ModelFile.ExistingModelFile model = models().getExistingFile(new ResourceLocation(ZornCoMiners.MOD_ID, "drill"));
        directionalBlock(Registration.DRILL_BLOCK.get(), model);
        simpleBlockItem(Registration.DRILL_BLOCK.get(), model);
    }
}
