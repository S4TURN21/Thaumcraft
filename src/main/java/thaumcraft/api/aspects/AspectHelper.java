package thaumcraft.api.aspects;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;

public class AspectHelper {
    public static AspectList cullTags(AspectList temp) {
        return cullTags(temp, 7);
    }

    public static AspectList cullTags(AspectList temp, int cap) {
        AspectList temp2 = new AspectList();
        for (Aspect tag : temp.getAspects()) {
            if (tag != null)
                temp2.add(tag, temp.getAmount(tag));
        }
        while (temp2 != null && temp2.size() > cap) {
            Aspect lowest = null;
            float low = Short.MAX_VALUE;
            for (Aspect tag : temp2.getAspects()) {
                if (tag == null) continue;
                float ta = temp2.getAmount(tag);
                if (tag.isPrimal()) {
                    ta *= .9f;
                } else {
                    if (!tag.getComponents()[0].isPrimal()) {
                        ta *= 1.1f;
                        if (!tag.getComponents()[0].getComponents()[0].isPrimal()) {
                            ta *= 1.05f;
                        }
                        if (!tag.getComponents()[0].getComponents()[1].isPrimal()) {
                            ta *= 1.05f;
                        }
                    }
                    if (!tag.getComponents()[1].isPrimal()) {
                        ta *= 1.1f;
                        if (!tag.getComponents()[1].getComponents()[0].isPrimal()) {
                            ta *= 1.05f;
                        }
                        if (!tag.getComponents()[1].getComponents()[1].isPrimal()) {
                            ta *= 1.05f;
                        }
                    }
                }

                if (ta < low) {
                    low = ta;
                    lowest = tag;
                }
            }
            temp2.aspects.remove(lowest);
        }
        return temp2;
    }

    public static AspectList getObjectAspects(ItemStack is) {
        return ThaumcraftApi.internalMethods.getObjectAspects(is);
    }

    public static AspectList generateTags(ItemStack is) {
        return ThaumcraftApi.internalMethods.generateTags(is);
    }
}
