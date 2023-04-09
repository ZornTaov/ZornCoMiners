package org.zornco.miners.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.zornco.miners.Registration;
import org.zornco.miners.ZornCoMiners;

import java.util.Objects;

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
    }
}
