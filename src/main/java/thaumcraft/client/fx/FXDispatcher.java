package thaumcraft.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.client.fx.particles.FXBlockRunes;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.blockentities.crafting.BlockEntityCrucible;
import thaumcraft.common.lib.SoundsTC;

import java.awt.*;

public class FXDispatcher {
    public static FXDispatcher INSTANCE = new FXDispatcher();

    public ClientLevel getWorld() {
        return Minecraft.getInstance().level;
    }

    public void drawPollutionParticles(BlockPos p) {
        float x = p.getX() + 0.2f + getWorld().random.nextFloat() * 0.6f;
        float y = p.getY() + 0.2f + getWorld().random.nextFloat() * 0.6f;
        float z = p.getZ() + 0.2f + getWorld().random.nextFloat() * 0.6f;
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, (getWorld().random.nextFloat() - getWorld().random.nextFloat()) * 0.005, 0.02, (getWorld().random.nextFloat() - getWorld().random.nextFloat()) * 0.005);
        fb.setMaxAge(100 + getWorld().random.nextInt(60));
        fb.setRBGColorF(1.0f, 0.3f, 0.9f);
        fb.setAlphaF(0.5f, 0.0f);
        fb.setGridSize(16);
        fb.setParticles(56, 1, 1);
        fb.setScale(2.0f, 5.0f);
        fb.setLayer(1);
        fb.setSlowDown(1.0f);
        fb.setWind(0.001);
        fb.setRotationSpeed(getWorld().random.nextFloat(), getWorld().random.nextBoolean() ? -1.0f : 1.0f);
        ParticleEngine.addEffect(getWorld(), fb);
    }

    //TODO: Fix particles don't fall after block disappearing
    public void drawBlockSparkles(BlockPos p, Vec3 start) {
        AABB bs = this.getWorld().getBlockState(p).getShape(this.getWorld(), p).bounds();
        bs.inflate(0.1, 0.1, 0.1);
        int num = (int) (bs.getSize() * 20.0);
        for (Direction face : Direction.values()) {
            BlockState state = this.getWorld().getBlockState(p.offset(face.getNormal()));
            if (!state.isFaceSturdy(this.getWorld(), p.offset(face.getNormal()), face.getOpposite())) {
                boolean rx = face.getStepX() == 0;
                boolean ry = face.getStepY() == 0;
                boolean rz = face.getStepZ() == 0;
                double mx = 0.5 + face.getStepX() * 0.51;
                double my = 0.5 + face.getStepY() * 0.51;
                double mz = 0.5 + face.getStepZ() * 0.51;
                for (int a = 0; a < num * 2; ++a) {
                    double x = mx;
                    double y = my;
                    double z = mz;
                    if (rx) {
                        x += this.getWorld().random.nextGaussian() * 0.6;
                    }
                    if (ry) {
                        y += this.getWorld().random.nextGaussian() * 0.6;
                    }
                    if (rz) {
                        z += this.getWorld().random.nextGaussian() * 0.6;
                    }
                    x = Mth.clamp(x, bs.minX, bs.maxX);
                    y = Mth.clamp(y, bs.minY, bs.maxY);
                    z = Mth.clamp(z, bs.minZ, bs.maxZ);
                    float r = Mth.randomBetweenInclusive(this.getWorld().random, 255, 255) / 255.0f;
                    float g = Mth.randomBetweenInclusive(this.getWorld().random, 189, 255) / 255.0f;
                    float b = Mth.randomBetweenInclusive(this.getWorld().random, 64, 255) / 255.0f;
                    Vec3 v1 = new Vec3(p.getX() + x, p.getY() + y, p.getZ() + z);
                    double delay = this.getWorld().random.nextInt(5) + v1.distanceTo(start) * 16.0;
                    this.drawSimpleSparkle(this.getWorld().random, p.getX() + x, p.getY() + y, p.getZ() + z, 0.0, 0.0025, 0.0, 0.4f + (float) this.getWorld().random.nextGaussian() * 0.1f, r, g, b, (int) delay, 1.0f, 0.01f, 16);
                }
            }
        }
    }

    public void drawSimpleSparkle(RandomSource rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) {
        boolean sp = rand.nextFloat() < 0.2;
        FXGeneric fb = new FXGeneric(this.getWorld(), x, y, z, x2, y2, z2);
        int age = baseAge * 4 + this.getWorld().random.nextInt(baseAge);
        fb.setMaxAge(age);
        fb.setRBGColorF(r, g, b);
        float[] alphas = new float[6 + rand.nextInt(age / 3)];
        for (int a = 1; a < alphas.length - 1; ++a) {
            alphas[a] = rand.nextFloat();
        }
        fb.setAlphaF(alphas);
        fb.setParticles(sp ? 320 : 512, 16, 1);
        fb.setLoop(true);
        fb.setGravity(grav);
        fb.setScale(scale, scale * 2.0f);
        fb.setLayer(0);
        fb.setSlowDown(decay);
        fb.setRandomMovementScale(5.0E-4f, 0.001f, 5.0E-4f);
        fb.setWind(5.0E-4);
        Minecraft.getInstance().particleEngine.add(fb);
    }

    public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float grav) {
        this.drawWispyMotes(d, e, f, vx, vy, vz, age, 0.25f + this.getWorld().random.nextFloat() * 0.75f, 0.25f + this.getWorld().random.nextFloat() * 0.75f, 0.25f + this.getWorld().random.nextFloat() * 0.75f, grav);
    }

    public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) {
        FXGeneric fb = new FXGeneric(this.getWorld(), d, e, f, vx, vy, vz);
        fb.setMaxAge((int) (age + age / 2 * this.getWorld().random.nextFloat()));
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(0.0f, 0.6f, 0.6f, 0.0f);
        fb.setGridSize(64);
        fb.setParticles(512, 16, 1);
        fb.setScale(1.0f, 0.5f);
        fb.setLoop(true);
        fb.setWind(0.001);
        fb.setGravity(grav);
        fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);
        Minecraft.getInstance().particleEngine.add(fb);
    }

    public void crucibleBubble(float x, float y, float z, float cr, float cg, float cb) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0);
        fb.setMaxAge(15 + getWorld().random.nextInt(10));
        fb.setScale(getWorld().random.nextFloat() * 0.3f + 0.3f);
        fb.setRBGColorF(cr, cg, cb);
        fb.setRandomMovementScale(0.002f, 0.002f, 0.002f);
        fb.setGravity(-0.001f);
        fb.setParticle(64);
        fb.setFinalFrames(65, 66, 66);
        ParticleEngine.addEffect(getWorld(), fb);
    }

    public void crucibleBoil(BlockPos pos, BlockEntityCrucible crucible, int pType) {
        for (int a = 0; a < 2; ++a) {
            FXGeneric fb = new FXGeneric(getWorld(), pos.getX() + 0.2f + getWorld().random.nextFloat() * 0.6f, pos.getY() + 0.1f + crucible.getFluidHeight(), pos.getZ() + 0.2f + getWorld().random.nextFloat() * 0.6f, 0.0, 0.002, 0.0);
            fb.setMaxAge((int) (7.0 + 8.0 / (Math.random() * 0.8 + 0.2)));
            fb.setScale(getWorld().random.nextFloat() * 0.3f + 0.2f);
            if (crucible.aspects.size() == 0) {
                fb.setRBGColorF(1.0f, 1.0f, 1.0f);
            } else {
                Color color = new Color(crucible.aspects.getAspects()[getWorld().random.nextInt(crucible.aspects.getAspects().length)].getColor());
                fb.setRBGColorF(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
            }
            fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);
            fb.setGravity(-0.025f * pType);
            fb.setParticle(64);
            fb.setFinalFrames(65, 66);
            ParticleEngine.addEffect(getWorld(), fb);
        }
    }

    public void crucibleFroth(float x, float y, float z) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0);
        fb.setMaxAge(4 + getWorld().random.nextInt(3));
        fb.setScale(getWorld().random.nextFloat() * 0.2f + 0.2f);
        fb.setRBGColorF(0.5f, 0.5f, 0.7f);
        fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);
        fb.setGravity(0.1f);
        fb.setParticle(64);
        fb.setFinalFrames(65, 66);
        ParticleEngine.addEffect(getWorld(), fb);
    }

    public void crucibleFrothDown(float x, float y, float z) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0);
        fb.setMaxAge(12 + getWorld().random.nextInt(12));
        fb.setScale(getWorld().random.nextFloat() * 0.2f + 0.4f);
        fb.setRBGColorF(0.25f, 0.0f, 0.75f);
        fb.setAlphaF(0.8f);
        fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);
        fb.setGravity(0.05f);
        fb.setNoClip(false);
        fb.setParticle(73);
        fb.setFinalFrames(65, 66);
        fb.setLayer(1);
        ParticleEngine.addEffect(getWorld(), fb);
    }

    public void drawBamf(BlockPos p, boolean sound, boolean flair, Direction side) {
        drawBamf(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, sound, flair, side);
    }

    public void drawBamf(double x, double y, double z, int color, boolean sound, boolean flair, Direction side) {
        Color c = new Color(color);
        float r = c.getRed() / 255.0f;
        float g = c.getGreen() / 255.0f;
        float b = c.getBlue() / 255.0f;
        this.drawBamf(x, y, z, r, g, b, sound, flair, side);
    }

    public void drawBamf(double x, double y, double z, boolean sound, boolean flair, Direction side) {
        this.drawBamf(x, y, z, 0.5f, 0.1f, 0.6f, sound, flair, side);
    }

    public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, Direction side) {
        if (sound) {
            this.getWorld().playLocalSound(x, y, z, SoundsTC.poof, SoundSource.BLOCKS, 0.4f, 1.0f + (float) this.getWorld().random.nextGaussian() * 0.05f, false);
        }
        for (int a = 0; a < 6 + this.getWorld().random.nextInt(3) + 2; ++a) {
            double vx = (0.05f + this.getWorld().random.nextFloat() * 0.05f) * (this.getWorld().random.nextBoolean() ? -1 : 1);
            double vy = (0.05f + this.getWorld().random.nextFloat() * 0.05f) * (this.getWorld().random.nextBoolean() ? -1 : 1);
            double vz = (0.05f + this.getWorld().random.nextFloat() * 0.05f) * (this.getWorld().random.nextBoolean() ? -1 : 1);
            if (side != null) {
                vx += side.getStepX() * 0.1f;
                vy += side.getStepY() * 0.1f;
                vz += side.getStepZ() * 0.1f;
            }
            FXGeneric fb2 = new FXGeneric(this.getWorld(), x + vx * 2.0, y + vy * 2.0, z + vz * 2.0, vx / 2.0, vy / 2.0, vz / 2.0);
            fb2.setLifetime(20 + this.getWorld().random.nextInt(15));
            fb2.setRBGColorF(Mth.clamp(r * (1.0f + (float) this.getWorld().random.nextGaussian() * 0.1f), 0.0f, 1.0f), Mth.clamp(g * (1.0f + (float) this.getWorld().random.nextGaussian() * 0.1f), 0.0f, 1.0f), Mth.clamp(b * (1.0f + (float) this.getWorld().random.nextGaussian() * 0.1f), 0.0f, 1.0f));
            fb2.setAlphaF(1.0f, 0.1f);
            fb2.setGridSize(16);
            fb2.setParticles(123, 5, 1);
            fb2.setScale(3.0f, 4.0f + this.getWorld().random.nextFloat() * 3.0f);
            fb2.setLayer(1);
            fb2.setSlowDown(0.7f);
            fb2.setRotationSpeed(this.getWorld().random.nextFloat(), this.getWorld().random.nextBoolean() ? -1.0f : 1.0f);
            Minecraft.getInstance().particleEngine.add(fb2);
        }
        if (flair) {
            for (int a = 0; a < 2 + this.getWorld().random.nextInt(3); ++a) {
                double vx = (0.025f + this.getWorld().random.nextFloat() * 0.025f) * (this.getWorld().random.nextBoolean() ? -1 : 1);
                double vy = (0.025f + this.getWorld().random.nextFloat() * 0.025f) * (this.getWorld().random.nextBoolean() ? -1 : 1);
                double vz = (0.025f + this.getWorld().random.nextFloat() * 0.025f) * (this.getWorld().random.nextBoolean() ? -1 : 1);
                this.drawWispyMotes(x + vx * 2.0, y + vy * 2.0, z + vz * 2.0, vx, vy, vz, 15 + this.getWorld().random.nextInt(10), -0.01f);
            }
            FXGeneric fb3 = new FXGeneric(this.getWorld(), x, y, z, 0.0, 0.0, 0.0);
            fb3.setMaxAge(10 + this.getWorld().random.nextInt(5));
            fb3.setRBGColorF(1.0f, 0.9f, 1.0f);
            fb3.setAlphaF(1.0f, 0.0f);
            fb3.setGridSize(16);
            fb3.setParticles(77, 1, 1);
            fb3.setScale(10.0f + this.getWorld().random.nextFloat() * 2.0f, 0.0f);
            fb3.setLayer(0);
            fb3.setRotationSpeed(this.getWorld().random.nextFloat(), (float) this.getWorld().random.nextGaussian());
            Minecraft.getInstance().particleEngine.add(fb3);
        }
        for (int a = 0; a < (flair ? 2 : 0) + this.getWorld().random.nextInt(3); ++a) {
            this.drawCurlyWisp(x, y, z, 0.0, 0.0, 0.0, 1.0f, (0.9f + this.getWorld().random.nextFloat() * 0.1f + r) / 2.0f, (0.1f + g) / 2.0f, (0.5f + this.getWorld().random.nextFloat() * 0.1f + b) / 2.0f, 0.75f, side, a, 0, 0);
        }
    }

    public void drawCurlyWisp(double x, double y, double z, double vx, double vy, double vz, float scale, float r, float g, float b, float a, Direction side, int seed, int layer, int delay) {
        if (this.getWorld() == null) {
            return;
        }
        vx += (0.0025f + this.getWorld().random.nextFloat() * 0.005f) * (this.getWorld().random.nextBoolean() ? -1 : 1);
        vy += (0.0025f + this.getWorld().random.nextFloat() * 0.005f) * (this.getWorld().random.nextBoolean() ? -1 : 1);
        vz += (0.0025f + this.getWorld().random.nextFloat() * 0.005f) * (this.getWorld().random.nextBoolean() ? -1 : 1);
        if (side != null) {
            vx += side.getStepX() * 0.025f;
            vy += side.getStepY() * 0.025f;
            vz += side.getStepZ() * 0.025f;
        }
        FXGeneric fb2 = new FXGeneric(this.getWorld(), x + vx * 5.0, y + vy * 5.0, z + vz * 5.0, vx, vy, vz);
        if (seed > 0 && this.getWorld().random.nextBoolean()) {
            fb2.setAngles(90.0f * (float) this.getWorld().random.nextGaussian(), 90.0f * (float) this.getWorld().random.nextGaussian());
        }
        fb2.setMaxAge(25 + this.getWorld().random.nextInt(20 + 20 * seed));
        fb2.setRBGColorF(r, g, b, 0.1f, 0.0f, 0.1f);
        fb2.setAlphaF(a, 0.0f);
        fb2.setGridSize(16);
        fb2.setParticles(60 + this.getWorld().random.nextInt(4), 1, 1);
        fb2.setScale(5.0f * scale, (10.0f + this.getWorld().random.nextFloat() * 4.0f) * scale);
        fb2.setLayer(layer);
        fb2.setRotationSpeed(this.getWorld().random.nextFloat(), this.getWorld().random.nextBoolean() ? (-2.0f - this.getWorld().random.nextFloat() * 2.0f) : (2.0f + this.getWorld().random.nextFloat() * 2.0f));
        Minecraft.getInstance().particleEngine.add(fb2);
    }

    public void scanHighlight(BlockPos p) {
        AABB bb = getWorld().getBlockState(p).getShape(getWorld(), p).bounds();
        bb = bb.move(p);
        scanHighlight(bb);
    }

    public void scanHighlight(AABB bb) {
        int num = Mth.ceil(bb.getSize() * 2.0);
        double ax = (bb.minX + bb.maxX) / 2.0;
        double ay = (bb.minY + bb.maxY) / 2.0;
        double az = (bb.minZ + bb.maxZ) / 2.0;
        for (Direction face : Direction.values()) {
            double mx = 0.5 + face.getStepX() * 0.51;
            double my = 0.5 + face.getStepY() * 0.51;
            double mz = 0.5 + face.getStepZ() * 0.51;
            for (int a = 0; a < num * 2; ++a) {
                double x = mx;
                double y = my;
                double z = mz;
                x += getWorld().random.nextGaussian() * (bb.maxX - bb.minX);
                y += getWorld().random.nextGaussian() * (bb.maxY - bb.minY);
                z += getWorld().random.nextGaussian() * (bb.maxZ - bb.minZ);
                x = Mth.clamp(x, bb.minX - ax, bb.maxX - ax);
                y = Mth.clamp(y, bb.minY - ay, bb.maxY - ay);
                z = Mth.clamp(z, bb.minZ - az, bb.maxZ - az);
                float r = getWorld().random.nextInt(16, 32) / 255.0f;
                float g = getWorld().random.nextInt(132, 165) / 255.0f;
                float b = getWorld().random.nextInt(223, 239) / 255.0f;
                drawSimpleSparkle(getWorld().random, ax + x, ay + y, az + z, 0.0, 0.0, 0.0, 0.4f + (float) getWorld().random.nextGaussian() * 0.1f, r, g, b, getWorld().random.nextInt(10), 1.0f, 0.0f, 4);
            }
        }
    }

    public void blockRunes(double x, double y, double z, float r, float g, float b, int dur, float grav) {
        FXBlockRunes fb = new FXBlockRunes(getWorld(), x + 0.5, y + 0.5, z + 0.5, r, g, b, dur);
        fb.setGravity(grav);
        Minecraft.getInstance().particleEngine.add(fb);
    }

    public void drawNitorCore(double x, double y, double z, double x2, double y2, double z2) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);
        fb.setMaxAge(10);
        fb.setRBGColorF(1.0f, 1.0f, 1.0f);
        fb.setAlphaF(1.0f);
        fb.setParticles(457, 1, 1);
        fb.setScale(1.0f, 1.0f + (float) getWorld().random.nextGaussian() * 0.1f, 1.0f);
        fb.setLayer(1);
        fb.setRandomMovementScale(2.0E-4f, 2.0E-4f, 2.0E-4f);
        ParticleEngine.addEffect(getWorld(), fb);
    }

    public void drawNitorFlames(double x, double y, double z, double x2, double y2, double z2, int color, int a) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);
        fb.setMaxAge(10 + getWorld().random.nextInt(5));
        Color c = new Color(color);
        fb.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
        fb.setAlphaF(0.66f);
        fb.setLoop(true);
        fb.setGridSize(64);
        fb.setParticles(264, 8, 1);
        fb.setScale(3.0f + getWorld().random.nextFloat(), 0.05f);
        fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);
        ParticleEngine.addEffectWithDelay(getWorld(), fb, a);
    }
}
