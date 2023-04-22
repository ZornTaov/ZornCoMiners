package org.zornco.miners.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.zornco.miners.client.render.IBlockStateRenderable;
import org.zornco.miners.common.core.Registration;

public class RenderableTile extends BlockEntity implements IBlockStateRenderable {
    public RenderableTile(BlockPos pPos, BlockState pBlockState) {
        super(Registration.RENDERABLE_TILE.get(), pPos, pBlockState);
    }
    @Override
    public BlockState getBlockStateForRender() {
        return Blocks.IRON_BLOCK.defaultBlockState();
    }


    public void markBlockForRenderUpdate() {
        if (getLevel() == null)
            return;

        BlockState state = getLevel().getBlockState(getBlockPos());
        getLevel().sendBlockUpdated(getBlockPos(), state, state, Block.UPDATE_CLIENTS);
    }

}
