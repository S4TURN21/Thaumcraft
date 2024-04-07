package thaumcraft.common.lib.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.items.resources.ItemCrystalEssence;

public class RecipeScribingTools extends CustomRecipe {
    public RecipeScribingTools(ResourceLocation pId) {
        super(pId);
    }

    @Override
    public boolean matches(CraftingContainer pContainer, Level pLevel) {
        boolean feather = false;
        boolean phial = false;
        boolean dye = false;
        for (int i = 0; i < pContainer.getContainerSize(); ++i) {
            if (!pContainer.getItem(i).isEmpty()) {
                ItemStack itemstack = pContainer.getItem(i);

                if (itemstack.isEmpty()) {
                    return false;
                }

                if (itemstack.getItem() == Items.FEATHER && feather) {
                    return false;
                }
                if (itemstack.getItem() == Items.FEATHER && !feather) {
                    feather = true;
                } else {
                    if (itemstack.getTags().anyMatch(t -> t == Tags.Items.DYES_BLACK) && dye) {
                        return false;
                    }
                    if (itemstack.getTags().anyMatch(t -> t == Tags.Items.DYES_BLACK) && !dye) {
                        dye = true;
                    } else {
                        if (itemstack.getItem() != ItemsTC.phial) {
                            return false;
                        }
                        ItemPhial item = (ItemPhial) itemstack.getItem();
                        if (item.getAspects(itemstack) == null) {
                            phial = true;
                        }
                    }
                }
            }
        }

        return feather && phial && dye;
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer) {
        return new ItemStack(ItemsTC.scribingTools);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation("thaumcraft", "scribing_tools"));
    }
}
