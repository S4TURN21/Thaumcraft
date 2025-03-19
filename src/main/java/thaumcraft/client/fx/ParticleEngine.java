package thaumcraft.client.fx;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class ParticleEngine {
    public static ParticleRenderType PARTICLE_SHEET_OPAQUE = new ParticleRenderType() {
        public void begin(BufferBuilder pBuilder, TextureManager pTextureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, ParticleEngine.particleTexture);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        public void end(Tesselator pTesselator) {
            pTesselator.end();
        }

        public String toString() {
            return "PARTICLE_SHEET_OPAQUE";
        }
    };
    public static ParticleRenderType PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType() {
        public void begin(BufferBuilder pBuilder, TextureManager pTextureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, ParticleEngine.particleTexture);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

            pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        public void end(Tesselator pTesselator) {
            pTesselator.end();
        }

        public String toString() {
            return "PARTICLE_SHEET_TRANSLUCENT";
        }
    };
    private static final List<ParticleRenderType> RENDER_ORDER = ImmutableList.of(ParticleEngine.PARTICLE_SHEET_OPAQUE, ParticleEngine.PARTICLE_SHEET_TRANSLUCENT);
    public static ResourceLocation particleTexture = new ResourceLocation("thaumcraft", "textures/misc/particles.png");
    private static Map<ParticleRenderType, ArrayList<Particle>> particles = Maps.newTreeMap(ForgeHooksClient.makeParticleRenderTypeComparator(RENDER_ORDER));
    private static ArrayList<ParticleDelay> particlesDelayed = new ArrayList<ParticleDelay>();

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            var camera = event.getCamera();
            var partialTicks = event.getPartialTick();
            var matrixStack = event.getPoseStack();
            var textureManager = Minecraft.getInstance().getTextureManager();
            var clippingHelper = event.getFrustum();
            RenderSystem.enableDepthTest();
            RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE2);
            RenderSystem.enableTexture();
            RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE0);
            PoseStack posestack = RenderSystem.getModelViewStack();
            posestack.pushPose();

            for (ParticleRenderType particlerendertype : ParticleEngine.particles.keySet()) {
                if (particlerendertype == ParticleRenderType.NO_RENDER) continue;
                Iterable<Particle> iterable = ParticleEngine.particles.get(particlerendertype);
                if (iterable != null) {
                    RenderSystem.setShader(GameRenderer::getParticleShader);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    Tesselator tesselator = Tesselator.getInstance();
                    BufferBuilder bufferbuilder = tesselator.getBuilder();
                    particlerendertype.begin(bufferbuilder, textureManager);

                    for (Particle particle : iterable) {
                        if (clippingHelper != null && particle.shouldCull() && !clippingHelper.isVisible(particle.getBoundingBox()))
                            continue;
                        try {
                            particle.render(bufferbuilder, camera, partialTicks);
                        } catch (Throwable throwable) {
                            CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering Particle");
                            CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being rendered");
                            crashreportcategory.setDetail("Particle", particle::toString);
                            crashreportcategory.setDetail("Particle Type", particlerendertype::toString);
                            throw new ReportedException(crashreport);
                        }
                    }

                    particlerendertype.end(tesselator);
                }
            }

            posestack.popPose();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
        }
    }

    public static void addEffect(Level world, Particle fx) {
        addEffect(world.dimension(), fx);
    }

    private static int getParticleLimit() {
        return (Minecraft.getInstance().options.particles().get().getId() == 2) ? 500 : ((Minecraft.getInstance().options.particles().get().getId() == 1) ? 1000 : 2000);
    }

    public static void addEffect(ResourceKey<Level> dim, Particle fx) {
        if (Minecraft.getInstance().level == null) {
            return;
        }
        int ps = Minecraft.getInstance().options.particles().get().getId();
        if (Minecraft.fps < 30) {
            ++ps;
        }
        if (Minecraft.getInstance().level.random.nextInt(3) < ps) {
            return;
        }

        if (!ParticleEngine.particles.containsKey(fx.getRenderType())) {
            ParticleEngine.particles.put(fx.getRenderType(), new ArrayList<Particle>());
        }
        ArrayList<Particle> parts = ParticleEngine.particles.get(fx.getRenderType());
        if (parts.size() >= getParticleLimit()) {
            parts.remove(0);
        }
        parts.add(fx);
        ParticleEngine.particles.put(fx.getRenderType(), parts);
    }

    public static void addEffectWithDelay(Level world, Particle fx, int delay) {
        ParticleEngine.particlesDelayed.add(new ParticleDelay(fx, world.dimension(), delay));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void updateParticles(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            try {
                Iterator<ParticleDelay> i = ParticleEngine.particlesDelayed.iterator();
                while (i.hasNext()) {
                    ParticleDelay pd = i.next();
                    --pd.delay;
                    if (pd.delay <= 0) {
                        ParticleEngine.addEffect(pd.dim, pd.particle);
                        i.remove();
                    }
                }
            } catch (Exception ex) {
            }

            for (ParticleRenderType layer : ParticleEngine.particles.keySet()) {
                ArrayList<Particle> parts = ParticleEngine.particles.get(layer);
                if (!particles.isEmpty()) {
                    for (int j = 0; j < parts.size(); ++j) {
                        Particle particle = parts.get(j);

                        particle.tick();

                        if (!particle.isAlive()) {
                            parts.remove(j--);
                            ParticleEngine.particles.put(layer, parts);
                        }
                    }
                }
            }
        }
    }

    private static class ParticleDelay {
        Particle particle;
        ResourceKey<Level> dim;
        int level;
        int delay;

        public ParticleDelay(Particle particle, ResourceKey<Level> dim, int delay) {
            this.dim = dim;
            this.particle = particle;
            this.delay = delay;
        }
    }
}
