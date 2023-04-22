package org.zornco.miners.client;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zornco.miners.ZornCoMiners;
import org.zornco.miners.client.render.BasicBlockStateRenderer;
import org.zornco.miners.common.core.Registration;

@Mod.EventBusSubscriber(modid = ZornCoMiners.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistration {
    @SubscribeEvent
    public static void regRenderer(final EntityRenderersEvent.RegisterRenderers evt) {
        evt.registerBlockEntityRenderer((BlockEntityType) Registration.RENDERABLE_TILE.get(), BasicBlockStateRenderer::new);
        //evt.registerBlockEntityRenderer(Registration.MINER_TILE.get(), EnergyControllerTileRenderer::new);
    }
}
