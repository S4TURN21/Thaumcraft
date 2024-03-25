package thaumcraft.common.config;

import net.minecraft.tags.BlockTags;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.CommonInternals;

public class ConfigAspects {
    public static void postInit() {
        CommonInternals.objectTags.clear();
        registerItemAspects();
    }

    private static void registerItemAspects() {
        ThaumcraftApi.registerObjectTag(BlockTags.BASE_STONE_OVERWORLD, new AspectList().add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag(BlockTags.SAND, new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5));
    }
}
