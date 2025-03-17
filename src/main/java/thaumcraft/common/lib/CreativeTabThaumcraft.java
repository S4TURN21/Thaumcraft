package thaumcraft.common.lib;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.items.ItemsTC;

public class CreativeTabThaumcraft extends CreativeModeTab {
    public CreativeTabThaumcraft(String label) {
        super(label);
    }

    @Override
    public @NotNull ItemStack makeIcon() {
        return new ItemStack(ItemsTC.goggles);
    }
}
