package thaumcraft.common.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.resources.ItemCrystalEssence;

public class SlotCrystal extends Slot {
    private Aspect aspect;

    public SlotCrystal(Aspect aspect, Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
        this.aspect = aspect;
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return isValidCrystal(pStack, aspect);
    }

    public static boolean isValidCrystal(ItemStack stack, Aspect aspect) {
        return stack != null && !stack.isEmpty() && stack.getItem() != null && stack.getItem() instanceof ItemCrystalEssence && ((ItemCrystalEssence) stack.getItem()).getAspects(stack).getAspects()[0] == aspect;
    }
}
