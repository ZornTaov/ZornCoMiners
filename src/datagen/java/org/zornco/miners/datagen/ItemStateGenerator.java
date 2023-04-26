package org.zornco.miners.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.zornco.miners.common.core.Registration;
import org.zornco.miners.ZornCoMiners;

import java.util.Objects;

public class ItemStateGenerator extends ItemModelProvider {

    public ItemStateGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ZornCoMiners.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        singleTexture(Objects.requireNonNull(Registration.TEST_PAD_ITEM.getId()).getPath(),
                new ResourceLocation("item/handheld"),
                "layer0", new ResourceLocation(ZornCoMiners.MOD_ID, "item/test_pad"));
    }
}
