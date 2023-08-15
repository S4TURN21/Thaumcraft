package thaumcraft.common.lib.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class InventoryUtils {
    public static void dropItemAtEntity(Level world, ItemStack item, Entity entity) {
        if (!world.isClientSide && item != null && !item.isEmpty() && item.getCount() > 0) {
            ItemEntity entityItem = new ItemEntity(world, entity.getX(), entity.getY() + entity.getEyeHeight() / 2.0f, entity.getZ(), item.copy());
            world.addFreshEntity(entityItem);
        }
    }
}
