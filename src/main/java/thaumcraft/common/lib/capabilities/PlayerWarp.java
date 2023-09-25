package thaumcraft.common.lib.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;

import javax.annotation.Nonnull;

public class PlayerWarp {
    private static class DefaultImpl implements IPlayerWarp {
        private int[] warp;
        private int counter;

        private DefaultImpl() {
            this.warp = new int[EnumWarpType.values().length];
        }

        @Override
        public void clear() {
            this.warp = new int[EnumWarpType.values().length];
            this.counter = 0;
        }

        @Override
        public int get(@Nonnull EnumWarpType type) {
            return this.warp[type.ordinal()];
        }

        @Override
        public void set(final EnumWarpType type, final int amount) {
            this.warp[type.ordinal()] = Mth.clamp(amount, 0, 500);
        }

        @Override
        public int add(@Nonnull EnumWarpType type, int amount) {
            return this.warp[type.ordinal()] = Mth.clamp(this.warp[type.ordinal()] + amount, 0, 500);
        }

        @Override
        public int reduce(@Nonnull EnumWarpType type, int amount) {
            return this.warp[type.ordinal()] = Mth.clamp(this.warp[type.ordinal()] - amount, 0, 500);
        }

        @Override
        public void sync(@Nonnull ServerPlayer player) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketSyncWarp(player));
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag properties = new CompoundTag();
            properties.putIntArray("warp", this.warp);
            properties.putInt("counter", this.getCounter());
            return properties;
        }

        @Override
        public void deserializeNBT(CompoundTag properties) {
            if (properties == null) {
                return;
            }
            this.clear();
            int[] ba = properties.getIntArray("warp");
            if (ba != null) {
                int l = EnumWarpType.values().length;
                if (ba.length < l) {
                    l = ba.length;
                }
                for (int a = 0; a < l; ++a) {
                    this.warp[a] = ba[a];
                }
            }
            this.setCounter(properties.getInt("counter"));
        }

        @Override
        public int getCounter() {
            return this.counter;
        }

        @Override
        public void setCounter(int amount) {
            this.counter = amount;
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        public static final ResourceLocation NAME = new ResourceLocation("thaumcraft", "warp");
        private PlayerWarp.DefaultImpl warp;
        private final LazyOptional<PlayerWarp.DefaultImpl> optional = LazyOptional.of(this::createWarp);

        public Provider() {
            this.warp = new DefaultImpl();
        }

        private DefaultImpl createWarp() {
            if (this.warp == null) {
                this.warp = new DefaultImpl();
            }

            return this.warp;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
            if (capability == ThaumcraftCapabilities.WARP) {
                return optional.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.warp.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.warp.deserializeNBT(nbt);
        }
    }
}
