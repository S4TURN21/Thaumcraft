package thaumcraft.common.config;

import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.lib.research.ResearchManager;

public class ConfigResearch {
    public static String[] TCCategories = new String[]{"BASICS", "ALCHEMY", "AUROMANCY", "ARTIFICE", "INFUSION", "GOLEMANCY", "ELDRITCH"};
    private static final ResourceLocation BACK_OVER = new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png");

    public static void postInit() {
        ResearchManager.parseAllResearch();
    }

    public static void init() {
        initCategories();
        for (String cat : ConfigResearch.TCCategories) {
            ThaumcraftApi.registerResearchLocation(new ResourceLocation("thaumcraft", "research/" + cat.toLowerCase()));
        }
    }

    private static void initCategories() {
        ResearchCategories.registerCategory("BASICS", null, new AspectList().add(Aspect.PLANT, 5).add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 5).add(Aspect.AIR, 5).add(Aspect.FIRE, 5).add(Aspect.EARTH, 3).add(Aspect.WATER, 5), new ResourceLocation("thaumcraft", "textures/item/thaumonomicon_cheat.png"), new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_1.jpg"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("AUROMANCY", "UNLOCKAUROMANCY", new AspectList().add(Aspect.AURA, 20).add(Aspect.MAGIC, 20).add(Aspect.FLUX, 15).add(Aspect.CRYSTAL, 5).add(Aspect.COLD, 5).add(Aspect.AIR, 5), new ResourceLocation("thaumcraft", "textures/research/cat_auromancy.png"), new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_2.jpg"), ConfigResearch.BACK_OVER);
    }
}
