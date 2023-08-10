package thaumcraft.api;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;

public class ThaumcraftApiHelper {
    public static ItemStack makeCrystal(Aspect aspect, int stackSize) {
        if (aspect == null) {
            return null;
        }
        ItemStack is = new ItemStack(ItemsTC.crystalEssence, stackSize);
        ((ItemGenericEssentiaContainer) ItemsTC.crystalEssence).setAspects(is, new AspectList().add(aspect, 1));
        return is;
    }

    public static ItemStack makeCrystal(Aspect aspect) {
        return makeCrystal(aspect, 1);
    }
}
