package thaumcraft.client.renderers.models.block;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.awt.*;

public class ModelResearchTable extends MeshDefinition {
    PartDefinition Inkwell;
    PartDefinition ScrollTube;
    PartDefinition ScrollRibbon;

    public ModelResearchTable() {
        Inkwell = getRoot().addOrReplaceChild("inkwell", CubeListBuilder.create().texOffs(0, 16).addBox(0.0f, 0.0f, 0.0f, 3, 2, 3).mirror(true), PartPose.offset(-6.0f, -2.0f, 3.0f));
        ScrollTube = getRoot().addOrReplaceChild("scroll_tube", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -0.5f, 0.0f, 8, 2, 2).mirror(true), PartPose.offsetAndRotation(-2.0f, -2.0f, 2.0f, 0.0f, 10.0f, 0.0f));
        ScrollRibbon = getRoot().addOrReplaceChild("scroll_ribbon", CubeListBuilder.create().texOffs(0, 4).addBox(-4.25f, -0.275f, 0.0f, 1, 2, 2).mirror(true), PartPose.offsetAndRotation(-2.0f, -2.0f, 2.0f, 0.0f, 10.0f, 0.0f));
    }

    public void renderInkwell(PoseStack pPoseStack, VertexConsumer vertexconsumer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Inkwell.bake(64, 32).render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
        RenderSystem.disableBlend();
        pPoseStack.popPose();
    }

    public void renderScroll(PoseStack pPoseStack, VertexConsumer vertexconsumer, int pPackedLight, int pPackedOverlay, int color) {
        pPoseStack.pushPose();
        ScrollTube.bake(64, 32).render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
        Color c = new Color(color);
        pPoseStack.scale(1.2f, 1.2f, 1.2f);
        ScrollRibbon.bake(64, 32).render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay, c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f);
        pPoseStack.popPose();
    }
}
