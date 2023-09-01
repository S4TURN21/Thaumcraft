package thaumcraft.common.lib.network.fx;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.utils.Utils;

import java.util.function.Supplier;

public class PacketFXBlockBamf {
    private double x;
    private double y;
    private double z;
    private int color;
    private byte flags;

    private byte face;

    public PacketFXBlockBamf() {
    }

    public PacketFXBlockBamf(FriendlyByteBuf buffer) {
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
        this.color = buffer.readInt();
        this.flags = buffer.readByte();
        this.face = buffer.readByte();
    }

    public PacketFXBlockBamf(double x, double y, double z, int color, boolean sound, boolean flair, Direction side) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        int f = 0;
        if (sound) {
            f = Utils.setBit(f, 0);
        }
        if (flair) {
            f = Utils.setBit(f, 1);
        }
        if (side != null) {
            this.face = (byte) side.ordinal();
        } else {
            this.face = -1;
        }
        this.flags = (byte) f;
    }

    public PacketFXBlockBamf(BlockPos pos, int color, boolean sound, boolean flair, Direction side) {
        this.x = pos.getX() + 0.5;
        this.y = pos.getY() + 0.5;
        this.z = pos.getZ() + 0.5;
        this.color = color;
        int f = 0;
        if (sound) {
            f = Utils.setBit(f, 0);
        }
        if (flair) {
            f = Utils.setBit(f, 1);
        }
        if (side != null) {
            this.face = (byte) side.ordinal();
        } else {
            this.face = -1;
        }
        this.flags = (byte) f;
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeDouble(this.x);
        buffer.writeDouble(this.y);
        buffer.writeDouble(this.z);
        buffer.writeInt(this.color);
        buffer.writeByte((int) this.flags);
        buffer.writeByte((int) this.face);
    }

    public static PacketFXBlockBamf fromBytes(FriendlyByteBuf buffer) {
        return new PacketFXBlockBamf(buffer);
    }


    @OnlyIn(Dist.CLIENT)
    public static void onMessage(PacketFXBlockBamf message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Direction side = null;
            if (message.face >= 0) {
                side = Direction.from3DDataValue((int) message.face);
            }
            if (message.color != -9999) {
                FXDispatcher.INSTANCE.drawBamf(message.x, message.y, message.z, message.color, Utils.getBit(message.flags, 0), Utils.getBit(message.flags, 1), side);
            } else {
                FXDispatcher.INSTANCE.drawBamf(message.x, message.y, message.z, Utils.getBit(message.flags, 0), Utils.getBit(message.flags, 1), side);
            }
        });
    }
}
