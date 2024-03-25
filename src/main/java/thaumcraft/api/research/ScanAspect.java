package thaumcraft.api.research;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;

public class ScanAspect implements IScanThing {
    String research;
    Aspect aspect;

    public ScanAspect(String research, Aspect aspect) {
        this.research = research;
        this.aspect = aspect;
    }

    @Override
    public boolean checkThing(Player player, Object obj) {
        if (obj == null) return false;

        AspectList al = null;

        ItemStack is = null;
        if (obj instanceof ItemStack)
            is = (ItemStack) obj;
        if (obj instanceof ItemEntity && ((ItemEntity) obj).getItem() != null)
            is = ((ItemEntity) obj).getItem();
        if (obj instanceof BlockPos) {
            Block b = player.level.getBlockState((BlockPos) obj).getBlock();
            is = new ItemStack(b, 1);
        }

        if (is != null) {
            al = AspectHelper.getObjectAspects(is);
        }

        return al != null && al.getAmount(aspect) > 0;
    }

    @Override
    public void onSuccess(Player player, Object object) {
        ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("AUROMANCY"), 1);
        ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("BASICS"), 1);
        ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("ALCHEMY"), 1);
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        return research;
    }
}
