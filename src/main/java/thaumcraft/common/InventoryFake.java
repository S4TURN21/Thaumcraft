package thaumcraft.common;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class InventoryFake extends SimpleContainer {
    public InventoryFake(int size) {
        super(size);
    }

    public InventoryFake(ItemStack... stacks) {
        super(stacks.length);
    }
}
