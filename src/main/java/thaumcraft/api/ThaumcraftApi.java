package thaumcraft.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.internal.DummyInternalMethodHandler;
import thaumcraft.api.internal.IInternalMethodHandler;

import java.util.HashMap;

public class ThaumcraftApi {
    public static IInternalMethodHandler internalMethods = new DummyInternalMethodHandler();

    public static void registerResearchLocation(ResourceLocation loc) {
        if (!CommonInternals.jsonLocs.containsKey(loc.toString())) {
            CommonInternals.jsonLocs.put(loc.toString(), loc);
        }
    }

    public static HashMap<ResourceLocation, Object> getCraftingRecipesFake() {
        return CommonInternals.craftingRecipeCatalogFake;
    }

    public static void addFakeCraftingRecipe(ResourceLocation registry, Object recipe) {
        getCraftingRecipesFake().put(registry, recipe);
    }

    public static boolean exists(ItemStack item) {
        ItemStack stack = item.copy();
        stack.setCount(1);
        AspectList tmp = CommonInternals.objectTags.get(stack.serializeNBT().toString());
        if (tmp == null) {
            try {
                stack.setDamageValue(Short.MAX_VALUE);
                tmp = CommonInternals.objectTags.get(stack.serializeNBT().toString());
                if (item.getDamageValue() == Short.MAX_VALUE && tmp == null) {
                    int index = 0;
                    do {
                        stack.setDamageValue(index);
                        tmp = CommonInternals.objectTags.get(stack.serializeNBT().toString());
                        index++;
                    } while (index < 16 && tmp == null);
                }
                if (tmp == null) return false;
            } catch (Exception e) {
            }
        }

        return true;
    }

    public static void registerObjectTag(ItemStack item, AspectList aspects) {
        (new AspectEventProxy()).registerObjectTag(item, aspects);
    }

    public static <T extends ItemLike> void registerObjectTag(TagKey<T> tag, AspectList aspects) {
        (new AspectEventProxy()).registerObjectTag(tag, aspects);
    }
}
