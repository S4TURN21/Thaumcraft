package thaumcraft.client.lib.events;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.items.tools.ItemThaumometer;
import thaumcraft.common.world.aura.AuraChunk;

import java.text.DecimalFormat;
import java.util.concurrent.LinkedBlockingQueue;

public class HudHandler {
    public static ResourceLocation HUD = new ResourceLocation("thaumcraft", "textures/gui/hud.png");
    public LinkedBlockingQueue<KnowledgeGainTracker> knowledgeGainTrackers;
    public static ResourceLocation[] KNOW_TYPE = new ResourceLocation[]{new ResourceLocation("thaumcraft", "textures/research/knowledge_theory.png"), new ResourceLocation("thaumcraft", "textures/research/knowledge_observation.png")};
    public static AuraChunk currentAura = new AuraChunk(null, (short) 0, 0.0f, 0.0f);
    DecimalFormat secondsFormatter;

    public HudHandler() {
        this.knowledgeGainTrackers = new LinkedBlockingQueue<>();
        secondsFormatter = new DecimalFormat("#######.#");
    }

    @OnlyIn(Dist.CLIENT)
    void renderHuds(PoseStack pPoseStack, Minecraft mc, float renderTickTime, Player player, long time) {
        pPoseStack.pushPose();
        Window sr = Minecraft.getInstance().getWindow();
        int ww = sr.getGuiScaledWidth();
        int hh = sr.getGuiScaledHeight();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        if (mc.isWindowActive() && !mc.options.hideGui) {
            RenderSystem.setShaderTexture(0, HUD);
            ItemStack handStack = player.getItemInHand(InteractionHand.MAIN_HAND);
            boolean rT = false;
            int start = 0;
            for (int a = 0; a < 2; ++a) {
                if (!rT && handStack.getItem() instanceof ItemThaumometer) {
                    renderThaumometerHud(pPoseStack, mc, renderTickTime, player, time, ww, hh, start);
                    rT = true;
                    start += 80;
                }
                handStack = player.getItemInHand(InteractionHand.OFF_HAND);
            }
        }
        pPoseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    void renderThaumometerHud(PoseStack pPoseStack, Minecraft mc, float partialTicks, Player player, long time, int ww, int hh, int shifty) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        float base = Mth.clamp(HudHandler.currentAura.getBase() / 525.0f, 0.0f, 1.0f);
        float vis = Mth.clamp(HudHandler.currentAura.getVis() / 525.0f, 0.0f, 1.0f);
        float flux = Mth.clamp(HudHandler.currentAura.getFlux() / 525.0f, 0.0f, 1.0f);
        float count = Minecraft.getInstance().getCameraEntity().tickCount + partialTicks;
        float count2 = Minecraft.getInstance().getCameraEntity().tickCount / 3.0f + partialTicks;
        if (flux + vis > 1.0f) {
            float m = 1.0f / (flux + vis);
            base *= m;
            vis *= m;
            flux *= m;
        }
        float start = 10.0f + (1.0f - vis) * 64.0f;
        pPoseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        pPoseStack.translate(2.0, shifty, 0.0);
        if (vis > 0.0f) {
            pPoseStack.pushPose();
            RenderSystem.setShaderColor(0.7f, 0.4f, 0.9f, 1.0f);
            RenderSystem.enableBlend();
            pPoseStack.translate(5.0, start, 0.0);
            pPoseStack.scale(1.0F, vis, 1.0F);
            UtilsFX.drawTexturedQuad(pPoseStack, 0.0f, 0.0f, 88.0f, 56.0f, 8.0f, 64.0f, -90.0);
            pPoseStack.popPose();

            pPoseStack.pushPose();
            RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, 1);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.5f);
            pPoseStack.translate(5.0, start, 0.0);
            UtilsFX.drawTexturedQuad(pPoseStack, 0.0f, 0.0f, 96.0f, 56.0f + count % 64.0f, 8.0f, vis * 64.0f, -90.0);
            RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
            pPoseStack.popPose();

            if (player.isCrouching()) {
                pPoseStack.pushPose();
                pPoseStack.translate(16.0, start, 0.0);
                pPoseStack.scale(0.5F, 0.5F, 0.5F);
                String msg = secondsFormatter.format(HudHandler.currentAura.getVis());
                mc.font.drawShadow(pPoseStack, msg, 0, 0, 0XEEAAFF);
                pPoseStack.popPose();
                RenderSystem.setShaderTexture(0, HUD);
            }
            pPoseStack.popPose();
        }

        pPoseStack.pushPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        UtilsFX.drawTexturedQuad(pPoseStack, 3.0f, 1.0f, 72.0f, 48.0f, 16.0f, 80.0f, -90.0);
        pPoseStack.popPose();

        start = 8.0f + (1.0f - base) * 64.0f;

        pPoseStack.pushPose();
        RenderSystem.enableBlend();
        UtilsFX.drawTexturedQuad(pPoseStack, 4.0f, start, 117.0f, 61.0f, 14.0f, 5.0f, -90.0);
        pPoseStack.popPose();

        pPoseStack.popPose();
    }

    public static class KnowledgeGainTracker {
        IPlayerKnowledge.EnumKnowledgeType type;
        ResearchCategory category;
        int progress;
        int max;
        long seed;
        boolean sparks;

        public KnowledgeGainTracker(IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int progress, long seed) {
            this.sparks = false;
            this.type = type;
            this.category = category;
            if (type == IPlayerKnowledge.EnumKnowledgeType.THEORY) {
                progress += 10;
            }
            this.progress = progress;
            this.max = progress;
            this.seed = seed;
        }
    }
}
