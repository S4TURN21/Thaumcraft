package thaumcraft.common.lib;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class CreativeTabThaumcraft extends CreativeModeTab {
    public CreativeTabThaumcraft(String label) {
        super(label);
    }

    @Override
    public @NotNull ItemStack makeIcon() {
        return new ItemStack(Items.REDSTONE);
    }
}
