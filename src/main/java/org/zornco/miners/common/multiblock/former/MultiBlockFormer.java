package org.zornco.miners.common.multiblock.former;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.zornco.miners.common.multiblock.pattern.MultiBlockPattern;


// This is used to form A MB
public abstract class MultiBlockFormer {
    public abstract void form(Level level, BlockPos origin, MultiBlockPattern.MultiBlockPatternMatch pattern);
}
