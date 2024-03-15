package thaumcraft.api.crafting;

import net.minecraft.world.item.crafting.CraftingRecipe;
import thaumcraft.api.aspects.AspectList;

public interface IArcaneRecipe extends CraftingRecipe
{
    public int getVis();
    public String getResearch();
    public AspectList getCrystals();
}