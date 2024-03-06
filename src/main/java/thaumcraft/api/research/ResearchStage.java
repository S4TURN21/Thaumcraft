package thaumcraft.api.research;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.capabilities.IPlayerKnowledge;

public class ResearchStage {
    String text;
    ResourceLocation[] recipes;
    Object[] obtain;
    Object[] craft;
    int[] craftReference;
    Knowledge[] know;
    String[] research;
    String[] researchIcon;
    int warp;

    public String getText() {
        return this.text;
    }
    public String getTextLocalized() {
        return Component.translatable(getText()).getString();
    }

    public void setText(String text) {
        this.text = text;
    }

    public ResourceLocation[] getRecipes() {
        return this.recipes;
    }

    public void setRecipes(ResourceLocation[] recipes) {
        this.recipes = recipes;
    }

    public Object[] getObtain() {
        return this.obtain;
    }

    public void setObtain(Object[] obtain) {
        this.obtain = obtain;
    }

    public Object[] getCraft() {
        return this.craft;
    }

    public void setCraft(Object[] craft) {
        this.craft = craft;
    }

    public int[] getCraftReference() {
        return this.craftReference;
    }

    public void setCraftReference(int[] craftReference) {
        this.craftReference = craftReference;
    }

    public Knowledge[] getKnow() {
        return this.know;
    }

    public void setKnow(Knowledge[] know) {
        this.know = know;
    }

    public String[] getResearch() {
        return this.research;
    }

    public void setResearch(String[] research) {
        this.research = research;
    }

    public String[] getResearchIcon() {
        return this.researchIcon;
    }

    public void setResearchIcon(String[] research) {
        this.researchIcon = research;
    }

    public int getWarp() {
        return this.warp;
    }

    public void setWarp(int warp) {
        this.warp = warp;
    }

    public static class Knowledge {
        public IPlayerKnowledge.EnumKnowledgeType type;
        public ResearchCategory category;
        public int amount;

        public Knowledge(IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int num) {
            this.type = type;
            this.category = category;
            this.amount = num;
        }

        public static Knowledge parse(String text) {
            String[] s = text.split(";");
            if (s.length == 2) {
                int num = 0;
                try {
                    num = Integer.parseInt(s[1]);
                } catch (Exception e) {
                }
                IPlayerKnowledge.EnumKnowledgeType t = IPlayerKnowledge.EnumKnowledgeType.valueOf(s[0].toUpperCase());
                if (t != null && !t.hasFields() && num > 0) {
                    return new Knowledge(t, null, num);
                }
            } else if (s.length == 3) {
                int num = 0;
                try {
                    num = Integer.parseInt(s[2]);
                } catch (Exception e) {
                }
                IPlayerKnowledge.EnumKnowledgeType t = IPlayerKnowledge.EnumKnowledgeType.valueOf(s[0].toUpperCase());
                ResearchCategory f = ResearchCategories.getResearchCategory(s[1].toUpperCase());
                if (t != null && f != null && num > 0) {
                    return new Knowledge(t, f, num);
                }
            }
            return null;
        }
    }
}
