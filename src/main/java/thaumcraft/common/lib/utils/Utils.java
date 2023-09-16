package thaumcraft.common.lib.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class Utils {
    public static boolean getBit(int value, int bit) {
        return (value & 1 << bit) != 0x0;
    }

    public static int setBit(int value, int bit) {
        return value | 1 << bit;
    }

    public static void writeNBTTagCompoundToBuffer(FriendlyByteBuf bb, CompoundTag nbt) {
        bb.writeNbt(nbt);
    }

    public static CompoundTag readNBTTagCompoundFromBuffer(FriendlyByteBuf bb) {
        return bb.readNbt();
    }
}
