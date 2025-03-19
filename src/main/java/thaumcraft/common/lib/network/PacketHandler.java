package thaumcraft.common.lib.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.network.blockentities.PacketBlockEntityToClient;
import thaumcraft.common.lib.network.blockentities.PacketBlockEntityToServer;
import thaumcraft.common.lib.network.fx.PacketFXBlockBamf;
import thaumcraft.common.lib.network.fx.PacketFXPollute;
import thaumcraft.common.lib.network.misc.PacketAuraToClient;
import thaumcraft.common.lib.network.misc.PacketKnowledgeGain;
import thaumcraft.common.lib.network.misc.PacketStartTheoryToServer;
import thaumcraft.common.lib.network.playerdata.PacketSyncKnowledge;
import thaumcraft.common.lib.network.playerdata.PacketSyncProgressToServer;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;
import thaumcraft.common.lib.network.playerdata.PacketWarpMessage;

import java.util.Optional;

public class PacketHandler {
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Thaumcraft.MODID, "messages"), () -> "1.0", s -> true, s -> true);

    public static void preInit() {
        int idx = 0;
        PacketHandler.INSTANCE.registerMessage(idx++, PacketKnowledgeGain.class, PacketKnowledgeGain::toBytes, PacketKnowledgeGain::fromBytes, PacketKnowledgeGain::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketStartTheoryToServer.class, PacketStartTheoryToServer::toBytes, PacketStartTheoryToServer::fromBytes, PacketStartTheoryToServer::onMessage, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketBlockEntityToServer.class, PacketBlockEntityToServer::toBytes, PacketBlockEntityToServer::fromBytes, PacketBlockEntityToServer::onMessage, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketBlockEntityToClient.class, PacketBlockEntityToClient::toBytes, PacketBlockEntityToClient::fromBytes, PacketBlockEntityToClient::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketAuraToClient.class, PacketAuraToClient::toBytes, PacketAuraToClient::fromBytes, PacketAuraToClient::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketSyncWarp.class, PacketSyncWarp::toBytes, PacketSyncWarp::fromBytes, PacketSyncWarp::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketSyncKnowledge.class, PacketSyncKnowledge::toBytes, PacketSyncKnowledge::fromBytes, PacketSyncKnowledge::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketWarpMessage.class, PacketWarpMessage::toBytes, PacketWarpMessage::fromBytes, PacketWarpMessage::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketSyncProgressToServer.class, PacketSyncProgressToServer::toBytes, PacketSyncProgressToServer::fromBytes, PacketSyncProgressToServer::onMessage, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketFXPollute.class, PacketFXPollute::toBytes, PacketFXPollute::fromBytes, PacketFXPollute::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketFXBlockBamf.class, PacketFXBlockBamf::toBytes, PacketFXBlockBamf::fromBytes, PacketFXBlockBamf::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
