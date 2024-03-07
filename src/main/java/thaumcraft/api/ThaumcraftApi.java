package thaumcraft.api;

import net.minecraft.resources.ResourceLocation;
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
}
