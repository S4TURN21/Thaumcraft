package thaumcraft.api.research;

import java.util.Arrays;

public class ResearchEntry {
    String key;
    String category;
    String[] parents;
    int displayColumn;
    int displayRow;
    Object[] icons;
    EnumResearchMeta[] meta;
    ResearchStage[] stages;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String[] getParents() {
        return this.parents;
    }

    public String[] getParentsStripped() {
        String[] out = null;
        if (this.parents != null) {
            out = new String[this.parents.length];
            for (int q = 0; q < out.length; ++q) {
                out[q] = "" + this.parents[q];
                if (out[q].startsWith("~")) {
                    out[q] = out[q].substring(1);
                }
            }
        }
        return out;
    }

    public void setParents(final String[] parents) {
        this.parents = parents;
    }

    public int getDisplayColumn() {
        return this.displayColumn;
    }

    public void setDisplayColumn(final int displayColumn) {
        this.displayColumn = displayColumn;
    }

    public int getDisplayRow() {
        return this.displayRow;
    }

    public void setDisplayRow(final int displayRow) {
        this.displayRow = displayRow;
    }

    public Object[] getIcons() {
        return this.icons;
    }

    public void setIcons(final Object[] icons) {
        this.icons = icons;
    }

    public boolean hasMeta(EnumResearchMeta me) {
        return this.meta != null && Arrays.asList(this.meta).contains(me);
    }

    public void setMeta(final EnumResearchMeta[] meta) {
        this.meta = meta;
    }

    public ResearchStage[] getStages() {
        return this.stages;
    }

    public void setStages(ResearchStage[] stages) {
        this.stages = stages;
    }

    public enum EnumResearchMeta {
        ROUND,
        SPIKY,
        REVERSE,
        HIDDEN,
        AUTOUNLOCK,
        HEX;
    }
}
