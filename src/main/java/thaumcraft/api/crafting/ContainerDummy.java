package thaumcraft.api.crafting;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerDummy extends AbstractContainerMenu {

    public ContainerDummy() {
        super(null, -1);
    }

    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int p_218265_) {
        return ItemStack.EMPTY;
    }

    public boolean stillValid(@NotNull Player player) {
        return false;
    }
}
