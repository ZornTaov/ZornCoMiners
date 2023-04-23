package org.zornco.miners.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MultiBlockUtils {

    public static List<BlockInWorld> getBlocksFromPattern(BlockPattern.BlockPatternMatch pattern) {
        List<BlockInWorld> result = new ArrayList<>();

        for(int i = 0; i < pattern.getWidth(); ++i) {
            for(int j = 0; j < pattern.getHeight(); ++j) {
                for(int k = 0; k < pattern.getDepth(); ++k) {
                    result.add(pattern.getBlock(i, j, k));
                }
            }
        }

        return result;
    }
}
