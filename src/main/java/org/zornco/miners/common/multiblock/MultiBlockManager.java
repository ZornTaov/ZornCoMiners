package org.zornco.miners.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import org.zornco.miners.common.core.Registration;
import org.zornco.miners.common.multiblock.former.MinerFormer;
import org.zornco.miners.common.multiblock.former.MultiBlockFormer;
import org.zornco.miners.common.multiblock.pattern.MultiBlockInWorld;
import org.zornco.miners.common.multiblock.pattern.MultiBlockInWorldType;
import org.zornco.miners.common.multiblock.pattern.MultiBlockPattern;
import org.zornco.miners.common.multiblock.pattern.MultiBlockPatternBuilder;


import java.util.HashMap;
import java.util.Map;

public class MultiBlockManager {
    private static final HashMap<MultiBlockPattern, MultiBlockFormer> STRUCTURES = new HashMap<>();

    public static void load(MultiBlockPattern pattern, MultiBlockFormer former) {
        STRUCTURES.put(pattern, former);
    }

    public static void handle(Level level, BlockPos pos) {
        for (Map.Entry<MultiBlockPattern, MultiBlockFormer> entry : STRUCTURES.entrySet()) {
            MultiBlockPattern key = entry.getKey();
            MultiBlockFormer former = entry.getValue();
            var match = key.find(level, pos);
            if (match != null) {
                former.form(level, pos,  match);
                break;
            }
        }
    }


    public static void init() {
        load(MultiBlockPatternBuilder
                .start()
                .aisle("     ", "     ", "  m  ", "     ", "     ")
                .aisle("  #  ", "  #  ", "##d##", "  #  ", "  #  ")
                .aisle("  #  ", "     ", "#   #", "     ", "  #  ")
                .aisle("     ", "     ", "     ", "     ", "     ")
                .aisle("     ", "     ", "     ", "     ", "     ")
                .where('#', MultiBlockInWorld.hasState(MultiBlockInWorldType.SLAVE, BlockPredicate.forBlock(Blocks.IRON_BLOCK)))
                .where('m', MultiBlockInWorld.hasState(MultiBlockInWorldType.MASTER, BlockPredicate.forBlock(Blocks.NETHERITE_BLOCK)))
                .where('d', MultiBlockInWorld.hasState(MultiBlockInWorldType.SLAVE, BlockPredicate.forBlock(Registration.DRILL_BLOCK.get())))
                .build(),
                new MinerFormer()
        );
    }


}
