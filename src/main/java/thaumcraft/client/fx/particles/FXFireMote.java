package thaumcraft.client.fx.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FXFireMote extends TextureSheetParticle {
    protected int particleTextureIndexX;
    protected int particleTextureIndexY;

    private float baseScale;
    private float baseAlpha;
    private ParticleRenderType glowlayer = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;

    public FXFireMote(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, float r, float g, float b, float scale, ParticleRenderType layer) {
        super(pLevel, pX, pY, pZ, 0, 0, 0);
        this.baseScale = 0.0f;
        this.baseAlpha = 1.0f;
        float colorR = r;
        float colorG = g;
        float colorB = b;
        if (colorR > 1.0) {
            colorR /= 255.0f;
        }
        if (colorG > 1.0) {
            colorG /= 255.0f;
        }
        if (colorB > 1.0) {
            colorB /= 255.0f;
        }
        this.glowlayer = layer;
        this.setColor(colorR, colorG, colorB);
        this.lifetime = 16;
        this.quadSize = scale;
        this.baseScale = scale;
        this.setParticleSpeed(pXSpeed, pYSpeed, pZSpeed);
        this.roll = Mth.TWO_PI;
        this.setParticleTextureIndex(7);
    }

    public void setParticleTextureIndex(int particleTextureIndex) {
        this.particleTextureIndexX = particleTextureIndex % 64;
        this.particleTextureIndexY = particleTextureIndex / 64;
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        float tx1 = this.particleTextureIndexX / 64.0f;
        float tx2 = tx1 + 1.0f / 64.0f;
        float ty1 = this.particleTextureIndexY / 64.0f;
        float ty2 = ty1 + 1.0f / 64.0f;

        float ts = 0.1f * this.getQuadSize(pPartialTicks);

        if (this.sprite != null) {
            tx1 = this.sprite.getU0();
            tx2 = this.sprite.getU1();
            ty1 = this.sprite.getV0();
            ty2 = this.sprite.getV1();
        }

        int j = this.getLightColor(pPartialTicks);

        Vec3 vec3 = pRenderInfo.getPosition();
        float x = (float) (Mth.lerp((double) pPartialTicks, this.xo, this.x) - vec3.x());
        float y = (float) (Mth.lerp((double) pPartialTicks, this.yo, this.y) - vec3.y());
        float z = (float) (Mth.lerp((double) pPartialTicks, this.zo, this.z) - vec3.z());
        Quaternion quaternion;
        if (this.roll == 0.0F) {
            quaternion = pRenderInfo.rotation();
        } else {
            quaternion = new Quaternion(pRenderInfo.rotation());
            float f3 = Mth.lerp(pPartialTicks, this.oRoll, this.roll);
            quaternion.mul(Vector3f.ZP.rotation(f3));
        }

        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(quaternion);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};

        for (int l = 0; l < 4; ++l) {
            Vector3f vector3f = avector3f[l];
            vector3f.transform(quaternion);
            vector3f.mul(ts);
            vector3f.add(x, y, z);
        }
        pBuffer.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).uv(tx2, ty2).color(rCol, gCol, bCol, this.alpha * this.baseAlpha).uv2(j).endVertex();
        pBuffer.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).uv(tx2, ty1).color(rCol, gCol, bCol, this.alpha * this.baseAlpha).uv2(j).endVertex();
        pBuffer.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).uv(tx1, ty1).color(rCol, gCol, bCol, this.alpha * this.baseAlpha).uv2(j).endVertex();
        pBuffer.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).uv(tx1, ty2).color(rCol, gCol, bCol, this.alpha * this.baseAlpha).uv2(j).endVertex();
    }

    @Override
    public void setAlpha(float pAlpha) {
        super.setAlpha(pAlpha);
    }

    @Override
    protected int getLightColor(float pPartialTick) {
        return 255;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return this.glowlayer;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level.random.nextInt(6) == 0) {
            ++this.age;
        }
        if (this.age >= this.lifetime) {
            this.remove();
        }
        float lifespan = this.age / (float) this.lifetime;
        this.quadSize = baseScale - baseScale * lifespan;
        this.baseAlpha = 1.0f - lifespan;
        this.oRoll = roll;
        ++this.roll;
    }
}
