package thaumcraft.common.items.armor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.items.ItemsTC;

import java.util.Objects;

public class ItemThaumiumArmor extends ArmorItem {
    public ItemThaumiumArmor(ArmorMaterial pMaterial, EquipmentSlot pSlot, Properties pProperties) {
        super(pMaterial, pSlot, pProperties);
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (stack.getItem() == ItemsTC.thaumiumHelm || stack.getItem() == ItemsTC.thaumiumChest || stack.getItem() == ItemsTC.thaumiumBoots) {
            return "thaumcraft:textures/models/armor/thaumium_1.png";
        }
        if (stack.getItem() == ItemsTC.thaumiumLegs) {
            return "thaumcraft:textures/models/armor/thaumium_2.png";
        }
        return "thaumcraft:textures/models/armor/thaumium_1.png";
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.is(ItemsTC.thaumiumIngot) || super.isValidRepairItem(pToRepair, pRepair);
    }
}
