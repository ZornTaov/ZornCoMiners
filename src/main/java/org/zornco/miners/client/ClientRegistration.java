package org.zornco.miners.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.zornco.miners.ZornCoMiners;
import org.zornco.miners.client.render.DummyBlockRenderer;
import org.zornco.miners.client.screen.MinerScreen;
import org.zornco.miners.common.core.Registration;

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
}
