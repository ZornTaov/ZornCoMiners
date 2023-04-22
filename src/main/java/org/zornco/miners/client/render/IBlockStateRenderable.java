package org.zornco.miners.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public interface IBlockStateRenderable {
    BlockState getBlockStateForRender();

    default ModelData getBSModelData() {
        return ModelData.EMPTY;
    }

    default RenderType getBSRenderType() {
        return RenderType.cutout();
    }

    default void renderAdditional(float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

    }

}
