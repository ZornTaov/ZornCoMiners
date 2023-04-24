package org.zornco.miners.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.function.Predicate;

public class MultiBlockInWorld extends BlockInWorld {
    MultiBlockInWorldType type;
    BlockPos offset;
    public MultiBlockInWorld(LevelReader pLevel, BlockPos pPos, boolean pLoadChunks) {
        super(pLevel, pPos, pLoadChunks);
    }


    public static Predicate<MultiBlockInWorld> hasState(Predicate<BlockState> pState, MultiBlockInWorldType type) {

        return (multiBlockInWorld) -> {
            multiBlockInWorld.type = type;
            return pState.test(multiBlockInWorld.getState());
        };
    }

    public MultiBlockInWorld setType(MultiBlockInWorldType type) {
        this.type = type;
        return this;
    }
    public MultiBlockInWorld setOffset(BlockPos off)
    {
        this.offset = off;
        return this;
    }
    public boolean equals(Object pOther) {
        if (this == pOther) {
            return true;
        } else if (!(pOther instanceof MultiBlockInWorld otherBIW)) {
            return false;
        } else {
            if (this.getPos().getX() != otherBIW.getPos().getX()) {
                return false;
            } else if (this.getPos().getY() != otherBIW.getPos().getY()) {
                return false;
            } else {
                return this.getPos().getZ() == otherBIW.getPos().getZ();
            }
        }
    }

    public int hashCode() {
        return (this.getPos().getY() + this.getPos().getZ() * 31) * 31 + this.getPos().getX();
    }
}
