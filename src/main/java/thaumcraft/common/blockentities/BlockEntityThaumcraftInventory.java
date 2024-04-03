package thaumcraft.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BlockEntityThaumcraftInventory extends BlockEntityThaumcraft implements WorldlyContainer, Tickable {
    private NonNullList<ItemStack> stacks;
    protected int[] syncedSlots;
    private NonNullList<ItemStack> syncedStacks;
    protected String customName;
    private int[] faceSlots;
    boolean initial;

    public BlockEntityThaumcraftInventory(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int size) {
        super(pType, pPos, pBlockState);

        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        syncedSlots = new int[0];
        syncedStacks = NonNullList.withSize(size, ItemStack.EMPTY);
        initial = true;
        faceSlots = new int[size];
        for (int a = 0; a < size; ++a) {
            faceSlots[a] = a;
        }
    }

    @Override
    public int getContainerSize() {
        return stacks.size();
    }

    protected NonNullList<ItemStack> getItems() {
        return stacks;
    }

    public ItemStack getSyncedStackInSlot(int index) {
        return syncedStacks.get(index);
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return getItems().get(pSlot);
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return true;
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        ItemStack itemstack = ContainerHelper.removeItem(getItems(), pSlot, pAmount);
        if (!itemstack.isEmpty() && isSyncedSlot(pSlot)) {
            syncSlots(null);
        }
        setChanged();
        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        ItemStack s = ContainerHelper.takeItem(getItems(), pSlot);
        if (isSyncedSlot(pSlot)) {
            syncSlots(null);
        }
        setChanged();
        return s;
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        getItems().set(pSlot, pStack);
        if (pStack.getCount() > getInventoryStackLimit()) {
            pStack.setCount(getInventoryStackLimit());
        }
        setChanged();
        if (isSyncedSlot(pSlot)) {
            syncSlots(null);
        }
    }

    public boolean hasCustomName() {
        return customName != null && customName.length() > 0;
    }

    private boolean isSyncedSlot(int slot) {
        for (int s : syncedSlots) {
            if (s == slot) {
                return true;
            }
        }
        return false;
    }

    protected void syncSlots(ServerPlayer player) {
        if (syncedSlots.length > 0) {
            CompoundTag nbt = new CompoundTag();
            ListTag nbttaglist = new ListTag();
            for (int i = 0; i < stacks.size(); ++i) {
                if (!stacks.get(i).isEmpty() && isSyncedSlot(i)) {
                    CompoundTag nbttagcompound1 = stacks.get(i).serializeNBT();
                    nbttaglist.add(nbttagcompound1);
                }
            }
            nbt.put("ItemsSynced", nbttaglist);
            sendMessageToClient(nbt, player);
        }
    }

    @Override
    public void syncTile(boolean rerender) {
        super.syncTile(rerender);
        syncSlots(null);
    }

    @Override
    public void messageFromClient(CompoundTag nbt, ServerPlayer player) {
        super.messageFromClient(nbt, player);
        if (nbt.contains("requestSync")) {
            syncSlots(player);
        }
    }

    @Override
    public void messageFromServer(CompoundTag nbt) {
        super.messageFromServer(nbt);
        if (nbt.contains("ItemsSynced")) {
            syncedStacks = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
            ListTag nbttaglist = nbt.getList("ItemsSynced", 10);
            for (int i = 0; i < nbttaglist.size(); ++i) {
                CompoundTag nbttagcompound1 = nbttaglist.getCompound(i);
                byte b0 = nbttagcompound1.getByte("Slot");
                if (isSyncedSlot(b0)) {
                    syncedStacks.set(b0, ItemStack.of(nbttagcompound1));
                }
            }
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("CustomName")) {
            customName = pTag.getString("CustomName");
        }
        ContainerHelper.loadAllItems(pTag, stacks = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (hasCustomName()) {
            pTag.putString("CustomName", customName);
        }
        ContainerHelper.saveAllItems(pTag, stacks);
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return level.getBlockEntity(getBlockPos()) == this && pPlayer.distanceToSqr(Vec3.atCenterOf(getBlockPos())) <= 64.0;
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return faceSlots;
    }

    @Override
    public void startOpen(Player pPlayer) {

    }

    @Override
    public void stopOpen(Player pPlayer) {

    }

    @Override
    public void clearContent() {

    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return canPlaceItem(pIndex, pItemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : stacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void tick() {
        if (initial) {
            initial = false;
            if (!level.isClientSide) {
                syncSlots(null);
            } else {
                CompoundTag nbt = new CompoundTag();
                nbt.putBoolean("requestSync", true);
                sendMessageToServer(nbt);
            }
        }
    }
}
