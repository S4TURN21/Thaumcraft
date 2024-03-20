package thaumcraft.common.blocks.tiles.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.client.gui.ContainerArcaneWorkbench;
import thaumcraft.common.container.InventoryArcaneWorkbench;
import thaumcraft.common.world.aura.AuraHandler;

public class BlockEntityArcaneWorkbench extends BlockEntity implements MenuProvider {
    public InventoryArcaneWorkbench inventoryCraft;
    private int auraVisServer;
    private int auraVisClient;
    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> BlockEntityArcaneWorkbench.this.auraVisClient;
                case 1 -> BlockEntityArcaneWorkbench.this.auraVisServer;
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0 -> BlockEntityArcaneWorkbench.this.auraVisClient = pValue;
                case 1 -> BlockEntityArcaneWorkbench.this.auraVisServer = pValue;
            }
            ;
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public BlockEntityArcaneWorkbench(BlockPos pPos, BlockState pBlockState) {
        super(ForgeRegistries.BLOCK_ENTITY_TYPES.getValue(new ResourceLocation("thaumcraft", "arcane_workbench")), pPos, pBlockState);
        auraVisServer = 0;
        auraVisClient = 0;
        inventoryCraft = new InventoryArcaneWorkbench(this, new ContainerDummy());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        NonNullList<ItemStack> stacks = NonNullList.withSize(inventoryCraft.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, stacks);
        for (int a = 0; a < stacks.size(); ++a) {
            inventoryCraft.setItem(a, stacks.get(a));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        NonNullList<ItemStack> stacks = NonNullList.withSize(inventoryCraft.getContainerSize(), ItemStack.EMPTY);
        for (int a = 0; a < stacks.size(); ++a) {
            stacks.set(a, inventoryCraft.getItem(a));
        }
        ContainerHelper.saveAllItems(pTag, stacks);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ContainerArcaneWorkbench(pContainerId, pPlayerInventory, this, this.dataAccess);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.empty();
    }

    public void getAura() {
        if (!getLevel().isClientSide) {
            int t = 0;
            t = (int) AuraHandler.getVis(getLevel(), getBlockPos());
            auraVisServer = t;
        }
    }

    public void spendAura(int vis) {
        if (!getLevel().isClientSide) {
            AuraHandler.drainVis(getLevel(), getBlockPos(), (float) vis, false);
        }
    }
}
