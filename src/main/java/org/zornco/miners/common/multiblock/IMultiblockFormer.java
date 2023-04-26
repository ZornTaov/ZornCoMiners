package org.zornco.miners.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.zornco.miners.common.multiblock.pattern.MultiBlockPattern;


// This is used to form a multiblock structure!
public interface IMultiblockFormer {
    void form(Level level, BlockPos origin, MultiBlockPattern.MultiBlockPatternMatch pattern);
}
