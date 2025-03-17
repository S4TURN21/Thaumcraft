package thaumcraft.client.lib.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.lib.utils.EntityUtils;

import java.awt.*;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class RenderEventHandler {
    public static RenderEventHandler INSTANCE = new RenderEventHandler();
    @OnlyIn(Dist.CLIENT)
    public static HudHandler hudHandler = new HudHandler();
    public static float tagscale;

    @SubscribeEvent
    public static void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {

        } else {
            Minecraft mc = Minecraft.getInstance();
            if (Minecraft.getInstance().getCameraEntity() instanceof Player) {
                Player player = (Player) Minecraft.getInstance().getCameraEntity();
                long time = System.currentTimeMillis();
                if (player != null) {
                    RenderEventHandler.hudHandler.renderHuds(new PoseStack(), mc, event.renderTickTime, player, time);
                }
            }
        }
    }

    @SubscribeEvent
    public static void blockHighlight(RenderHighlightEvent.Block event) {
        BlockHitResult target = event.getTarget();
        BlockPos pos = target.getBlockPos();
        if (event.getCamera().getEntity() instanceof Player player) {
            BlockEntity blockEntity = event.getCamera().getEntity().getLevel().getBlockEntity(pos);
            if (EntityUtils.hasGoggles(player)) {
                boolean spaceAbove = event.getCamera().getEntity().level.isEmptyBlock(pos.above());
                if (blockEntity instanceof IAspectContainer && ((IAspectContainer) blockEntity).getAspects() != null && ((IAspectContainer) blockEntity).getAspects().size() > 0) {
                    if (RenderEventHandler.tagscale < 0.3f) {
                        RenderEventHandler.tagscale += 0.031f - RenderEventHandler.tagscale / 10.0f;
                    }
                    drawTagsOnContainer(event.getPoseStack(), event.getMultiBufferSource(), event.getCamera(), pos.getX(), pos.getY() + (spaceAbove ? 0.4f : 0.0f), pos.getZ(), ((IAspectContainer) blockEntity).getAspects(), 0xDC, spaceAbove ? Direction.UP : target.getDirection(), event.getPartialTick());
                }
            }
        }
    }

    @SubscribeEvent
    public static void renderLast(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            if (RenderEventHandler.tagscale > 0.0f) {
                RenderEventHandler.tagscale -= 0.005f;
            }
        }
    }

    public static void drawTagsOnContainer(PoseStack poseStack, MultiBufferSource multiBufferSource, Camera camera, float x, float y, float z, AspectList tags, int packedLight, Direction dir, float partialTicks) {
        if (Minecraft.getInstance().getCameraEntity() instanceof Player player && tags != null && tags.size() > 0) {
            int fox = 0;
            int foy = 0;
            int foz = 0;

            if (dir != null) {
                fox = dir.getStepX();
                foy = dir.getStepY();
                foz = dir.getStepZ();
            } else {
                x -= 0.5;
                z -= 0.5;
            }

            var cameraPosition = camera.getPosition();

            double iPX = cameraPosition.x();
            double iPY = cameraPosition.y();
            double iPZ = cameraPosition.z();

            int rowsize = 5;
            int current = 0;
            float shifty = 0.0f;
            int left = tags.size();

            for (Aspect tag : tags.getAspects()) {
                int div = Math.min(left, rowsize);
                if (current >= rowsize) {
                    current = 0;
                    shifty -= RenderEventHandler.tagscale * 1.05f;
                    left -= rowsize;
                    if (left < rowsize) {
                        div = left % rowsize;
                    }
                }
                float shift = (current - div / 2.0f + 0.5f) * RenderEventHandler.tagscale * 4.0f;
                shift *= RenderEventHandler.tagscale;

                var color = new Color(tag.getColor());

                var buffer = multiBufferSource.getBuffer(RenderType.textSeeThrough(tag.getImage()));

                poseStack.pushPose();
                poseStack.translate(-iPX + x + 0.5 + RenderEventHandler.tagscale * 2.0f * fox, -iPY + y - shifty + 0.5 + RenderEventHandler.tagscale * 2.0f * foy, -iPZ + z + 0.5 + RenderEventHandler.tagscale * 2.0f * foz);
                float xd = (float) (iPX - (x + 0.5));
                float zd = (float) (iPZ - (z + 0.5));
                float rotYaw = (float) (Math.atan2(xd, zd) * 180.0 / Mth.PI);
                poseStack.mulPose(new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), rotYaw + 180.0f, true));
                poseStack.translate(shift, 0.0, 0.0);
                poseStack.mulPose(new Quaternion(new Vector3f(0.0f, 0.0f, 1.0f), 90.0f, true));
                poseStack.scale(RenderEventHandler.tagscale, RenderEventHandler.tagscale, RenderEventHandler.tagscale);
                Matrix4f matrix = poseStack.last().pose();

                UtilsFX.renderQuadCentered(poseStack, buffer, 1.0f, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, packedLight, 0.75f);

                if (tags.getAmount(tag) >= 0) {
                    poseStack.mulPose(new Quaternion(new Vector3f(0.0f, 0.0f, 1.0f), 90.0f, true));
                    String am = "" + tags.getAmount(tag);
                    poseStack.scale(0.04f, 0.04f, 0.04f);
                    poseStack.translate(0.0f, 6.0f, -0.1f);
                    int sw = Minecraft.getInstance().font.width(am);
                    Minecraft.getInstance().font.drawInBatch(am, 14.0f - sw, 1.0f, 0x111111, false, matrix, multiBufferSource, true, 0, packedLight);
                    poseStack.translate(0.0f, 0.0f, -0.1f);
                    Minecraft.getInstance().font.drawInBatch(am, 13 - sw, 0, 0xFFFFFF, false, matrix, multiBufferSource, true, 0, packedLight);
                }
                poseStack.popPose();
                ++current;
            }
        }
    }
}