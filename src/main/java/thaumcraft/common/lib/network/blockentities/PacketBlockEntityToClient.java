package thaumcraft.common.lib.network.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blockentities.BlockEntityThaumcraft;
import thaumcraft.common.lib.utils.Utils;

import java.util.function.Supplier;

public class PacketBlockEntityToClient {
    private long pos;
    private CompoundTag nbt;

    public PacketBlockEntityToClient(BlockPos pos, CompoundTag nbt) {
        this.pos = pos.asLong();
        this.nbt = nbt;
    }

    public PacketBlockEntityToClient(FriendlyByteBuf buffer) {
        pos = buffer.readLong();
        nbt = Utils.readNBTTagCompoundFromBuffer(buffer);
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeLong(pos);
        Utils.writeNBTTagCompoundToBuffer(buffer, nbt);
    }

    public static PacketBlockEntityToClient fromBytes(FriendlyByteBuf buffer) {
        return new PacketBlockEntityToClient(buffer);
    }

    public static void onMessage(PacketBlockEntityToClient message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world = Thaumcraft.proxy.getClientWorld();
            BlockPos bp = BlockPos.of(message.pos);
            if (world != null && bp != null) {
                BlockEntity te = world.getBlockEntity(bp);
                if (te != null && te instanceof BlockEntityThaumcraft) {
                    ((BlockEntityThaumcraft) te).messageFromServer((message.nbt == null) ? new CompoundTag() : message.nbt);
                }
            }
        });
    }
}
