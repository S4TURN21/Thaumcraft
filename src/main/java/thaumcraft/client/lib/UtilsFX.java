package thaumcraft.client.lib;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ScreenUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UtilsFX {
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
            pPoseStack.popPose();
        }
        return rc;
    }
}
