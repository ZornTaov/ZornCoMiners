package org.zornco.miners.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.zornco.miners.ZornCoMiners;
import org.zornco.miners.common.block.MinerContainer;

public class MinerScreen extends AbstractContainerScreen<MinerContainer> {
    private final ResourceLocation GUI = new ResourceLocation(ZornCoMiners.MOD_ID, "textures/gui/miner.png");
    public MinerScreen(MinerContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }


    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(PoseStack pPoseStack, int pX, int pY) {
        super.renderTooltip(pPoseStack, pX, pY);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        if (pX > relX+8 && pX < relX+8+16) {
            if (pY > relY+8 && pY < relY+8+70) {

                int energy = menu.getEnergy();
                renderTooltip(pPoseStack, Component.literal(energy + "/10000FE"), pX, pY);
            }
        }
    }

    @Override
    protected void renderLabels(@NotNull PoseStack matrixStack, int mouseX, int mouseY) {
        drawString(matrixStack, Minecraft.getInstance().font, "Status: " + (menu.getStatus() ? "Valid" : "Invalid"), 96, 10, 0xffffff);
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        int energy = menu.getEnergy();
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
        this.blit(matrixStack, relX+8, relY+8+70-70*energy/10000, 176, 17, 16, 70*energy/10000);
    }
}
