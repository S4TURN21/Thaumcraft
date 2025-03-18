package thaumcraft.common.container;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.AspectList;

public class CrucibleContainer extends SimpleContainer {
    public AspectList aspects;

    public CrucibleContainer(AspectList aspects, ItemStack cat) {
        super(1);
        this.aspects = aspects;
        this.setItem(0, cat);
    }
}