package thaumcraft.client.gui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import thaumcraft.Thaumcraft;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.blocks.tiles.crafting.BlockEntityArcaneWorkbench;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.container.InventoryArcaneWorkbench;
import thaumcraft.common.container.slot.SlotCraftingArcaneWorkbench;
import thaumcraft.common.container.slot.SlotCrystal;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

import java.util.Optional;

public class ContainerArcaneWorkbench extends AbstractContainerMenu {
    public static int[] xx = new int[]{64, 17, 112, 17, 112, 64};
    public static int[] yy = new int[]{13, 35, 35, 93, 93, 115};
    private final Inventory ip;
    public ResultContainer craftResult;
    public final BlockEntityArcaneWorkbench blockEntity;
    private final Level level;
    private CraftingContainer inventoryCraft;

    public ContainerArcaneWorkbench(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public ContainerArcaneWorkbench(int id, Inventory inv, BlockEntity entity) {
        super(ForgeRegistries.MENU_TYPES.getValue(new ResourceLocation(Thaumcraft.MODID, "arcane_workbench")), id);
        craftResult = new ResultContainer();
        blockEntity = (BlockEntityArcaneWorkbench) entity;
        this.level = inv.player.level;
        this.ip = inv;
        this.inventoryCraft = new InventoryArcaneWorkbench(blockEntity,this);
        this.addSlot(new SlotCraftingArcaneWorkbench(blockEntity, inv.player, inventoryCraft, craftResult, 15, 160, 64));
        for (int i = 0; i < 3; ++i) {
            for (int k = 0; k < 3; ++k) {
                this.addSlot(new Slot(inventoryCraft, k + i * 3, 40 + k * 24, 40 + i * 24));
            }
        }
        for (ShardType st : ShardType.values()) {
            if (st.getMetadata() < 6) {
                this.addSlot(new SlotCrystal(st.getAspect(), inventoryCraft, st.getMetadata() + 9, ContainerArcaneWorkbench.xx[st.getMetadata()], ContainerArcaneWorkbench.yy[st.getMetadata()]));
            }
        }
        for (int i = 0; i < 3; ++i) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inv, k + i * 9 + 9, 16 + k * 18, 151 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inv, i, 16 + i * 18, 209));
        }
        slotsChanged(inventoryCraft);
    }

    public void slotsChanged(Container pInventory) {
        IArcaneRecipe recipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(inventoryCraft, ip.player);
        boolean hasVis = true;
        boolean hasCrystals = true;

        if (hasVis && hasCrystals) {
            slotChangedCraftingGrid(blockEntity.getLevel(), ip.player, inventoryCraft, craftResult);
        }
        super.broadcastChanges();
    }

    protected void slotChangedCraftingGrid(Level level, Player player, CraftingContainer craftMat, ResultContainer craftRes) {
        if (!level.isClientSide) {
            ServerPlayer entityplayermp = (ServerPlayer) player;
            ItemStack itemstack = ItemStack.EMPTY;
            IArcaneRecipe arecipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(craftMat, entityplayermp);
            if (arecipe != null) {
                if (craftRes.setRecipeUsed(level, entityplayermp, arecipe)) {
                    itemstack = arecipe.assemble(craftMat);
                }
            } else {
                CraftingContainer craftInv = new CraftingContainer(new ContainerDummy(), 3, 3);
                for (int a = 0; a < 9; ++a) {
                    craftInv.setItem(a, craftMat.getItem(a));
                }
                Optional<CraftingRecipe> irecipe = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftInv, level);
                if (irecipe.isPresent()) {
                    CraftingRecipe craftingrecipe = irecipe.get();
                    if (craftRes.setRecipeUsed(level, entityplayermp, craftingrecipe)) {
                        itemstack = craftingrecipe.assemble(craftInv);
                    }
                }
            }
            craftRes.setItem(0, itemstack);
            entityplayermp.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, itemstack));
        }
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        if (!blockEntity.getLevel().isClientSide) {
            this.clearContainer(pPlayer, inventoryCraft);
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return blockEntity.getLevel().getBlockEntity(blockEntity.getBlockPos()) == blockEntity && pPlayer.distanceToSqr(Vec3.atCenterOf(blockEntity.getBlockPos())) <= 64.0;
    }

    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex == 0) {
                if (!moveItemStackTo(itemstack1, 16, 52, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (pIndex >= 16 && pIndex < 52) {
                for (ShardType st : ShardType.values()) {
                    if (st.getMetadata() < 6) {
                        if (SlotCrystal.isValidCrystal(itemstack1, st.getAspect())) {
                            if (!moveItemStackTo(itemstack1, 10 + st.getMetadata(), 11 + st.getMetadata(), false)) {
                                return ItemStack.EMPTY;
                            }
                            if (itemstack1.getCount() == 0) {
                                break;
                            }
                        }
                    }
                }
                if (itemstack1.getCount() != 0) {
                    if (pIndex >= 16 && pIndex < 43) {
                        if (!moveItemStackTo(itemstack1, 43, 52, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (pIndex >= 43 && pIndex < 52 && !moveItemStackTo(itemstack1, 16, 43, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!moveItemStackTo(itemstack1, 16, 52, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(ip.player, itemstack1);
        }
        return itemstack;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        return pSlot.container != craftResult && super.canTakeItemForPickAll(pStack, pSlot);
    }
}
