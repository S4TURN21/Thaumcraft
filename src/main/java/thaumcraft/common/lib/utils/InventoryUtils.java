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

    public static ItemStack cycleItemStack(final Object input) {
        return cycleItemStack(input, 0);
    }

    public static ItemStack cycleItemStack(Object input, int counter) {
        ItemStack it = ItemStack.EMPTY;
        if (input instanceof ItemStack) {
            it = (ItemStack) input;
            if (it != null && !it.isEmpty() && it.getItem() != null && it.isDamageableItem() && it.getDamageValue() == Short.MAX_VALUE) {
                int q3 = 5000 / it.getMaxDamage();
                int md = (int) ((counter + System.currentTimeMillis() / q3) % it.getMaxDamage());
                ItemStack it2 = new ItemStack(it.getItem(), 1);
                it2.setTag(it.getTag());
                it = it2;
            }
        }
        return it;
    }
}
