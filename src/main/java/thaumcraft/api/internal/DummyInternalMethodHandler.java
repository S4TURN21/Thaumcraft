package thaumcraft.api.internal;

import net.minecraft.world.entity.player.Player;
import thaumcraft.api.capabilities.IPlayerWarp;

public class DummyInternalMethodHandler implements IInternalMethodHandler {
    @Override
    public boolean completeResearch(Player player, String researchkey) {
        return false;
    }

    @Override
    public void addWarpToPlayer(Player player, int amount, IPlayerWarp.EnumWarpType type) {
    }
}
