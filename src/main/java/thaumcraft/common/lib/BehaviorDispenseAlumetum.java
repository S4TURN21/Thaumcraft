package thaumcraft.common.lib;

import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import thaumcraft.common.entities.projectile.EntityAlumentum;

public class BehaviorDispenseAlumetum extends AbstractProjectileDispenseBehavior {
    @Override
    protected @NotNull Projectile getProjectile(@NotNull Level pLevel, Position pPosition, @NotNull ItemStack pStack) {
        return new EntityAlumentum(pLevel, pPosition.x(), pPosition.y(), pPosition.z());
    }
}
