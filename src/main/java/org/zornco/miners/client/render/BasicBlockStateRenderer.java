package org.zornco.miners.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BasicBlockStateRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private final BlockRenderDispatcher blockRenderDispatcher;
    public BasicBlockStateRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(BlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity instanceof IBlockStateRenderable renderable) {
            blockRenderDispatcher.renderSingleBlock(renderable.getBlockStateForRender(), pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, renderable.getBSModelData(), renderable.getBSRenderType());
            renderable.renderAdditional(pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
        }
    }
}
