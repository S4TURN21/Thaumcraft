package thaumcraft.client.fx.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thaumcraft.client.fx.ParticleEngine;


public class FXBlockRunes extends TextureSheetParticle {
    double ofx;
    double ofy;
    float rotation;
    int runeIndex;

    public FXBlockRunes(ClientLevel pLevel, double pX, double pY, double pZ, float red, float green, float blue, int m) {
        super(pLevel, pX, pY, pZ, 0, 0, 0);
        ofx = 0.0;
        ofy = 0.0;
        rotation = 0.0f;
        runeIndex = 0;
        if (red == 0.0f) {
            red = 1.0f;
        }
        rotation = (float) (pLevel.random.nextInt(4) * 90);
        rCol = red;
        gCol = green;
        bCol = blue;
        gravity = 0.0f;
        double motionX = 0.0;
        zd = motionX;
        yd = motionX;
        this.xd = motionX;
        lifetime = 3 * m;
        setSize(0.01f, 0.01f);
        xo = x;
        yo = y;
        zo = z;
        runeIndex = (int) (Math.random() * 16.0 + 224.0);
        ofx = pLevel.random.nextFloat() * 0.2;
        ofy = -0.3 + pLevel.random.nextFloat() * 0.6;
        quadSize = (float) (1.0 + pLevel.random.nextGaussian() * 0.10000000149011612);
        alpha = 0.0f;
    }

    @Override
    public void render(VertexConsumer wr, Camera pRenderInfo, float pPartialTicks) {
        Vec3 vec3 = pRenderInfo.getPosition();
        float f = (float) (Mth.lerp((double) pPartialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) pPartialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) pPartialTicks, this.zo, this.z) - vec3.z());
        Quaternion quat = Vector3f.YP.rotationDegrees(rotation);
        Quaternion quat1 = Vector3f.ZP.rotationDegrees(90.0f);

        Vector3f[] avector3f = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };
        float f4 = 0.3f * quadSize;

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.mul(0.5f * f4);
            vector3f.transform(quat);
            var offset = new Vector3f((float) ofx, (float) ofy, -0.51f);
            offset.transform(quat1);
            offset.transform(quat);
            vector3f.add(offset);
            vector3f.add(f, f1, f2);
        }

        float var16 = runeIndex % 16 / 64.0f;
        float var17 = var16 + 0.015625f;
        float var18 = 0.09375f;
        float var19 = var18 + 0.015625f;

        int j = this.getLightColor(pPartialTicks);
        wr.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).uv(var17, var19).color(this.rCol, this.gCol, this.bCol, this.alpha / 2.0f).uv2(j).endVertex();
        wr.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).uv(var17, var18).color(this.rCol, this.gCol, this.bCol, this.alpha / 2.0f).uv2(j).endVertex();
        wr.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).uv(var16, var18).color(this.rCol, this.gCol, this.bCol, this.alpha / 2.0f).uv2(j).endVertex();
        wr.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).uv(var16, var19).color(this.rCol, this.gCol, this.bCol, this.alpha / 2.0f).uv2(j).endVertex();
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        float threshold = lifetime / 5.0f;
        if (age <= threshold) {
            alpha = age / threshold;
        } else {
            alpha = (lifetime - age) / (float) lifetime;
        }
        if (age++ >= lifetime) {
            remove();
        }
        yd -= 0.04D * (double) gravity;
        x += xd;
        y += yd;
        z += zd;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return new ParticleRenderType() {
            public void begin(BufferBuilder p_107455_, TextureManager p_107456_) {
                RenderSystem.depthMask(false);
                RenderSystem.setShaderTexture(0, ParticleEngine.particleTexture);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                p_107455_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            }

            public void end(Tesselator p_107458_) {
                p_107458_.end();
            }

            public String toString() {
                return "BLOCK_RUNES";
            }
        };
    }

    @Override
    protected int getLightColor(float pPartialTick) {
        int i = 240;
        int k = i >> 16 & 255;
        int j = i & 255;
        return j | k << 16;
    }

    public void setGravity(float value) {
        gravity = value;
    }
}
