package org.zornco.miners.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.zornco.miners.common.core.Registration;
import org.zornco.miners.common.tile.DummyTile;

public class CubeFormer extends MutliblockFormer {
    @Override
    void Form(Level level, BlockPos origin, BlockPattern pattern) {
        MultiBlockUtils.getBlocksFromPattern(pattern.find(level, origin)).forEach(blockInWorld -> {
            BlockState originalState = blockInWorld.getState();

            level.setBlock(blockInWorld.getPos(), Registration.DUMMY_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);

            if (level.getBlockEntity(blockInWorld.getPos()) instanceof DummyTile tile)
                tile.setOriginalBlockState(originalState);
        });
    }
}
