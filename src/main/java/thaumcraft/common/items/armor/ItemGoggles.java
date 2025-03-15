package thaumcraft.common.items.armor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.ThaumcraftMaterials;

public class ItemGoggles extends ArmorItem {
    public ItemGoggles(Properties pProperties) {
        super(ThaumcraftMaterials.ARMORMAT_SPECIAL, EquipmentSlot.HEAD, pProperties);
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "thaumcraft:textures/models/armor/goggles.png";
    }
}