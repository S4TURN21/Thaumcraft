package thaumcraft.common.lib.network.fx;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import thaumcraft.client.fx.FXDispatcher;

import java.util.function.Supplier;

public class PacketFXPollute {
    private int x;
    private int y;
    private int z;
    private byte amount;

    public PacketFXPollute() {
    }

    public PacketFXPollute(FriendlyByteBuf buffer) {
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
        this.amount = buffer.readByte();
    }

    public PacketFXPollute(BlockPos pos, float amt) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        if (amt < 1.0f && amt > 0.0f) {
            amt = 1.0f;
        }
        this.amount = (byte) amt;
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeInt(this.z);
        buffer.writeInt(this.amount);
    }

    public static PacketFXPollute fromBytes(FriendlyByteBuf buffer) {
        return new PacketFXPollute(buffer);
    }

    public static void onMessage(PacketFXPollute message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            FXDispatcher.INSTANCE.drawPollutionParticles(new BlockPos(message.x, message.y, message.z));
        });
    }
}
