package thaumcraft.common.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import thaumcraft.Thaumcraft;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.common.blockentities.crafting.BlockEntityResearchTable;
import thaumcraft.common.container.slot.SlotLimitedByClass;
import thaumcraft.common.container.slot.SlotLimitedByItemstack;

public class ContainerResearchTable extends AbstractContainerMenu {
    public BlockEntityResearchTable blockEntity;

    public ContainerResearchTable(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, (BlockEntityResearchTable) inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public ContainerResearchTable(int id, Inventory inv, BlockEntityResearchTable entity) {
        super(ForgeRegistries.MENU_TYPES.getValue(new ResourceLocation(Thaumcraft.MODID, "research_table")), id);
        blockEntity = entity;
        addSlot(new SlotLimitedByClass(IScribeTools.class, entity, 0, 16, 15));
        addSlot(new SlotLimitedByItemstack(new ItemStack(Items.PAPER), entity, 1, 224, 16));
        bindPlayerInventory(inv);
    }

    protected void bindPlayerInventory(Inventory inventoryPlayer) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 77 + j * 18, 190 + i * 18));
            }
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                addSlot(new Slot(inventoryPlayer, i + j * 3, 20 + i * 18, 190 + j * 18));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = slots.get(slot);
        if (slotObject != null && slotObject.hasItem()) {
            ItemStack stackInSlot = slotObject.getItem();
            stack = stackInSlot.copy();
            if (slot < 2) {
                if (!blockEntity.isItemValidForSlot(slot, stackInSlot) || !moveItemStackTo(stackInSlot, 2, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!blockEntity.isItemValidForSlot(slot, stackInSlot) || !moveItemStackTo(stackInSlot, 0, 2, false)) {
                return ItemStack.EMPTY;
            }
            if (stackInSlot.getCount() == 0) {
                slotObject.set(ItemStack.EMPTY);
            }
            else {
                slotObject.setChanged();
            }
        }
        return stack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return blockEntity.stillValid(pPlayer);
    }
}
