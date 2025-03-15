package thaumcraft.api;

import codechicken.lib.item.SimpleArmorMaterial;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class ThaumcraftMaterials {
    public static ArmorMaterial ARMORMAT_SPECIAL = new SimpleArmorMaterial(new int[]{13, 15, 16, 11}, new int[]{1, 2, 3, 1}, 25, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, "SPECIAL", 1.0F, 0.0f);
}