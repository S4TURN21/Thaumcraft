package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.research.ResearchEntry;

public class ResearchToast implements Toast {
    ResearchEntry entry;
    private long firstDrawTime;
    private boolean newDisplay;
    ResourceLocation tex;

    public ResearchToast(ResearchEntry entry) {
        tex = new ResourceLocation("thaumcraft", "textures/gui/hud.png");
        this.entry = entry;
    }

    @Override
    public Visibility render(PoseStack pPoseStack, ToastComponent toastGui, long delta) {
        if (newDisplay) {
            firstDrawTime = delta;
            newDisplay = false;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, tex);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        toastGui.blit(pPoseStack, 0, 0, 0, 224, 160, 32);
        GuiResearchBrowser.drawResearchIcon(pPoseStack, entry, 6, 8, 0.0f, false);
        toastGui.getMinecraft().font.draw(pPoseStack, Component.translatable("research.complete"), 30, 7, 10631665);
        String s = entry.getLocalizedName();
        float w = (float) toastGui.getMinecraft().font.width(s);
        if (w > 124.0f) {
            w = 124.0f / w;
            pPoseStack.pushPose();
            pPoseStack.translate(30.0f, 18.0f, 0.0f);
            pPoseStack.scale(w, w, w);
            toastGui.getMinecraft().font.draw(pPoseStack, s, 0, 0, 16755465);
            pPoseStack.popPose();
        } else {
            toastGui.getMinecraft().font.draw(pPoseStack, s, 30, 18, 16755465);
        }
        return (delta - firstDrawTime < 5000L) ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }
}
