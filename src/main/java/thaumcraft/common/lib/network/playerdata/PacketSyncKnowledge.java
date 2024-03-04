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
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.client.gui.ResearchToast;
import thaumcraft.common.lib.utils.Utils;

import java.util.function.Supplier;

public class PacketSyncKnowledge {

    protected CompoundTag data;

    public PacketSyncKnowledge(Player player) {
        IPlayerKnowledge pk = ThaumcraftCapabilities.getKnowledge(player);
        this.data = pk.serializeNBT();
        for (String key : pk.getResearchList()) {
            pk.clearResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP);
        }
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
            for (String key : pk.getResearchList()) {
                if (pk.hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP)) {
                    ResearchEntry ri = ResearchCategories.getResearch(key);
                    if (ri != null) {
                        Minecraft.getInstance().getToasts().addToast(new ResearchToast(ri));
                    }
                }
                pk.clearResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
