package thaumcraft.api;

import codechicken.lib.item.SimpleArmorMaterial;
import codechicken.lib.item.SimpleItemTier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.items.ItemsTC;

public class ThaumcraftMaterials {
    public static Tier TOOLMAT_THAUMIUM = SimpleItemTier.builder().harvestLevel(3).maxUses(500).efficiency(7F).attackDamage(2.5f).enchantability(22).repairMaterial(() -> Ingredient.of(ItemsTC.thaumiumIngot)).build();
    public static ArmorMaterial ARMORMAT_THAUMIUM = SimpleArmorMaterial.builder().textureName("THAUMIUM").durabilityFactor(25).damageReduction(new int[]{2, 5, 6, 2}).enchantability(25).soundEvent(SoundEvents.ARMOR_EQUIP_IRON).toughness(1.0F).repairMaterial(() -> Ingredient.of(ItemsTC.thaumiumIngot)).build();
    public static ArmorMaterial ARMORMAT_SPECIAL = SimpleArmorMaterial.builder().textureName("SPECIAL").durabilityFactor(25).damageReduction(new int[]{1, 2, 3, 1}).enchantability(25).soundEvent(SoundEvents.ARMOR_EQUIP_LEATHER).toughness(1.0F).repairMaterial(() -> Ingredient.EMPTY).build();
}