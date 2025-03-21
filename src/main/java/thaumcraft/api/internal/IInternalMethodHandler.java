package thaumcraft.api.internal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.research.ResearchCategory;

public interface IInternalMethodHandler {
    boolean addKnowledge(Player player, IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int amount);

    boolean progressResearch(Player player, String researchkey);

    void addWarpToPlayer(Player player, int amount, IPlayerWarp.EnumWarpType type);

    AspectList getObjectAspects(ItemStack is);

    AspectList generateTags(ItemStack is);

    void addFlux(Level world, BlockPos pos, float amount, boolean showEffect);

    boolean completeResearch(Player player, String res);
}
