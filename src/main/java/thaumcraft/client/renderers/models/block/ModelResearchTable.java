package thaumcraft.client.renderers.models.block;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ModelResearchTable {
    private final PartDefinition root;
    PartDefinition Inkwell;

    public ModelResearchTable() {
        this.root = new MeshDefinition().getRoot();
        Inkwell = root.addOrReplaceChild("inkwell", CubeListBuilder.create().texOffs(0, 16).addBox(-6.0f, -2.0f, 3.0f, 3, 2, 3).mirror(true), PartPose.ZERO);
    }

    public void renderInkwell(PoseStack pPoseStack, VertexConsumer vertexconsumer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        var part = Inkwell.bake(64, 32);
        part.render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
        RenderSystem.disableBlend();
        pPoseStack.popPose();
    }
}
