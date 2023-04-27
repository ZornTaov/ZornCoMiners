package org.zornco.miners.client.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
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
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import org.jetbrains.annotations.NotNull;
import org.zornco.miners.client.ClientRegistration;
import org.zornco.miners.common.multiblock.pattern.MultiBlockInWorldType;
import org.zornco.miners.common.tile.DummyTile;

public class DummyBlockRenderer<T extends DummyTile> implements BlockEntityRenderer<T> {
    private final BlockEntityRendererProvider.Context context;
    public DummyBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(DummyTile be, float pPartialTick, PoseStack matrix, @NotNull MultiBufferSource buffer, int pPackedLight, int pPackedOverlay) {
        Level level = be.getLevel();
        if(level == null) return;
        if (!be.isMaster()) return;

        matrix.pushPose();

        var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        var map = ImmutableMap.<String, Matrix4f>builder();
        var base = new Matrix4f();
        //base.rotation((float)Math.sin(time * 0.4) * 0.1f, 0, 0, 1);
        //base.translate(new Vector3f(-.5f, -2f, -.5f));
        matrix.translate(.5f, -3f, .5f);
        matrix.mulPose(Quaternion.fromXYZ(0, (float)Math.PI/4F, 0));
        matrix.scale(.8f, 1f, .8f);
        //map.put("base", base);
        var transforms = CompositeRenderable.Transforms.of(map.build());
        ClientRegistration.renderable.render(matrix, bufferSource, RenderType::entitySolid, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, pPartialTick, transforms);
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

//            renderer.renderModel(
//                matrix.last(),
//                buffer.getBuffer(bufferType),
//                state,
//                model,
//                red, green, blue,
//                light,
//                OverlayTexture.NO_OVERLAY,
//                data,
//                type
//            );
        }
        matrix.popPose();
    }
}
