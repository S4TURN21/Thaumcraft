package thaumcraft.client.fx.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FXGeneric extends TextureSheetParticle {
    boolean doneFrames;
    double windX;
    double windZ;
    int layer;
    float dr;
    float dg;
    float db;
    boolean loop;
    float rotationSpeed;
    int startParticle;
    int numParticles;
    int particleInc;
    float[] scaleKeys;
    float[] scaleFrames;
    float[] alphaKeys;
    float[] alphaFrames;
    float randomX;
    float randomY;
    float randomZ;
    boolean angled;
    float angleYaw;
    float anglePitch;
    int gridSize;
    protected int particleTextureIndexX;
    protected int particleTextureIndexY;

    public FXGeneric(ClientLevel world, double x, double y, double z, double xx, double yy, double zz) {
        super(world, x, y, z, xx, yy, zz);
        this.doneFrames = false;
        this.dr = 0.0f;
        this.dg = 0.0f;
        this.db = 0.0f;
        this.loop = false;
        this.startParticle = 0;
        this.numParticles = 1;
        this.particleInc = 1;
        this.scaleKeys = new float[]{1.0f};
        this.scaleFrames = new float[]{0.0f};
        this.alphaKeys = new float[]{1.0f};
        this.alphaFrames = new float[]{0.0f};
        this.friction = 0.9800000190734863f;
        this.gridSize = 64;
        this.setSize(0.1f, 0.1f);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.xd = xx;
        this.yd = yy;
        this.zd = zz;
    }

    void calculateFrames() {
        this.doneFrames = true;
        if (this.alphaKeys == null) {
            this.setAlphaF(1.0f);
        }
        this.alphaFrames = new float[this.lifetime + 1];
        float inc = (this.alphaKeys.length - 1) / (float) this.lifetime;
        float is = 0.0f;
        for (int a = 0; a <= this.lifetime; ++a) {
            final int isF = Mth.floor(is);
            float diff = (isF < this.alphaKeys.length - 1) ? (diff = this.alphaKeys[isF + 1] - this.alphaKeys[isF]) : 0.0f;
            final float pa = is - isF;
            this.alphaFrames[a] = this.alphaKeys[isF] + diff * pa;
            is += inc;
        }
        if (this.scaleKeys == null) {
            this.setScale(1.0f);
        }
        this.scaleFrames = new float[this.lifetime + 1];
        inc = (this.scaleKeys.length - 1) / (float) this.lifetime;
        is = 0.0f;
        for (int a = 0; a <= this.lifetime; ++a) {
            final int isF = Mth.floor(is);
            float diff = (isF < this.scaleKeys.length - 1) ? (diff = this.scaleKeys[isF + 1] - this.scaleKeys[isF]) : 0.0f;
            final float pa = is - isF;
            this.scaleFrames[a] = this.scaleKeys[isF] + diff * pa;
            is += inc;
        }
    }

    @Override
    public void tick() {
        if (!this.doneFrames) {
            this.calculateFrames();
        }
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        this.oRoll = this.roll;
        this.roll += 3.1415927f * this.rotationSpeed * 2.0f;
        this.yd -= 0.04 * this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;
        this.xd += this.level.random.nextGaussian() * this.randomX;
        this.yd += this.level.random.nextGaussian() * this.randomY;
        this.zd += this.level.random.nextGaussian() * this.randomZ;
        this.xd += this.windX;
        this.zd += this.windZ;
        if (this.onGround && this.friction != 1.0) {
            this.xd *= (double) 0.7F;
            this.zd *= (double) 0.7F;
        }
    }

    public static class ParticleLayer implements ParticleRenderType {
        private final int layer;

        public ParticleLayer(int layer) {

            this.layer = layer;
        }

        @Override
        public void begin(BufferBuilder pBuilder, @NotNull TextureManager pTextureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.setShaderTexture(0, new ResourceLocation("thaumcraft", "textures/misc/particles.png"));
            RenderSystem.enableBlend();

            switch (layer) {
                case 0 -> RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                case 1 -> RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            }

            pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator pTesselator) {
            pTesselator.end();
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return new ParticleLayer(this.layer);
    }

    @Override
    public void render(VertexConsumer wr, Camera camera, float partialTicks) {
        if (this.loop) {
            this.setParticleTextureIndex(this.startParticle + this.age / this.particleInc % this.numParticles);
        } else {
            float fs = this.age / (float) this.lifetime;
            this.setParticleTextureIndex((int) (this.startParticle + Math.min(this.numParticles * fs, (float) (this.numParticles - 1))));
        }
        this.alpha = ((this.alphaFrames.length <= 0) ? 0.0f : this.alphaFrames[Math.min(this.age, this.alphaFrames.length - 1)]);
        this.quadSize = ((this.scaleFrames.length <= 0) ? 0.0f : this.scaleFrames[Math.min(this.age, this.scaleFrames.length - 1)]);

        this.draw(wr, camera, partialTicks);
    }

    public void draw(VertexConsumer wr, Camera camera, float partialTicks) {
        float tx1 = this.particleTextureIndexX / (float) this.gridSize;
        float tx2 = tx1 + 1.0f / (float) this.gridSize;
        float ty1 = this.particleTextureIndexY / (float) this.gridSize;
        float ty2 = ty1 + 1.0f / (float) this.gridSize;
        float ts = 0.1f * this.getQuadSize(partialTicks);

        if (this.sprite != null) {
            tx1 = this.sprite.getU0();
            tx2 = this.sprite.getU1();
            ty1 = this.sprite.getV0();
            ty2 = this.sprite.getV1();
        }

        float fs = Mth.clamp((this.age + partialTicks) / this.lifetime, 0.0f, 1.0f);
        float pr = this.rCol + (this.dr - this.rCol) * fs;
        float pg = this.gCol + (this.dg - this.gCol) * fs;
        float pb = this.bCol + (this.db - this.bCol) * fs;

        int i = this.getLightColor(partialTicks);
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;

        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp((double) partialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) partialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) partialTicks, this.zo, this.z) - vec3.z());
        Quaternion quaternion;
        if (this.angled) {
            float f3 = Mth.lerp(partialTicks, this.oRoll, this.roll);
            quaternion = new Quaternion(this.anglePitch + 90.0f, -this.angleYaw + 90.0f, f3 * 57.29577951308232f, true);
        } else if (this.roll == 0.0F) {
            quaternion = camera.rotation();
        } else {
            quaternion = new Quaternion(camera.rotation());
            float f3 = Mth.lerp(partialTicks, this.oRoll, this.roll);
            quaternion.mul(Vector3f.ZP.rotation(f3));
        }

        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(quaternion);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};

        for (int l = 0; l < 4; ++l) {
            Vector3f vector3f = avector3f[l];
            vector3f.transform(quaternion);
            vector3f.mul(ts);
            vector3f.add(f, f1, f2);
        }
        wr.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).uv(tx2, ty2).color(pr, pg, pb, this.alpha).uv2(j, k).endVertex();
        wr.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).uv(tx2, ty1).color(pr, pg, pb, this.alpha).uv2(j, k).endVertex();
        wr.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).uv(tx1, ty1).color(pr, pg, pb, this.alpha).uv2(j, k).endVertex();
        wr.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).uv(tx1, ty2).color(pr, pg, pb, this.alpha).uv2(j, k).endVertex();
    }

    public void setWind(double d) {
        int m = this.level.getMoonPhase();
        Vec3 vsource = new Vec3(0.0, 0.0, 0.0);
        Vec3 vtar = new Vec3(0.1, 0.0, 0.0);
        vtar = rotateAroundY(vtar, m * (40 + this.level.random.nextInt(10)) / 180.0f * 3.1415927f);
        Vec3 vres = vsource.add(vtar.x, vtar.y, vtar.z);
        this.windX = vres.x * d;
        this.windZ = vres.z * d;
    }

    public void setLayer(final int layer) {
        this.layer = layer;
    }

    private static Vec3 rotateAroundY(Vec3 vec, float angle) {
        float var2 = Mth.cos(angle);
        float var3 = Mth.sin(angle);
        double var4 = vec.x * var2 + vec.z * var3;
        double var5 = vec.y;
        double var6 = vec.z * var2 - vec.x * var3;
        return new Vec3(var4, var5, var6);
    }

    public void setRBGColorF(float particleRedIn, float particleGreenIn, float particleBlueIn) {
        super.setColor(particleRedIn, particleGreenIn, particleBlueIn);
        this.dr = particleRedIn;
        this.dg = particleGreenIn;
        this.db = particleBlueIn;
    }

    public void setRBGColorF(float particleRedIn, float particleGreenIn, float particleBlueIn, float r2, float g2, float b2) {
        super.setColor(particleRedIn, particleGreenIn, particleBlueIn);
        this.dr = r2;
        this.dg = g2;
        this.db = b2;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setRotationSpeed(final float start, final float rot) {
        this.roll = (float) (start * 3.141592653589793 * 2.0);
        this.rotationSpeed = (float) (rot * 0.017453292519943);
    }

    public void setMaxAge(final int max) {
        this.lifetime = max;
    }

    public void setParticles(final int startParticle, final int numParticles, final int particleInc) {
        this.numParticles = numParticles;
        this.particleInc = particleInc;
        this.setParticleTextureIndex(this.startParticle = startParticle);
    }

    public void setScale(final float... scale) {
        this.quadSize = scale[0];
        this.scaleKeys = scale;
    }

    public void setAlphaF(float... a1) {
        super.setAlpha(a1[0]);
        this.alphaKeys = a1;
    }

    public void setSlowDown(float slowDown) {
        this.friction = slowDown;
    }

    public void setRandomMovementScale(float x, float y, float z) {
        this.randomX = x;
        this.randomY = y;
        this.randomZ = z;
    }

    public void setAngles(float yaw, float pitch) {
        this.angleYaw = yaw;
        this.anglePitch = pitch;
        this.angled = true;
    }

    public void setGravity(float g) {
        this.gravity = g;
    }

    public void setParticleTextureIndex(int index) {
        if (index < 0) {
            index = 0;
        }
        this.particleTextureIndexX = index % this.gridSize;
        this.particleTextureIndexY = index / this.gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }
}