package thaumcraft.api.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public interface IPlayerWarp extends INBTSerializable<CompoundTag> {
    void clear();

    int get(@Nonnull EnumWarpType type);

    void set(@Nonnull EnumWarpType type, int amount);

    int add(@Nonnull EnumWarpType type, int amount);

    int reduce(@Nonnull EnumWarpType type, int amount);

    void sync(ServerPlayer player);

    int getCounter();

    void setCounter(int amount);

    public enum EnumWarpType {
        PERMANENT,
        NORMAL,
        TEMPORARY;
    }
}
