package thaumcraft.client.renderers.entity;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Random;

public class RenderSpecialItem extends ItemEntityRenderer {
    public RenderSpecialItem(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(ItemEntity e, float pEntityYaw, float pt, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        Random random = new Random(187L);
        float var11 = Mth.sin((e.getAge() + pt) / 10.0f + e.bobOffs) * 0.1f + 0.1f;
        pMatrixStack.pushPose();
        pMatrixStack.translate(0.0D, var11 + 0.25f, 0.0D);
        int q = Minecraft.useFancyGraphics() ? 10 : 5;
        VertexConsumer wr = pBuffer.getBuffer(RenderType.lightning());
        float f1 = e.getAge() / 500.0f;
        float f2 = 0.9f;
        float f3 = 0.0f;
        pMatrixStack.pushPose();
        for (int i = 0; i < q; ++i) {
            pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0F));
            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0F));
            pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0F));
            pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0F));
            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0F));
            pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0f + f1 * 360.0f));
            float fa = random.nextFloat() * 20.0f + 5.0f + f3 * 10.0f;
            float f4 = random.nextFloat() * 2.0f + 1.0f + f3 * 2.0f;
            fa /= 30.0f / (Math.min(e.getAge(), 10) / 10.0f);
            f4 /= 30.0f / (Math.min(e.getAge(), 10) / 10.0f);
            Matrix4f matrix4f = pMatrixStack.last().pose();
            wr.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f - f3).endVertex();
            wr.vertex(matrix4f, -0.866f * f4, fa, (-0.5f * f4)).color(1.0f, 0.0f, 1.0f, 0.0f).endVertex();
            wr.vertex(matrix4f, 0.866f * f4, fa, (-0.5f * f4)).color(1.0f, 0.0f, 1.0f, 0.0f).endVertex();
            wr.vertex(matrix4f, 0.0f, fa, (1.0f * f4)).color(1.0f, 0.0f, 1.0f, 0.0f).endVertex();
            wr.vertex(matrix4f, -0.866f * f4, fa, (-0.5f * f4)).color(1.0f, 0.0f, 1.0f, 0.0f).endVertex();
        }
        pMatrixStack.popPose();
        pMatrixStack.popPose();
        super.render(e, pEntityYaw, pt, pMatrixStack, pBuffer, pPackedLight);
    }
}
