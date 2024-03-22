package thaumcraft.api.internal;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerWarp;

public class DummyInternalMethodHandler implements IInternalMethodHandler {
    @Override
    public boolean completeResearch(Player player, String researchkey) {
        return false;
    }

    @Override
    public void addWarpToPlayer(Player player, int amount, IPlayerWarp.EnumWarpType type) {
    }

    @Override
    public AspectList getObjectAspects(ItemStack is) {
        return null;
    }
}
