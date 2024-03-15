package thaumcraft.common.blocks.tiles.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.client.gui.ContainerArcaneWorkbench;

public class BlockEntityArcaneWorkbench extends BlockEntity implements MenuProvider {

    public BlockEntityArcaneWorkbench(BlockPos pPos, BlockState pBlockState) {
        super(ForgeRegistries.BLOCK_ENTITY_TYPES.getValue(new ResourceLocation("thaumcraft", "arcane_workbench")), pPos, pBlockState);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ContainerArcaneWorkbench(pContainerId, pPlayerInventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.empty();
    }
}
