package org.zornco.miners.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import org.zornco.miners.ZornCoMiners;

import static org.zornco.miners.common.core.Registration.*;

public class LangGenerator extends LanguageProvider {
    public LangGenerator(DataGenerator generator) {
        super(generator, ZornCoMiners.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup."+ZornCoMiners.MOD_ID, "ZornCo Miner"); // "itemGroup.zorncominer"
        add(TEST_PAD_ITEM.get() , "Test Pad"); // "item.zorncominer.test_pad"
        add(MINER_BLOCK.get(), "Miner"); // "item.zorncominer.miner"
        add(DRILL_BLOCK.get(), "Drill"); // "item.zorncominer.miner"
        addTop("connected_to", "Connected To: %s"); // "zorncominer.top.connected_to"
        addTop("transferred", "FE/t: %s"); // "zorncominer.top.transferred"
    }
    public static String toProperCase(final String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    /**
     * Add a "The One Probe" info string
     */
    private void addTop(String key, String value) {
        add(key, "top", value);
    }

    private void add(String key, String type, String value) {
        add(String.format("%s.%s.%s", ZornCoMiners.MOD_ID, type, key), value);
    }
}
