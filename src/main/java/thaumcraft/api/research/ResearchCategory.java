package thaumcraft.api.research;

import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.aspects.AspectList;

import java.util.HashMap;
import java.util.Map;

public class ResearchCategory {
    public int minDisplayColumn;
    public int minDisplayRow;
    public int maxDisplayColumn;
    public int maxDisplayRow;
    public ResourceLocation icon;
    public ResourceLocation background;
    public ResourceLocation background2;
    public String researchKey;
    public String key;
    public AspectList formula;
    public Map<String, ResearchEntry> research;

    public ResearchCategory(String key, String researchkey, AspectList formula, ResourceLocation icon, ResourceLocation background) {
        this.research = new HashMap<>();
        this.key = key;
        this.researchKey = researchkey;
        this.icon = icon;
        this.background = background;
        this.background2 = null;
        this.formula = formula;
    }

    public ResearchCategory(String key, String researchKey, AspectList formula, ResourceLocation icon, ResourceLocation background, ResourceLocation background2) {
        this.research = new HashMap<>();
        this.key = key;
        this.researchKey = researchKey;
        this.icon = icon;
        this.background = background;
        this.background2 = background2;
        this.formula = formula;
    }
}
