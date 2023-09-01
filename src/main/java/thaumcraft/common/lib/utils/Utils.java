package thaumcraft.common.lib.utils;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;

import java.io.IOException;

public class Utils {
    public static void writeNBTTagCompoundToBuffer(FriendlyByteBuf bb, CompoundTag nbt) {
        bb.writeNbt(nbt);
    }

    public static CompoundTag readNBTTagCompoundFromBuffer(FriendlyByteBuf bb) {
        return bb.readNbt();
    }
}
