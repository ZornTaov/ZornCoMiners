package org.zornco.miners.common.multiblock.former;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.zornco.miners.common.block.DummyBlock;
import org.zornco.miners.common.core.Registration;
import org.zornco.miners.common.multiblock.pattern.MultiBlockInWorld;
import org.zornco.miners.common.multiblock.pattern.MultiBlockInWorldType;
import org.zornco.miners.common.multiblock.pattern.MultiBlockPattern;
import org.zornco.miners.common.tile.DummyTile;
import java.util.List;

public class MinerFormer extends MultiBlockFormer {
    @Override
    public void form(Level level, BlockPos origin, MultiBlockPattern.MultiBlockPatternMatch pattern) {
        pattern.getAllTypes().forEach(multiBlockInWorld -> formDummy(level, multiBlockInWorld));
        pattern.getAllTypes().forEach(multiBlockInWorld -> finishFormingDummy(level, multiBlockInWorld));
    }

    public void formDummy(Level level, MultiBlockInWorld multiBlockInWorld) {
        BlockState original = multiBlockInWorld.getState();
        BlockState dummy = Registration.MINER_BLOCK.get().defaultBlockState().setValue(DummyBlock.MB_SLAVE, multiBlockInWorld.getType() == MultiBlockInWorldType.SLAVE);

        level.setBlock(multiBlockInWorld.getPos(), dummy, Block.UPDATE_ALL);

        if (level.getBlockEntity(multiBlockInWorld.getPos()) instanceof DummyTile tile) {
            tile.setOriginalBlockState(original);
            tile.setMultiBlockType(multiBlockInWorld.getType());
            tile.setChanged();
        }
    }

    private void finishFormingDummy(Level level, MultiBlockInWorld multiBlockInWorld) {
        if (level.getBlockEntity(multiBlockInWorld.getPos()) instanceof DummyTile tile)
            tile.setFormed(true);
    }


}
