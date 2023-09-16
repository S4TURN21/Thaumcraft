package thaumcraft.client.lib;

import com.mojang.blaze3d.vertex.*;

public class UtilsFX {
    public static void drawTexturedQuadF(PoseStack poseStack, float par1, float par2, float par3, float par4, float par5, float par6, double zLevel) {
        var pMatrix = poseStack.last().pose();
        final float d = 0.0625f;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(pMatrix, par1 + 0.0f, par2 + 16.0f, (float) zLevel).uv((par3 + 0.0f) * d, (par4 + par6) * d).endVertex();
        buffer.vertex(pMatrix, par1 + 16.0f, par2 + 16.0f, (float) zLevel).uv((par3 + par5) * d, (par4 + par6) * d).endVertex();
        buffer.vertex(pMatrix, par1 + 16.0f, par2 + 0.0f, (float) zLevel).uv((par3 + par5) * d, (par4 + 0.0f) * d).endVertex();
        buffer.vertex(pMatrix, par1 + 0.0f, par2 + 0.0f, (float) zLevel).uv((par3 + 0.0f) * d, (par4 + 0.0f) * d).endVertex();
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
}
