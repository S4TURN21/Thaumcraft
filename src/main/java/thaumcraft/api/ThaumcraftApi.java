package thaumcraft.api;

import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.internal.CommonInternals;

public class ThaumcraftApi {
    public static void registerResearchLocation(ResourceLocation loc) {
        if (!CommonInternals.jsonLocs.containsKey(loc.toString())) {
            CommonInternals.jsonLocs.put(loc.toString(), loc);
        }
    }
}
