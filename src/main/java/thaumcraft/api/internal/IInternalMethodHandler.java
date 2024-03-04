package thaumcraft.api.internal;

import net.minecraft.world.entity.player.Player;
import thaumcraft.api.capabilities.IPlayerWarp;

public interface IInternalMethodHandler {
    void addWarpToPlayer(Player player, int amount, IPlayerWarp.EnumWarpType type);

    boolean completeResearch(Player player, String res);
}
