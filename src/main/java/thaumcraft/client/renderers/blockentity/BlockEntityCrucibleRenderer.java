package thaumcraft.client.renderers.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.ModelData;
import thaumcraft.common.blockentities.crafting.BlockEntityCrucible;

public class BlockEntityCrucibleRenderer implements BlockEntityRenderer<BlockEntityCrucible> {
    public BlockEntityCrucibleRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BlockEntityCrucible crucible, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if (crucible.tank.getFluidAmount() > 0) {
            TextureAtlasSprite icon = Minecraft.getInstance().getBlockRenderer().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon(ModelData.EMPTY);

            float n = (float) crucible.aspects.visSize();

            float recolor = n / 500.0f;

            if (recolor > 0.0f) {
                recolor = 0.5f + recolor / 2.0f;
            }
            if (recolor > 1.0f) {
                recolor = 1.0f;
            }

            int start = 0x3F76E4;
            var end = 0x4000BF;

            float startR = (float) (start >> 16 & 255) / 255.0F;
            float startG = (float) (start >> 8 & 255) / 255.0F;
            float startB = (float) (start & 255) / 255.0F;

            float endR = (float) (end >> 16 & 255) / 255.0F;
            float endG = (float) (end >> 8 & 255) / 255.0F;
            float endB = (float) (end & 255) / 255.0F;

            float targetR = startR + (endR - startR) * recolor;
            float targetG = startG + (endG - startG) * recolor;
            float targetB = startB + (endB - startB) * recolor;

            float uMin = icon.getU0();
            float vMin = icon.getV0();
            float uMax = icon.getU1();
            float vMax = icon.getV1();

            var buffer = pBufferSource.getBuffer(RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS));

            pPoseStack.pushPose();

            pPoseStack.translate(0.0f, crucible.getFluidHeight(), 1.0f);
            pPoseStack.mulPose(new Quaternion(new Vector3f(-1.0f, 0.0f, 0.0f), 90.0f, true));

            var pose = pPoseStack.last();
            var matrix = pose.pose();
            var normal = pose.normal();

            buffer.vertex(matrix, 0.0f, 0.0f, 0.0f).color(targetR, targetG, targetB, 1.0f).uv(uMax, vMax).overlayCoords(pPackedOverlay).uv2(pPackedLight).normal(normal, 0.0f, 0.0f, 1.0f).endVertex();
            buffer.vertex(matrix, 1.0f, 0.0f, 0.0f).color(targetR, targetG, targetB, 1.0f).uv(uMin, vMax).overlayCoords(pPackedOverlay).uv2(pPackedLight).normal(normal, 0.0f, 0.0f, 1.0f).endVertex();
            buffer.vertex(matrix, 1.0f, 1.0f, 0.0f).color(targetR, targetG, targetB, 1.0f).uv(uMin, vMin).overlayCoords(pPackedOverlay).uv2(pPackedLight).normal(normal, 0.0f, 0.0f, 1.0f).endVertex();
            buffer.vertex(matrix, 0.0f, 1.0f, 0.0f).color(targetR, targetG, targetB, 1.0f).uv(uMax, vMin).overlayCoords(pPackedOverlay).uv2(pPackedLight).normal(normal, 0.0f, 0.0f, 1.0f).endVertex();

            pPoseStack.popPose();
        }
    }
}
