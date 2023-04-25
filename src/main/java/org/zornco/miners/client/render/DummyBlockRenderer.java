package org.zornco.miners.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.client.model.data.ModelData;
import org.zornco.miners.common.tile.DummyTile;

public class DummyBlockRenderer<T extends DummyTile> implements BlockEntityRenderer<T> {
    private final BlockEntityRendererProvider.Context context;
    public DummyBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(DummyTile dummyTile, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        context.getBlockRenderDispatcher().renderSingleBlock(dummyTile.getOriginalBlockState(), pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, ModelData.EMPTY, RenderType.cutout());
    }
}
