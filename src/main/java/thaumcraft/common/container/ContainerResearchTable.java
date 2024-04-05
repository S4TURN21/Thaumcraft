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
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.blockentities.crafting.BlockEntityResearchTable;
import thaumcraft.common.container.slot.SlotLimitedByClass;
import thaumcraft.common.container.slot.SlotLimitedByItemstack;
import thaumcraft.common.lib.utils.InventoryUtils;

import java.util.HashMap;

public class ContainerResearchTable extends AbstractContainerMenu {
    static HashMap<Integer, Long> antiSpam = new HashMap<>();
    public BlockEntityResearchTable blockEntity;
    Player player;

    public ContainerResearchTable(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, (BlockEntityResearchTable) inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public ContainerResearchTable(int id, Inventory inv, BlockEntityResearchTable entity) {
        super(ForgeRegistries.MENU_TYPES.getValue(new ResourceLocation(Thaumcraft.MODID, "research_table")), id);
        player = inv.player;
        blockEntity = entity;
        addSlot(new SlotLimitedByClass(IScribeTools.class, entity, 0, 16, 15));
        addSlot(new SlotLimitedByItemstack(new ItemStack(Items.PAPER), entity, 1, 224, 16));
        bindPlayerInventory(inv);
    }

    @Override
    public boolean clickMenuButton(Player pPlayer, int button) {
        if (button == 1) {
            if (blockEntity.data.lastDraw != null) {
                blockEntity.data.savedCards.add(blockEntity.data.lastDraw.card.getSeed());
            }
            for (ResearchTableData.CardChoice cc : blockEntity.data.cardChoices) {
                if (cc.selected) {
                    blockEntity.data.lastDraw = cc;
                    break;
                }
            }
            blockEntity.data.cardChoices.clear();
            blockEntity.syncTile(false);
            return true;
        }
        if (button == 4 || button == 5 || button == 6) {
            long tn = System.currentTimeMillis();
            long to = 0L;
            if (ContainerResearchTable.antiSpam.containsKey(pPlayer.getId())) {
                to = ContainerResearchTable.antiSpam.get(pPlayer.getId());
            }
            if (tn - to < 333L) {
                return false;
            }
            ContainerResearchTable.antiSpam.put(pPlayer.getId(), tn);
            try {
                TheorycraftCard card = blockEntity.data.cardChoices.get(button - 4).card;
                if (card.getRequiredItems() != null) {
                    for (ItemStack stack : card.getRequiredItems()) {
                        if (stack != null && !stack.isEmpty() && !InventoryUtils.isPlayerCarryingAmount(player, stack, true)) {
                            return false;
                        }
                    }
                    if (card.getRequiredItemsConsumed() != null && card.getRequiredItemsConsumed().length == card.getRequiredItems().length) {
                        for (int a = 0; a < card.getRequiredItems().length; ++a) {
                            if (card.getRequiredItemsConsumed()[a] && card.getRequiredItems()[a] != null && !card.getRequiredItems()[a].isEmpty()) {
                                InventoryUtils.consumePlayerItem(player, card.getRequiredItems()[a], true, true);
                            }
                        }
                    }
                }
                if (card.activate(pPlayer, blockEntity.data)) {
                    blockEntity.consumeInkFromTable();
                    blockEntity.data.cardChoices.get(button - 4).selected = true;
                    blockEntity.data.addInspiration(-card.getInspirationCost());
                    blockEntity.syncTile(false);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (button == 7 && blockEntity.data.isComplete()) {
//            blockEntity.finishTheory(pPlayer);
            blockEntity.syncTile(false);
            return true;
        }
        if (button == 9 && !blockEntity.data.isComplete()) {
            blockEntity.data = null;
            blockEntity.syncTile(false);
            return true;
        }
        if (button == 2 || button == 3) {
            if (blockEntity.data != null && !blockEntity.data.isComplete() && blockEntity.consumepaperFromTable()) {
                blockEntity.data.drawCards(button, pPlayer);
                blockEntity.syncTile(false);
            }
            return true;
        }
        return false;
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
            } else if (!blockEntity.isItemValidForSlot(slot, stackInSlot) || !moveItemStackTo(stackInSlot, 0, 2, false)) {
                return ItemStack.EMPTY;
            }
            if (stackInSlot.getCount() == 0) {
                slotObject.set(ItemStack.EMPTY);
            } else {
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
