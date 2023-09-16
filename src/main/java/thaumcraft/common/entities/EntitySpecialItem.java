package thaumcraft.common.entities;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntitySpecialItem extends ItemEntity {
    public EntitySpecialItem(EntityType<EntitySpecialItem> entityType, Level level) {
        super(entityType, level);
    }

    public EntitySpecialItem(Level pLevel, double x, double y, double z, ItemStack pItemStack) {
        this((EntityType<EntitySpecialItem>) Registry.ENTITY_TYPE.get(new ResourceLocation("thaumcraft", "special_item")), pLevel);
        this.setPos(x, y, z);
        this.setNoGravity(true);
        this.setItem(pItemStack);
    }

    @Override
    public boolean hurt(DamageSource source, float pAmount) {
        return !source.isExplosion() && super.hurt(source, pAmount);
    }
}
