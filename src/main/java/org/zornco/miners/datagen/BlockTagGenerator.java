package org.zornco.miners.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.zornco.miners.Registration;
import org.zornco.miners.ZornCoMiners;

import java.util.Set;

public class BlockTagGenerator extends BlockTagsProvider {

    public BlockTagGenerator(DataGenerator generator, ExistingFileHelper files) {
        super(generator, ZornCoMiners.MOD_ID, files);
    }

    @Override
    protected void addTags() {

        var blocks = Set.of(
            Registration.MINER_BLOCK.get(),
            Registration.DRILL_BLOCK.get()
        );
        var pickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        var ironTool = tag(BlockTags.NEEDS_IRON_TOOL);

        blocks.forEach(block -> {
            pickaxe.add(block);
            ironTool.add(block);
        });

//        var controller = tag(NodeBlockTags.CONTROLLER_TAG);
//
//        controller.add(Registration.ENERGY_CONTROLLER_BLOCK.get());
    }
}
