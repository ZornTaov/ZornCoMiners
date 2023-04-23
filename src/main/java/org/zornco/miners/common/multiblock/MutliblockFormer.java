package org.zornco.miners.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

// This is used to form A MB
public abstract class MutliblockFormer {
    abstract void Form(Level level, BlockPos origin, BlockPattern pattern); // do we pass pattern?
}
