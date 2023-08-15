package thaumcraft.common.lib.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledge;


//TODO playerJoin sync knowledge
//TODO cloneCapabilitiesEvent
@Mod.EventBusSubscriber(modid = Thaumcraft.MODID)
public class PlayerEvents {

    @SubscribeEvent
    public static void attachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(PlayerKnowledge.Provider.NAME, new PlayerKnowledge.Provider());
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IPlayerKnowledge.class);
    }
}
