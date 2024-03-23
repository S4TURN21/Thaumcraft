package thaumcraft.api.internal;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.research.ResearchCategory;

public interface IInternalMethodHandler {
    boolean addKnowledge(Player player, IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int amount);

    void addWarpToPlayer(Player player, int amount, IPlayerWarp.EnumWarpType type);

    AspectList getObjectAspects(ItemStack is);

    boolean completeResearch(Player player, String res);
}
