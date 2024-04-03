package thaumcraft.client.renderers.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.models.block.ModelResearchTable;
import thaumcraft.common.blockentities.crafting.BlockEntityResearchTable;
import thaumcraft.common.blocks.crafting.BlockResearchTable;

public class BlockEntityResearchTableRenderer implements BlockEntityRenderer<BlockEntityResearchTable> {
    private ModelResearchTable tableModel;
    private static ResourceLocation TEX = new ResourceLocation("thaumcraft", "textures/block/research_table_model.png");

    public BlockEntityResearchTableRenderer(BlockEntityRendererProvider.Context context) {
        tableModel = new ModelResearchTable();
    }

    @Override
    public void render(BlockEntityResearchTable table, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5f, 1.0f, 0.5f);
        pPoseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
        switch (table.getBlockState().getValue(BlockResearchTable.FACING)) {
            case EAST: {
                pPoseStack.mulPose(Vector3f.YP.rotationDegrees(90.0f));
                break;
            }
            case WEST: {
                pPoseStack.mulPose(Vector3f.YP.rotationDegrees(270.0f));
                break;
            }
            case SOUTH: {
                pPoseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
                break;
            }
        }

        VertexConsumer vertexconsumer = pBufferSource.getBuffer(RenderType.entityTranslucentCull(TEX));

        if (!table.getSyncedStackInSlot(0).isEmpty() && table.getSyncedStackInSlot(0).getItem() instanceof IScribeTools) {
            tableModel.renderInkwell(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
            pPoseStack.pushPose();
            RenderSystem.enableBlend();
            pPoseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
            pPoseStack.translate(-0.5, 0.1, 0.125);
            pPoseStack.mulPose(Vector3f.YP.rotationDegrees(60.0f));
            pPoseStack.scale(0.5f, 0.5f, 0.5f);
            RenderSystem.enableDepthTest();
            UtilsFX.renderItemIn2D(pPoseStack, new ResourceLocation("thaumcraft", "research/quill"), 0.0625f);
            RenderSystem.disableBlend();
            pPoseStack.popPose();
        }

        pPoseStack.popPose();
    }
}
