package thaumcraft.common.lib.network.playerdata;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.utils.Utils;

import java.util.function.Supplier;

public class PacketSyncKnowledge {

    protected CompoundTag data;

    public PacketSyncKnowledge() {
    }

    public PacketSyncKnowledge(FriendlyByteBuf buffer) {
        this.data = Utils.readNBTTagCompoundFromBuffer(buffer);
    }

    public void toBytes(FriendlyByteBuf buffer) {
        Utils.writeNBTTagCompoundToBuffer(buffer, this.data);
    }

    public static PacketSyncKnowledge fromBytes(FriendlyByteBuf buffer) {
        return new PacketSyncKnowledge(buffer);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onMessage(PacketSyncKnowledge message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            IPlayerKnowledge pk = ThaumcraftCapabilities.getKnowledge(player);
            pk.deserializeNBT(message.data);
        });
    }
}
