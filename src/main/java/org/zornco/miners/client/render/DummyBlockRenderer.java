package org.zornco.miners.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.renderable.BakedModelRenderable;
import org.jetbrains.annotations.NotNull;
import org.zornco.miners.common.tile.DummyTile;

public class DummyBlockRenderer<T extends DummyTile> implements BlockEntityRenderer<T> {
    private final BlockEntityRendererProvider.Context context;
    public DummyBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(DummyTile be, float pPartialTick, PoseStack matrix, @NotNull MultiBufferSource buffer, int pPackedLight, int pPackedOverlay) {
//        context.getBlockRenderDispatcher().renderSingleBlock(dummyTile.getOriginalBlockState(),
//            pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, ModelData.EMPTY, RenderType.cutout());
        Level level = be.getLevel();
        if(level == null) return;
        matrix.pushPose();
        BlockState state = be.getOriginalBlockState();
        BlockModelShaper blockModelShapes = context.getBlockRenderDispatcher().getBlockModelShaper();
        BakedModel model = blockModelShapes.getBlockModel(state);
        ModelData data = model.getModelData(level, be.getBlockPos(), state, be.getModelData());

        ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        //noinspection ConstantConditions
        RandomSource rand = be.getLevel().getRandom();

        int color = Minecraft.getInstance().getBlockColors().getColor(state, be.getLevel(), be.getBlockPos(), 0);
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;

        int light = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos());

        for (RenderType type : model.getRenderTypes(state, rand, data))
        {
            RenderType bufferType = RenderTypeHelper.getEntityRenderType(type, false);

            renderer.renderModel(
                matrix.last(),
                buffer.getBuffer(bufferType),
                state,
                model,
                red, green, blue,
                light,
                OverlayTexture.NO_OVERLAY,
                data,
                type
            );
        }
        matrix.popPose();
    }
}
