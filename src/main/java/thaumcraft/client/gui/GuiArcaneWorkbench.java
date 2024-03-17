package thaumcraft.client.gui;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

import java.awt.*;

public class GuiArcaneWorkbench extends AbstractContainerScreen<ContainerArcaneWorkbench> {
    private Inventory ip;
    ResourceLocation tex = new ResourceLocation("thaumcraft", "textures/gui/arcaneworkbench.png");

    public GuiArcaneWorkbench(ContainerArcaneWorkbench pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        ip = pPlayerInventory;
        this.imageWidth = 190;
        this.imageHeight = 234;
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {

    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderTexture(0, tex);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        int centerX = (width - getXSize()) / 2;
        int centerY = (height - getYSize()) / 2;
        this.blit(pPoseStack, centerX, centerY, 0, 0, getXSize(), getYSize());

        IArcaneRecipe result = ThaumcraftCraftingManager.findMatchingArcaneRecipe(menu.blockEntity.inventoryCraft, ip.player);
        AspectList crystals = null;
        if (result != null) {
            crystals = result.getCrystals();
        }
        if (crystals != null) {
            RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, 1);
            for (Aspect a : crystals.getAspects()) {
                int id = ShardType.getMetaByAspect(a);
                Color col = new Color(a.getColor());
                RenderSystem.setShaderColor(col.getRed() / 255.0f, col.getGreen() / 255.0f, col.getBlue() / 255.0f, 0.33f);
                pPoseStack.pushPose();
                pPoseStack.translate(centerX + ContainerArcaneWorkbench.xx[id] + 7.5f, centerY + ContainerArcaneWorkbench.yy[id] + 8.0f, 0.0f);
                pPoseStack.mulPose(new Quaternion(new Vector3f(0.0f, 0.0f, 1.0f), id * 60 + minecraft.cameraEntity.tickCount % 360 + pPartialTick, true));
                pPoseStack.scale(0.5f, 0.5f, 0.0f);
                this.blit(pPoseStack, -32, -32, 192, 0, 64, 64);
                pPoseStack.scale(1.0f, 1.0f, 1.0f);
                pPoseStack.popPose();
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        }
    }
}
