package thaumcraft.api.aspects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.internal.CommonInternals;

import java.util.Random;

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

    public static AspectList getEntityAspects(Entity entity) {
        AspectList tags = null;
        String entityString = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
        if (entity instanceof Player) {
            tags = new AspectList();
            tags.add(Aspect.MAN, 4);
            Random rand = new Random(entity.getName().hashCode());
            Aspect[] posa = Aspect.aspects.values().toArray(new Aspect[]{});
            tags.add(posa[rand.nextInt(posa.length)], 15);
            tags.add(posa[rand.nextInt(posa.length)], 15);
            tags.add(posa[rand.nextInt(posa.length)], 15);
        } else {
            f1:
            for (ThaumcraftApi.EntityTags et : CommonInternals.scanEntities) {
                if (!et.entityName.equals(entityString)) continue;
                if (et.nbts == null || et.nbts.length == 0) {
                    tags = et.aspects;
                } else {
                    CompoundTag tc = new CompoundTag();
                    entity.deserializeNBT(tc);
                    for (ThaumcraftApi.EntityTagsNBT nbt : et.nbts) {
                        if (tc.contains(nbt.name)) {
                            if (!ThaumcraftApiHelper.getNBTDataFromId(tc, tc.getTagType(nbt.name), nbt.name).equals(nbt.value))
                                continue f1;
                        } else {
                            continue f1;
                        }
                    }
                    tags = et.aspects;
                }
            }
        }
        return tags;
    }
}
