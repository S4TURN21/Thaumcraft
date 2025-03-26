package thaumcraft.common.entities;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.common.config.ConfigEntities;

public class EntitySpecialItem extends ItemEntity {
    public EntitySpecialItem(EntityType<? extends ItemEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public EntitySpecialItem(Level pLevel, double pPosX, double pPosY, double pPosZ, ItemStack pItemStack) {
        this(ConfigEntities.SPECIAL_ITEM, pLevel);
        this.setPos(pPosX, pPosY, pPosZ);
        this.setDeltaMovement(Math.random() * 0.2 - 0.1, 0.2, Math.random() * 0.2 - 0.1);
        this.setItem(pItemStack);
        this.setNoGravity(true);
    }

    @Override
    public boolean hurt(DamageSource source, float pAmount) {
        return !source.isExplosion() && super.hurt(source, pAmount);
    }
}
