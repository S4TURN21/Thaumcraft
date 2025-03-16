package thaumcraft.client.lib;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ScreenUtils;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.common.config.ModConfig;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UtilsFX {
    static DecimalFormat myFormatter = new DecimalFormat("#######.##");
    public static boolean hideStackOverlay = false;

    public static void drawTexturedQuad(PoseStack poseStack, float x, float y, float uOffset, float vOffset, float uWidth, float vHeight, double zLevel) {
        float var7 = 0.00390625f;
        float var8 = 0.00390625f;
        Matrix4f pMatrix = poseStack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(pMatrix, x + 0.0f, y + vHeight, (float) zLevel).uv((uOffset + 0.0f) * var7, (vOffset + vHeight) * var8).endVertex();
        bufferbuilder.vertex(pMatrix, x + uWidth, y + vHeight, (float) zLevel).uv((uOffset + uWidth) * var7, (vOffset + vHeight) * var8).endVertex();
        bufferbuilder.vertex(pMatrix, x + uWidth, y + 0.0f, (float) zLevel).uv((uOffset + uWidth) * var7, (vOffset + 0.0f) * var8).endVertex();
        bufferbuilder.vertex(pMatrix, x + 0.0f, y + 0.0f, (float) zLevel).uv((uOffset + 0.0f) * var7, (vOffset + 0.0f) * var8).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    public static void drawTexturedQuadF(PoseStack poseStack, float x, float y, float uOffset, float vOffset, float uWidth, float vHeight, double zLevel) {
        var pMatrix = poseStack.last().pose();
        final float d = 0.0625f;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(pMatrix, x + 0.0f, y + 16.0f, (float) zLevel).uv((uOffset + 0.0f) * d, (vOffset + vHeight) * d).endVertex();
        buffer.vertex(pMatrix, x + 16.0f, y + 16.0f, (float) zLevel).uv((uOffset + uWidth) * d, (vOffset + vHeight) * d).endVertex();
        buffer.vertex(pMatrix, x + 16.0f, y + 0.0f, (float) zLevel).uv((uOffset + uWidth) * d, (vOffset + 0.0f) * d).endVertex();
        buffer.vertex(pMatrix, x + 0.0f, y + 0.0f, (float) zLevel).uv((uOffset + 0.0f) * d, (vOffset + 0.0f) * d).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    public static void drawTexturedQuadFull(PoseStack pose, float x, float y, double zLevel) {
        var pMatrix = pose.last().pose();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(pMatrix, x + 0.0f, y + 16.0f, (float) zLevel).uv(0.0f, 1.0f).endVertex();
        buffer.vertex(pMatrix, x + 16.0f, y + 16.0f, (float) zLevel).uv(1.0f, 1.0f).endVertex();
        buffer.vertex(pMatrix, x + 16.0f, y + 0.0f, (float) zLevel).uv(1.0f, 0.0f).endVertex();
        buffer.vertex(pMatrix, x + 0.0f, y + 0.0f, (float) zLevel).uv(0.0f, 0.0f).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    public static void drawCustomTooltip(PoseStack pPoseStack, Screen gui, Font fr, List<String> textList, int x, int y, int subTipColor) {
        drawCustomTooltip(pPoseStack, gui, fr, textList, x, y, subTipColor, false);
    }

    public static void drawCustomTooltip(PoseStack pPoseStack, Screen gui, Font fr, List<String> textList, int x, int y, int subTipColor, boolean ignoremouse) {
        if (!textList.isEmpty()) {
            Minecraft mc = Minecraft.getInstance();
            Window scaledresolution = mc.getWindow();
            int sf = (int) scaledresolution.getGuiScale();
            int max = 240;
            int mx = x;
            boolean flip = false;
            if (!ignoremouse && (max + 24) * sf + mx > mc.getWindow().getWidth()) {
                max = (mc.getWindow().getWidth() - mx) / sf - 24;
                if (max < 120) {
                    max = 240;
                    flip = true;
                }
            }
            int widestLineWidth = 0;
            Iterator<String> textLineEntry = textList.iterator();
            boolean b = false;
            while (textLineEntry.hasNext()) {
                String textLine = textLineEntry.next();
                if (fr.width(textLine) > max) {
                    b = true;
                    break;
                }
            }
            if (b) {
                List tl = new ArrayList();
                for (Object o : textList) {
                    String textLine2 = (String) o;
                    List tl2 = fr.split(FormattedText.of(textLine2), textLine2.startsWith("@@") ? (max * 2) : max);
                    for (Object o2 : tl2) {
                        String textLine3 = ((String) o2).trim();
                        if (textLine2.startsWith("@@")) {
                            textLine3 = "@@" + textLine3;
                        }
                        tl.add(textLine3);
                    }
                }
                textList = tl;
            }
            Iterator<String> textLines = textList.iterator();
            int totalHeight = -2;
            while (textLines.hasNext()) {
                String textLine4 = textLines.next();
                int lineWidth = fr.width(textLine4);
                if (textLine4.startsWith("@@") /*&& !fr.getUnicodeFlag()*/) {
                    lineWidth /= 2;
                }
                if (lineWidth > widestLineWidth) {
                    widestLineWidth = lineWidth;
                }
                totalHeight += ((textLine4.startsWith("@@") /*&& !fr.getUnicodeFlag()*/) ? 7 : 10);
            }
            int sX = x + 12;
            int sY = y - 12;
            if (textList.size() > 1) {
                totalHeight += 2;
            }
            if (sY + totalHeight > scaledresolution.getGuiScaledHeight()) {
                sY = scaledresolution.getGuiScaledHeight() - totalHeight - 5;
            }
            if (flip) {
                sX -= widestLineWidth + 24;
            }
            pPoseStack.pushPose();
            Minecraft.getInstance().getItemRenderer().blitOffset = 300.0f;
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            Matrix4f matrix4f = pPoseStack.last().pose();
            int backgroundColor = ScreenUtils.DEFAULT_BACKGROUND_COLOR;
            drawGradientRect(matrix4f, bufferbuilder, sX - 3, sY - 4, sX + widestLineWidth + 3, sY - 3, backgroundColor, backgroundColor);
            drawGradientRect(matrix4f, bufferbuilder, sX - 3, sY + totalHeight + 3, sX + widestLineWidth + 3, sY + totalHeight + 4, backgroundColor, backgroundColor);
            drawGradientRect(matrix4f, bufferbuilder, sX - 3, sY - 3, sX + widestLineWidth + 3, sY + totalHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(matrix4f, bufferbuilder, sX - 4, sY - 3, sX - 3, sY + totalHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(matrix4f, bufferbuilder, sX + widestLineWidth + 3, sY - 3, sX + widestLineWidth + 4, sY + totalHeight + 3, backgroundColor, backgroundColor);
            int borderColorStart = ScreenUtils.DEFAULT_BORDER_COLOR_START;
            int borderColorEnd = ScreenUtils.DEFAULT_BORDER_COLOR_END;
            drawGradientRect(matrix4f, bufferbuilder, sX - 3, sY - 3 + 1, sX - 3 + 1, sY + totalHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(matrix4f, bufferbuilder, sX + widestLineWidth + 2, sY - 3 + 1, sX + widestLineWidth + 3, sY + totalHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(matrix4f, bufferbuilder, sX - 3, sY - 3, sX + widestLineWidth + 3, sY - 3 + 1, borderColorStart, borderColorStart);
            drawGradientRect(matrix4f, bufferbuilder, sX - 3, sY + totalHeight + 2, sX + widestLineWidth + 3, sY + totalHeight + 3, borderColorEnd, borderColorEnd);
            RenderSystem.enableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            BufferUploader.drawWithShader(bufferbuilder.end());
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
            for (int i = 0; i < textList.size(); ++i) {
                pPoseStack.pushPose();
                pPoseStack.translate((float) sX, (float) sY, 0.0f);
                String tl3 = textList.get(i);
                boolean shift = false;
                pPoseStack.pushPose();
                if (tl3.startsWith("@@") /*&& !fr.getUnicodeFlag()*/) {
                    sY += 7;
                    pPoseStack.scale(0.5f, 0.5f, 1.0f);
                    shift = true;
                } else {
                    sY += 10;
                }
                tl3 = tl3.replaceAll("@@", "");
                if (subTipColor != 0XFFFFFF9D) {
                    if (i == 0) {
                        tl3 = "§" + Integer.toHexString(subTipColor) + tl3;
                    } else {
                        tl3 = "§7" + tl3;
                    }
                }
                pPoseStack.translate(0.0, 0.0, 301.0);
                fr.drawShadow(pPoseStack, tl3, 0.0f, shift ? 3.0f : 0.0f, -1);
                pPoseStack.popPose();
                if (i == 0) {
                    sY += 2;
                }
                pPoseStack.popPose();
            }
            pPoseStack.popPose();
            Minecraft.getInstance().getItemRenderer().blitOffset = 0.0f;
        }
    }

    public static void drawTag(PoseStack pPoseStack, int x, int y, Aspect aspect, float amt, int bonus, double z) {
        drawTag(pPoseStack, x, y, aspect, amt, bonus, z, GlConst.GL_ONE_MINUS_SRC_ALPHA, 1.0f, false);
    }

    public static void drawTag(PoseStack pPoseStack, int x, int y, Aspect aspect, float amount, int bonus, double z, int blend, float alpha, boolean bw) {
        drawTag(pPoseStack, x, (double) y, aspect, amount, bonus, z, blend, alpha, bw);
    }

    public static void drawTag(PoseStack pPoseStack, double x, double y, Aspect aspect, float amount, int bonus, double z, int blend, float alpha, boolean bw) {
        if (aspect == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Color color = new Color(aspect.getColor());
        pPoseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, blend);

        pPoseStack.pushPose();
        RenderSystem.setShaderTexture(0, aspect.getImage());
        if (!bw) {
            RenderSystem.setShaderColor(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha);
        } else {
            RenderSystem.setShaderColor(0.1f, 0.1f, 0.1f, alpha * 0.8f);
        }
        GuiComponent.blit(pPoseStack, 0, 0, 0, 0, 16, 16, 16, 16);
        pPoseStack.popPose();

        if (amount > 0.0f) {
            pPoseStack.pushPose();
            float q = 0.5f;
            if (!ModConfig.CONFIG_GRAPHICS.largeTagText.get()) {
                pPoseStack.scale(0.5f, 0.5f, 0.5f);
                q = 1.0f;
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            String am = UtilsFX.myFormatter.format(amount);
            int sw = mc.font.width(am);
            for (Direction e : Direction.Plane.HORIZONTAL) {
                mc.font.draw(pPoseStack, am, (32 - sw + (int) x * 2) * q + e.getStepX(), (32 - mc.font.lineHeight + (int) y * 2) * q + e.getStepZ(), 0);
            }
            mc.font.draw(pPoseStack, am, (32 - sw + (int) x * 2) * q, (32 - mc.font.lineHeight + (int) y * 2) * q, 0XFFFFFF);
            pPoseStack.popPose();
        }
        if (bonus > 0) {
            pPoseStack.pushPose();
            RenderSystem.setShaderTexture(0, ParticleEngine.particleTexture);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            int px = 16 * (mc.player.tickCount % 16);
            drawTexturedQuad(pPoseStack, (float) ((int) x - 4), (float) ((int) y - 4), (float) px, 80.0f, 16.0f, 16.0f, z);
            if (bonus > 1) {
                float q2 = 0.5f;
                if (!ModConfig.CONFIG_GRAPHICS.largeTagText.get()) {
                    pPoseStack.scale(0.5f, 0.5f, 0.5f);
                    q2 = 1.0f;
                }
                String am2 = "" + bonus;
                int sw2 = mc.font.width(am2) / 2;
                pPoseStack.translate(0.0, 0.0, -1.0);
                mc.font.drawShadow(pPoseStack, am2, (8 - sw2 + (int) x * 2) * q2, (15 - mc.font.lineHeight + (int) y * 2) * q2, 0XFFFFFF);
            }
            pPoseStack.popPose();
        }
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        pPoseStack.popPose();
    }

    public static void drawGradientRect(Matrix4f pMatrix, BufferBuilder pBuilder, int startX, int startY, int endX, int endY, int startColor, int endColor) {
        float startAlpha = (startColor >> 24 & 0xFF) / 255.0f;
        float startRed = (startColor >> 16 & 0xFF) / 255.0f;
        float startGreen = (startColor >> 8 & 0xFF) / 255.0f;
        float startBlue = (startColor & 0xFF) / 255.0f;
        float endAlpha = (endColor >> 24 & 0xFF) / 255.0f;
        float endRed = (endColor >> 16 & 0xFF) / 255.0f;
        float endGreen = (endColor >> 8 & 0xFF) / 255.0f;
        float endBlue = (endColor & 0xFF) / 255.0f;
        pBuilder.vertex(pMatrix, endX, startY, 300.0f).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        pBuilder.vertex(pMatrix, startX, startY, 300.0f).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        pBuilder.vertex(pMatrix, startX, endY, 300.0f).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        pBuilder.vertex(pMatrix, endX, endY, 300.0f).color(endRed, endGreen, endBlue, endAlpha).endVertex();
    }

    public static boolean renderItemStack(PoseStack pPoseStack, Minecraft mc, ItemStack itm, int x, int y, String txt) {
        ItemRenderer itemRender = mc.getItemRenderer();
        boolean rc = false;
        if (itm != null && !itm.isEmpty()) {
            rc = true;
            pPoseStack.pushPose();
            pPoseStack.translate(0.0f, 0.0f, 32.0f);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            itemRender.renderAndDecorateItem(itm, x, y);
            if (!UtilsFX.hideStackOverlay) {
                itemRender.renderGuiItemDecorations(mc.font, itm, x, y, txt);
            }
            pPoseStack.popPose();
        }
        return rc;
    }

    public static void renderItemIn2D(PoseStack pPoseStack, ResourceLocation sprite, float thickness) {
        renderItemIn2D(pPoseStack, Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(sprite), thickness);
    }

    public static void renderItemIn2D(PoseStack pPoseStack, TextureAtlasSprite icon, float thickness) {
        pPoseStack.pushPose();
        float minu = icon.getU0();
        float minv = icon.getV0();
        float maxu = icon.getU1();
        float maxv = icon.getV1();
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        renderTextureIn3D(pPoseStack, maxu, maxv, minu, minv, 16, 16, thickness);
        pPoseStack.popPose();
    }

    public static void renderTextureIn3D(PoseStack pPoseStack, float maxu, float maxv, float minu, float minv, int width, int height, float thickness) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Matrix4f matrix = pPoseStack.last().pose();

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        bufferBuilder.vertex(matrix, 0, 0, 0).uv(maxu, maxv).color(0xFFFFFF).normal(0, 0, 1).endVertex();
        bufferBuilder.vertex(matrix, 1, 0, 0).uv(minu, maxv).color(0xFFFFFF).normal(0, 0, 1).endVertex();
        bufferBuilder.vertex(matrix, 1, 1, 0).uv(minu, minv).color(0xFFFFFF).normal(0, 0, 1).endVertex();
        bufferBuilder.vertex(matrix, 0, 1, 0).uv(maxu, minv).color(0xFFFFFF).normal(0, 0, 1).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        bufferBuilder.vertex(matrix, 0, 1, 0 - thickness).uv(maxu, minv).color(0xFFFFFF).normal(0, 0, -1).endVertex();
        bufferBuilder.vertex(matrix, 1, 1, 0 - thickness).uv(minu, minv).color(0xFFFFFF).normal(0, 0, -1).endVertex();
        bufferBuilder.vertex(matrix, 1, 0, 0 - thickness).uv(minu, maxv).color(0xFFFFFF).normal(0, 0, -1).endVertex();
        bufferBuilder.vertex(matrix, 0, 0, 0 - thickness).uv(maxu, maxv).color(0xFFFFFF).normal(0, 0, -1).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());

        float f5 = 0.5f * (maxu - minu) / width;
        float f6 = 0.5f * (maxv - minv) / height;

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        for (int k = 0; k < width; ++k) {
            float f7 = k / (float) width;
            float f8 = maxu + (minu - maxu) * f7 - f5;
            bufferBuilder.vertex(matrix, f7, 0.0F, 0.0f - thickness).uv(f8, maxv).color(0xFFFFFF).normal(-1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, f7, 0.0F, 0.0F).uv(f8, maxv).color(0xFFFFFF).normal(-1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, f7, 1.0F, 0.0F).uv(f8, minv).color(0xFFFFFF).normal(-1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, f7, 1.0F, 0.0f - thickness).uv(f8, minv).color(0xFFFFFF).normal(-1.0f, 0.0f, 0.0f).endVertex();
        }
        BufferUploader.drawWithShader(bufferBuilder.end());

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        for (int k = 0; k < width; ++k) {
            float f7 = k / (float) width;
            float f8 = maxu + (minu - maxu) * f7 - f5;
            float f9 = f7 + 1.0f / width;
            bufferBuilder.vertex(matrix, f9, 1.0F, 0.0f - thickness).uv(f8, minv).color(0xFFFFFF).normal(1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, f9, 1.0F, 0.0F).uv(f8, minv).color(0xFFFFFF).normal(1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, f9, 0.0F, 0.0F).uv(f8, maxv).color(0xFFFFFF).normal(1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, f9, 0.0F, 0.0f - thickness).uv(f8, maxv).color(0xFFFFFF).normal(1.0f, 0.0f, 0.0f).endVertex();
        }
        BufferUploader.drawWithShader(bufferBuilder.end());

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        for (int k = 0; k < height; ++k) {
            float f7 = k / (float) height;
            float f8 = maxv + (minv - maxv) * f7 - f6;
            float f9 = f7 + 1.0f / height;
            bufferBuilder.vertex(matrix, 0.0F, f9, 0.0F).uv(maxu, f8).color(0xFFFFFF).normal(0.0f, 1.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, 1.0F, f9, 0.0F).uv(minu, f8).color(0xFFFFFF).normal(0.0f, 1.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, 1.0F, f9, 0.0f - thickness).uv(minu, f8).color(0xFFFFFF).normal(0.0f, 1.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, 0.0F, f9, 0.0f - thickness).uv(maxu, f8).color(0xFFFFFF).normal(0.0f, 1.0f, 0.0f).endVertex();
        }
        BufferUploader.drawWithShader(bufferBuilder.end());

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        for (int k = 0; k < height; ++k) {
            float f7 = k / (float) height;
            float f8 = maxv + (minv - maxv) * f7 - f6;
            bufferBuilder.vertex(matrix, 1.0F, f7, 0.0F).uv(minu, f8).color(0xFFFFFF).normal(0.0f, -1.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, 0.0F, f7, 0.0F).uv(maxu, f8).color(0xFFFFFF).normal(0.0f, -1.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, 0.0F, f7, 0.0f - thickness).uv(maxu, f8).color(0xFFFFFF).normal(0.0f, -1.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, 1.0F, f7, 0.0f - thickness).uv(minu, f8).color(0xFFFFFF).normal(0.0f, -1.0f, 0.0f).endVertex();
        }
        BufferUploader.drawWithShader(bufferBuilder.end());
    }

    public static void renderQuadCentered(PoseStack pPoseStack, VertexConsumer pBuffer, float scale, float red, float green, float blue, int packedLight, float opacity) {
        renderQuadCentered(pPoseStack, pBuffer, 1, 1, 0, scale, red, green, blue, packedLight, opacity);
    }

    public static void renderQuadCentered(PoseStack pPoseStack, VertexConsumer pBuffer, int gridX, int gridY, int frame, float scale, float red, float green, float blue, int packedLight, float opacity) {
        var matrix = pPoseStack.last().pose();

        int xm = frame % gridX;
        int ym = frame / gridY;

        float uMin = xm / (float) gridX;
        float uMax = uMin + 1.0f / gridX;
        float vMin = ym / (float) gridY;
        float vMax = vMin + 1.0f / gridY;

        int j = packedLight >> 16 & 0xFFFF;
        int k = packedLight & 0xFFFF;

        pBuffer.vertex(matrix, -0.5f * scale, 0.5f * scale, 0.0f * scale).color(red, green, blue, opacity).uv(uMax, vMax).uv2(k, j).endVertex();
        pBuffer.vertex(matrix, 0.5f * scale, 0.5f * scale, 0.0f * scale).color(red, green, blue, opacity).uv(uMax, vMin).uv2(k, j).endVertex();
        pBuffer.vertex(matrix, 0.5f * scale, -0.5f * scale, 0.0f * scale).color(red, green, blue, opacity).uv(uMin, vMin).uv2(k, j).endVertex();
        pBuffer.vertex(matrix, -0.5f * scale, -0.5f * scale, 0.0f * scale).color(red, green, blue, opacity).uv(uMin, vMax).uv2(k, j).endVertex();
    }
}
