package thaumcraft.common.lib.network.playerdata;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.utils.Utils;

import java.util.function.Supplier;

public class PacketSyncWarp {
    protected CompoundTag data;

    public PacketSyncWarp() {
    }

    public PacketSyncWarp(ServerPlayer player) {
        IPlayerWarp pk = ThaumcraftCapabilities.getWarp(player);
        this.data = pk.serializeNBT();
    }

    public PacketSyncWarp(FriendlyByteBuf buffer) {
        this.data = Utils.readNBTTagCompoundFromBuffer(buffer);
    }

    public void toBytes(FriendlyByteBuf buffer) {
        Utils.writeNBTTagCompoundToBuffer(buffer, this.data);
    }

    public static PacketSyncWarp fromBytes(FriendlyByteBuf buffer) {
        return new PacketSyncWarp(buffer);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onMessage(PacketSyncWarp message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            IPlayerWarp pk = ThaumcraftCapabilities.getWarp(player);
            pk.deserializeNBT(message.data);
        });
    }
}
