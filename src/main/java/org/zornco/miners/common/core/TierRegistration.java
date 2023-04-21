package org.zornco.miners.common.core;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;

public class TierRegistration {
    /**
     * do we want tier x frame to only work with
     * tier x ore? So t1 ore can only use t1 frame,
     * not t2 frame or above
     *
     * default: false
     */

    public static boolean strictFrameTier = false;
    public final static HashMap<Block, Integer> TIERS = new HashMap<>();


    static {
        TIERS.put(Blocks.IRON_BARS, 1);
        TIERS.put(Blocks.IRON_BLOCK, 2);
    }

    public static int getTierForBlock(Block block) {
        if (TIERS.containsKey(block)) {
            return TIERS.get(block);
        }

        return -1;
    }

    public static boolean isValidForTier(Block block, int tier) {
        if (strictFrameTier)
            return getTierForBlock(block) == tier;

        return getTierForBlock(block) >= tier;
    }
}
