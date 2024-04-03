package thaumcraft.common.lib.network.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import thaumcraft.common.blockentities.BlockEntityThaumcraft;
import thaumcraft.common.lib.utils.Utils;

import java.util.function.Supplier;

public class PacketBlockEntityToServer {
    private long pos;
    private CompoundTag nbt;

    public PacketBlockEntityToServer() {
    }

    public PacketBlockEntityToServer(BlockPos pos, CompoundTag nbt) {
        this.pos = pos.asLong();
        this.nbt = nbt;
    }

    public PacketBlockEntityToServer(FriendlyByteBuf buffer) {
        pos = buffer.readLong();
        nbt = Utils.readNBTTagCompoundFromBuffer(buffer);
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeLong(pos);
        Utils.writeNBTTagCompoundToBuffer(buffer, nbt);
    }

    public static PacketBlockEntityToServer fromBytes(FriendlyByteBuf buffer) {
        return new PacketBlockEntityToServer(buffer);
    }

    public static void onMessage(PacketBlockEntityToServer message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world = ctx.get().getSender().level;
            BlockPos bp = BlockPos.of(message.pos);
            if (world != null && bp != null) {
                BlockEntity te = world.getBlockEntity(bp);
                if (te != null && te instanceof BlockEntityThaumcraft) {
                    ((BlockEntityThaumcraft) te).messageFromClient((message.nbt == null) ? new CompoundTag() : message.nbt, ctx.get().getSender());
                }
            }
        });
    }
}
