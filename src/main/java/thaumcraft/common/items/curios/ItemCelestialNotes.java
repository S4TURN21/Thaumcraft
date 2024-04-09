package thaumcraft.common.items.curios;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.items.ItemTCBase;

import java.util.List;

public class ItemCelestialNotes extends ItemTCBase {
    public ItemCelestialNotes(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        try {
            pTooltipComponents.add(Component.translatable(getDescriptionId(pStack) + ".text").withStyle(ChatFormatting.AQUA));
        } catch (Exception ignored) {
        }
    }

    @Override
    public Component getName(ItemStack pStack) {
        return Component.translatable("item.thaumcraft.celestial_notes");
    }
}
