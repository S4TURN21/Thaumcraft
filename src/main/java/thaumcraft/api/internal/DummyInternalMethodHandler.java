package thaumcraft.api.internal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.research.ResearchCategory;

public class DummyInternalMethodHandler implements IInternalMethodHandler {
    @Override
    public boolean completeResearch(Player player, String researchkey) {
        return false;
    }

    @Override
    public boolean addKnowledge(Player player, IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int amount) {
        return false;
    }

    @Override
    public void addWarpToPlayer(Player player, int amount, IPlayerWarp.EnumWarpType type) {
    }

    @Override
    public AspectList getObjectAspects(ItemStack is) {
        return null;
    }

    @Override
    public void addFlux(Level world, BlockPos pos, float amount, boolean showEffect) {

    }

    @Override
    public boolean progressResearch(Player player, String researchkey) {
        return false;
    }
}
