package thaumcraft.common.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotLimitedByClass extends Slot {
    Class clazz;
    int limit;

    public SlotLimitedByClass(Class clazz, Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
        limit = 64;
        this.clazz = clazz;
    }

    public SlotLimitedByClass(Class clazz, int limit, Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
        this.clazz = clazz;
        this.limit = limit;
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return !pStack.isEmpty() && clazz.isAssignableFrom(pStack.getItem().getClass());
    }

    @Override
    public int getMaxStackSize() {
        return limit;
    }
}
