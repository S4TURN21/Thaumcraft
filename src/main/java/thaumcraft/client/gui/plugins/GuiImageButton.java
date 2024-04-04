package thaumcraft.client.gui.plugins;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class GuiImageButton extends Button {
    ResourceLocation loc;
    int lx;
    int ly;
    int ww;
    int hh;
    public int color;

    public GuiImageButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnTooltip pOnTooltip, ResourceLocation loc, int lx, int ly, int ww, int hh, int color, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pOnTooltip);
        this.color = color;
        this.loc = loc;
        this.lx = lx;
        this.ly = ly;
        this.ww = ww;
        this.hh = hh;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (visible) {
            Font font = Minecraft.getInstance().font;
            isHovered = (pMouseX >= x - width / 2 && pMouseY >= y - height / 2 && pMouseX < x - width / 2 + width && pMouseY < y - height / 2 + height);
            int k = getYImage(isHovered);
            Color c = new Color(color);
            float cc = 0.9f;
            float ac = 1.0f;
            if (k == 2) {
                ac = 1.0f;
                cc = 1.0f;
            }
            if (!active) {
                cc = 0.5f;
                ac = 0.9f;
            }
            RenderSystem.setShaderColor(cc * (c.getRed() / 255.0f), cc * (c.getGreen() / 255.0f), cc * (c.getBlue() / 255.0f), ac);
            RenderSystem.setShaderTexture(0, loc);
            blit(pPoseStack, x - ww / 2, y - hh / 2, lx, ly, ww, hh);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            if (!getMessage().getString().isEmpty()) {
                int j = 0xFFFFFF;
                if (!active) {
                    j = 0xA0A0A0;
                } else if (isHovered) {
                    j = 0xFFFFA0;
                }
                pPoseStack.pushPose();
                pPoseStack.translate(x, y, 0.0);
                pPoseStack.scale(0.5f, 0.5f, 0.0f);
                drawCenteredString(pPoseStack, font, getMessage(), 0, -4, j);
                pPoseStack.popPose();
            }
        }
    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        return active && visible && pMouseX >= x - width / 2f && pMouseY >= y - height / 2f && pMouseX < x - width / 2f + width && pMouseY < y - height / 2f + height;
    }
}
