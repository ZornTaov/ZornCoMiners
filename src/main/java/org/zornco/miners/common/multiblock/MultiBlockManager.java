package org.zornco.miners.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;

import java.util.HashMap;
import java.util.Map;

public class MultiBlockManager {
    private static final HashMap<MultiBlockPattern, MutliblockFormer> STRUCTURES = new HashMap<>();

    public static void load(MultiBlockPattern pattern, MutliblockFormer former) {
        STRUCTURES.put(pattern, former);
    }

    public static void handle(Level level, BlockPos pos) {
        for (Map.Entry<MultiBlockPattern, MutliblockFormer> entry : STRUCTURES.entrySet()) {
            MultiBlockPattern key = entry.getKey();
            MutliblockFormer former = entry.getValue();
            if (key.find(level, pos) != null) {
                former.Form(level, pos, key);
                break;
            }
        }
    }


    public static void init() {
        load(MultiBlockPatternBuilder.start()
                .aisle("a")
                .aisle("b")
                .aisle("a")
                .where('a', MultiBlockInWorld.hasState(BlockPredicate.forBlock(Blocks.IRON_BLOCK), MultiBlockInWorldType.SLAVE))
                .where('b', MultiBlockInWorld.hasState((state) -> state.getValue(HopperBlock.FACING) == Direction.DOWN, MultiBlockInWorldType.MASTER))
                .build(),
                new CubeFormer()
        );
    }


}
