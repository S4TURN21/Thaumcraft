package thaumcraft.api.internal;

import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.crafting.IThaumcraftRecipe;

import java.util.HashMap;

public class CommonInternals {
    public static HashMap<String, ResourceLocation> jsonLocs = new HashMap<>();
    public static HashMap<ResourceLocation, IThaumcraftRecipe> craftingRecipeCatalog = new HashMap<>();
    public static HashMap<ResourceLocation, Object> craftingRecipeCatalogFake = new HashMap<>();

    public static IThaumcraftRecipe getCatalogRecipe(ResourceLocation key) {
        return CommonInternals.craftingRecipeCatalog.get(key);
    }

    public static Object getCatalogRecipeFake(ResourceLocation key) {
        return CommonInternals.craftingRecipeCatalogFake.get(key);
    }
}
