package thaumcraft.api.aspects;

import net.minecraft.world.item.ItemStack;

public interface IEssentiaContainerItem {
    AspectList getAspects(ItemStack itemstack);

    void setAspects(ItemStack itemstack, AspectList aspects);

    boolean ignoreContainedAspects();
}
