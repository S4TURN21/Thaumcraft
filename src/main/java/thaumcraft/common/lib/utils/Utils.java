package thaumcraft.common.lib.utils;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;

import java.io.IOException;

public class Utils {
    public static void writeNBTTagCompoundToBuffer(FriendlyByteBuf bb, CompoundTag nbt) {
        if (nbt == null) {
            bb.writeByte(0);
        } else {
            try {
                nbt.write(new ByteBufOutputStream(bb));
            } catch (IOException ioexception) {
                throw new EncoderException(ioexception);
            }
        }
    }

    public static CompoundTag readNBTTagCompoundFromBuffer(FriendlyByteBuf bb) {
        int i = bb.readerIndex();
        byte b0 = bb.readByte();
        if (b0 == 0) {
            return null;
        }
        bb.readerIndex(i);
        return bb.readNbt(new NbtAccounter(0x200000L));
    }
}
