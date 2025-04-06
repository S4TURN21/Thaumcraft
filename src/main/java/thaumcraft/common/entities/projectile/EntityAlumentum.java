package thaumcraft.common.entities.projectile;

import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigEntities;

public class EntityAlumentum extends ThrowableProjectile {
    public EntityAlumentum(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public EntityAlumentum(LivingEntity pShooter, Level pLevel) {
        super(ConfigEntities.ALUMENTUM, pShooter, pLevel);
    }

    public EntityAlumentum(Level pLevel, double pX, double pY, double pZ) {
        super(ConfigEntities.ALUMENTUM, pX, pY, pZ, pLevel);
    }

    @Override
    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        super.shoot(pX, pY, pZ, 0.75f, pInaccuracy);
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide) {
            for (double i = 0.0; i < 3.0; ++i) {
                double coeff = i / 3.0;
                FXDispatcher.INSTANCE.drawAlumentum((float) (xo + (getX() - xo) * coeff), (float) (yo + (getY() - yo) * coeff), (float) (zo + (getZ() - zo) * coeff), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), random.nextFloat() * 0.2f, random.nextFloat() * 0.1f, random.nextFloat() * 0.1f, 0.5f, 4.0f);
                FXDispatcher.INSTANCE.drawGenericParticles(getX() + level.random.nextGaussian() * 0.2, getY() + level.random.nextGaussian() * 0.2, getZ() + level.random.nextGaussian() * 0.2, 0.0, 0.0, 0.0, 1.0f, 1.0f, 1.0f, 0.7f, false, 448, 8, 1, 8, 0, 0.3f, 0.0f, ParticleRenderType.PARTICLE_SHEET_OPAQUE);
            }
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
        if (!level.isClientSide) {
            level.explode(this, getX(), getY(), getZ(), 1.1f, Explosion.BlockInteraction.BREAK);
            discard();
        }
    }

    @Override
    public void defineSynchedData() {
    }
}
