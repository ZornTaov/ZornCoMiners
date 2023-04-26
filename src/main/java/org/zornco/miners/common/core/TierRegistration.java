package org.zornco.miners.common.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;

// TODO: Update this to utilize Upgrade Cards/Items
public class TierRegistration {
    /**
     * Do we want to allow the Tier Card to
     * mine ores that are at a tier below it
     *
     * when set to true tier x ores can only
     * be mined by tier x cards and not able
     * to mine lower tier ores.
     *
     * default: false
     */

    public static boolean strictTiering = false; // Todo: make this a Config option! For Server side
    public final static HashMap<Item, Integer> TIERS = new HashMap<>();


    static {
        TIERS.put(Items.IRON_BARS, 1);
        TIERS.put(Items.IRON_BLOCK, 2);
    }

    public static int getTierForCard(Item item) {
        if (TIERS.containsKey(item)) {
            return TIERS.get(item);
        }

        return -1;
    }

    public static boolean isValidForTier(Item item, int tier) {
        if (strictTiering)
            return getTierForCard(item) == tier;

        return getTierForCard(item) >= tier;
    }

    public static boolean isValidForTier(ItemStack stack, int tier) {
        return isValidForTier(stack.getItem(), tier);
    }
}
