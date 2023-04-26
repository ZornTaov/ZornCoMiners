package org.zornco.miners.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import org.zornco.miners.common.core.Registration;
import org.zornco.miners.common.multiblock.pattern.MultiBlockInWorld;
import org.zornco.miners.common.multiblock.pattern.MultiBlockInWorldType;
import org.zornco.miners.common.multiblock.pattern.MultiBlockPattern;
import org.zornco.miners.common.multiblock.pattern.MultiBlockPatternBuilder;


import java.util.HashMap;
import java.util.Map;

public class MultiBlockManager {
    private static final HashMap<MultiBlockPattern, IMultiblockFormer> STRUCTURES = new HashMap<>();

    public static void load(MultiBlockPattern pattern, IMultiblockFormer former) {
        STRUCTURES.put(pattern, former);
    }

    public static void handle(Level level, BlockPos pos) {
        var structurePattern = STRUCTURES.keySet().stream()
                .filter(pattern -> pattern.find(level, pos) != null)
                .findFirst();

        structurePattern.ifPresent(multiBlockPattern -> {
            IMultiblockFormer former = STRUCTURES.get(multiBlockPattern);
            former.form(level, pos, multiBlockPattern.find(level, pos));
        });
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
                Registration.MINER_BLOCK.get()
        );
    }


}
