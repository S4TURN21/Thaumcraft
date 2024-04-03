package thaumcraft.common.blockentities.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.common.blockentities.BlockEntityThaumcraftInventory;
import thaumcraft.common.container.ContainerResearchTable;

public class BlockEntityResearchTable extends BlockEntityThaumcraftInventory implements MenuProvider {
    public ResearchTableData data;

    public BlockEntityResearchTable(BlockPos pPos, BlockState pBlockState) {
        super(ForgeRegistries.BLOCK_ENTITY_TYPES.getValue(new ResourceLocation("thaumcraft", "research_table")), pPos, pBlockState, 2);
        data = null;
        syncedSlots = new int[]{0, 1};
    }

    @Override
    public void readSyncNBT(CompoundTag nbt) {
        super.readSyncNBT(nbt);
        if (nbt.contains("note")) {
            (data = new ResearchTableData(this)).deserialize(nbt.getCompound("note"));
        } else {
            data = null;
        }
    }

    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        if (data != null) {
            nbttagcompound.put("note", data.serialize());
        } else {
            nbttagcompound.remove("note");
        }
        return super.writeSyncNBT(nbttagcompound);
    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ContainerResearchTable(pContainerId, pPlayerInventory, this);
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        switch (i) {
            case 0: {
                if (itemstack.getItem() instanceof IScribeTools) {
                    return true;
                }
                break;
            }
            case 1: {
                if (itemstack.getItem() == Items.PAPER) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        if (level != null && level.isClientSide) {
            syncTile(false);
        }
    }
}
