package thaumcraft.common.container;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.level.block.entity.BlockEntity;

public class InventoryArcaneWorkbench extends CraftingContainer {
    BlockEntity workbench;

    public InventoryArcaneWorkbench(BlockEntity blockEntity, AbstractContainerMenu pMenu) {
        super(pMenu, 5, 3);
        workbench = blockEntity;
    }
}
