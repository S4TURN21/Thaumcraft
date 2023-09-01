package thaumcraft.common.lib.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.network.playerdata.PacketSyncKnowledge;

import java.util.Optional;

public class PacketHandler {
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Thaumcraft.MODID, "thaumcraft"), () -> "1.0", s -> true, s -> true);

    public static void preInit() {
        int idx = 0;
        PacketHandler.INSTANCE.registerMessage(idx++, PacketSyncKnowledge.class, PacketSyncKnowledge::toBytes, PacketSyncKnowledge::fromBytes, PacketSyncKnowledge::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
