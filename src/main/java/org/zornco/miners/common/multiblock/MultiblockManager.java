package org.zornco.miners.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

import java.util.HashMap;
import java.util.Map;

public class MultiblockManager {
    private static final HashMap<BlockPattern, MutliblockFormer> STRUCTURES = new HashMap<>();

    public static void load(BlockPattern pattern, MutliblockFormer former) {
        STRUCTURES.put(pattern, former);
    }

    public static void handle(Level level, BlockPos pos) {
        for (Map.Entry<BlockPattern, MutliblockFormer> entry : STRUCTURES.entrySet()) {
            BlockPattern key = entry.getKey();
            MutliblockFormer former = entry.getValue();
            if (key.find(level, pos) != null) {
                former.Form(level, pos, key);
                break;
            }
        }
    }


    public static void init() {
        load(BlockPatternBuilder.start()
                .aisle("a")
                .aisle("b")
                .aisle("a")
                .where('a', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.IRON_BLOCK)))
                .where('b', BlockInWorld.hasState((state) -> state.getValue(HopperBlock.FACING) == Direction.DOWN))
                .build(),
                new CubeFormer()
        );
    }


}
