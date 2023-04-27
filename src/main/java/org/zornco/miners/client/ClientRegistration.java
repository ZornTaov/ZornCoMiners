package org.zornco.miners.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.IRenderable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.zornco.miners.ZornCoMiners;
import org.zornco.miners.client.render.DummyBlockRenderer;
import org.zornco.miners.client.screen.MinerScreen;
import org.zornco.miners.common.core.Registration;

import java.util.Map;

@Mod.EventBusSubscriber(modid = ZornCoMiners.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistration {
    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(Registration.MINER_CONTAINER.get(), MinerScreen::new);
        });
    }
    @SubscribeEvent
    public static void regRenderer(final EntityRenderersEvent.RegisterRenderers evt) {
        evt.registerBlockEntityRenderer(Registration.MINER_TILE.get(), DummyBlockRenderer::new);
        //evt.registerBlockEntityRenderer(Registration.MINER_TILE.get(), EnergyControllerTileRenderer::new);
    }

    public static IRenderable<CompositeRenderable.Transforms> renderable;
    public static IRenderable<ModelData> bakedRenderable;
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new SimplePreparableReloadListener<ObjModel>()
        {
            @Override
            protected @NotNull ObjModel prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller)
            {
                var settings = new ObjModel.ModelSettings(
                    new ResourceLocation("zorncominers:models/block/mega_drill.obj"),
                    false,
                    true,
                    true,
                    false,
                    null
                );
                return ObjLoader.INSTANCE.loadModel(settings);
            }

            @Override
            protected void apply(@NotNull ObjModel model, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller)
            {
                var config = StandaloneGeometryBakingContext.create(Map.of(
                    "#qr", new ResourceLocation("zorncominers:block/mega_drill")
                ));
                renderable = model.bakeRenderable(config);
            }
        });
    }
}
