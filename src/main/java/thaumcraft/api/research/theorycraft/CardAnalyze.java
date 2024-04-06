package thaumcraft.api.research.theorycraft;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;

import java.util.ArrayList;
import java.util.Random;

public class CardAnalyze extends TheorycraftCard {

    String cat = null;

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("cat", cat);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        cat = nbt.getString("cat");
    }

    @Override
    public String getResearchCategory() {
        return cat;
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        Random r = new Random(getSeed());
        ArrayList<String> cats = new ArrayList<>();
        for (ResearchCategory rc : ResearchCategories.researchCategories.values()) {
            if (rc.key == "BASICS") continue;
            if (ThaumcraftCapabilities.getKnowledge(player).getKnowledge(
                    IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.researchCategories.get(cat)) > 0)
                cats.add(rc.key);
        }
        if (cats.size() > 0) {
            cat = cats.get(r.nextInt(cats.size()));
        }
        return cat != null;
    }

    @Override
    public int getInspirationCost() {
        return 2;
    }

    @Override
    public String getLocalizedName() {
        return Component.translatable("card.analyze.name", Component.translatable("tc.research_category." + cat).withStyle(ChatFormatting.DARK_BLUE).withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.RESET).getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.analyze.text", Component.translatable("tc.research_category." + cat).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RESET),
                Component.translatable("tc.research_category.BASICS").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RESET)).getString();
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        ResearchCategory rc = ResearchCategories.getResearchCategory(cat);
        int k = ThaumcraftCapabilities.getKnowledge(player).getKnowledge(IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, rc);
        if (k >= 1) {
            data.addTotal("BASICS", 5);
            ThaumcraftCapabilities.getKnowledge(player).addKnowledge(
                    IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, rc, -IPlayerKnowledge.EnumKnowledgeType.OBSERVATION.getProgression());
            data.addTotal(cat, player.getRandom().nextIntBetweenInclusive(25, 50));
            return true;
        }
        return false;
    }
}