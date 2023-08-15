package thaumcraft.api.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.jetbrains.annotations.NotNull;

public class ThaumcraftCapabilities {
    public static final Capability<IPlayerKnowledge> KNOWLEDGE = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static IPlayerKnowledge getKnowledge(@NotNull Player player) {
        return player.getCapability(ThaumcraftCapabilities.KNOWLEDGE, null).resolve().get();
    }
}
