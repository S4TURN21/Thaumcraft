package thaumcraft.common.items.armor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.items.IGoggles;

public class ItemGoggles extends ArmorItem implements IGoggles {
    public ItemGoggles(Properties pProperties) {
        super(ThaumcraftMaterials.ARMORMAT_SPECIAL, EquipmentSlot.HEAD, pProperties);
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "thaumcraft:textures/models/armor/goggles.png";
    }

    @Override
    public boolean showIngamePopups(ItemStack itemstack, LivingEntity player) {
        return true;
    }
}