package thaumcraft.api.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.AspectList;

public class ItemGenericEssentiaContainer extends Item {
    protected int base;
    public ItemGenericEssentiaContainer(Item.Properties pProperties, int base) {
        super(pProperties);
        this.base = base;
    }
    public AspectList getAspects(ItemStack itemstack) {
        if (itemstack.hasTag()) {
            AspectList aspects = new AspectList();
            aspects.readFromNBT(itemstack.getTag());
            return (aspects.size() > 0) ? aspects : null;
        }
        return null;
    }
    public void setAspects(ItemStack itemstack, AspectList aspects) {
        if (!itemstack.hasTag()) {
            itemstack.setTag(new CompoundTag());
        }
        aspects.writeToNBT(itemstack.getTag());
    }
}
