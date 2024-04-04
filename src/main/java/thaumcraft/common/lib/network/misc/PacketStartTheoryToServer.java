package thaumcraft.common.lib.network.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import thaumcraft.common.blockentities.crafting.BlockEntityResearchTable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class PacketStartTheoryToServer {
    private long pos;
    private Set<String> aids;

    public PacketStartTheoryToServer() {
        aids = new HashSet<String>();
    }

    public PacketStartTheoryToServer(BlockPos pos, Set<String> aids) {
        this.aids = new HashSet<>();
        this.pos = pos.asLong();
        this.aids = aids;
    }

    public PacketStartTheoryToServer(FriendlyByteBuf buffer) {
        pos = buffer.readLong();
        for (int s = buffer.readByte(), a = 0; a < s; ++a) {
            aids.add(buffer.readUtf());
        }
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeLong(pos);
        buffer.writeByte(aids.size());
        for (String aid : aids) {
            buffer.writeUtf(aid);
        }
    }

    public static PacketStartTheoryToServer fromBytes(FriendlyByteBuf buffer) {
        return new PacketStartTheoryToServer(buffer);
    }

    public static void onMessage(PacketStartTheoryToServer message, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            Level world = context.getSender().level;
            Entity player = context.getSender();
            BlockPos bp = BlockPos.of(message.pos);
            if (world != null && player != null && player instanceof Player && bp != null) {
                BlockEntity te = world.getBlockEntity(bp);
                if (te != null && te instanceof BlockEntityResearchTable) {
                    ((BlockEntityResearchTable) te).startNewTheory((Player) player, message.aids);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
