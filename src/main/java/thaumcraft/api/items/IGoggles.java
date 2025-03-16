package thaumcraft.api.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IGoggles {
    boolean showIngamePopups(ItemStack itemstack, LivingEntity player);
}
