package thaumcraft.api.aspects;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.tags.ITagManager;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.CommonInternals;

public class AspectEventProxy {
    public void registerObjectTag(ItemStack item, AspectList aspects) {
        if (aspects == null) aspects = new AspectList();
        try {
            CommonInternals.objectTags.put(CommonInternals.generateUniqueItemstackId(item), aspects);
        } catch (Exception ignored) {
        }
    }

    public <T extends ItemLike> void registerObjectTag(TagKey<T> oreDict, AspectList aspects) {
        if (aspects == null) aspects = new AspectList();
        ITagManager<T> ores = RegistryManager.ACTIVE.getRegistry(oreDict.registry()).tags();
        if (ores != null) {
            for (ItemStack ore : ores.getTag(oreDict).stream().map(ItemStack::new).toList()) {
                try {
                    ItemStack oc = ore.copy();
                    oc.setCount(1);
                    registerObjectTag(oc, aspects.copy());
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void registerComplexObjectTag(ItemStack item, AspectList aspects ) {
        if(!ThaumcraftApi.exists(item)) {
            AspectList tmp = AspectHelper.generateTags(item);
            if(tmp != null && tmp.size() > 0)
            {
                for(Aspect tag : tmp.getAspects())
                {
                    aspects.add(tag, tmp.getAmount(tag));
                }
            }
            registerObjectTag(item, aspects);
        }
        else {
            AspectList tmp = AspectHelper.getObjectAspects(item);
            for(Aspect tag : aspects.getAspects())
            {
                tmp.merge(tag, tmp.getAmount(tag));
            }
            registerObjectTag(item, tmp);
        }
    }
}
