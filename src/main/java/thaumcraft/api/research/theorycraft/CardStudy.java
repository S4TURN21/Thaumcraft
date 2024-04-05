package thaumcraft.api.research.theorycraft;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Random;

public class CardStudy extends TheorycraftCard {
    String cat = "BASICS";

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
        ArrayList<String> list = data.getAvailableCategories(player);
        cat = list.get(r.nextInt(list.size()));
        return cat != null;
    }

    @Override
    public boolean isAidOnly() {
        return true;
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getLocalizedName() {
        return Component.translatable("card.study.name", Component.translatable("tc.research_category." + cat).withStyle(ChatFormatting.DARK_BLUE).withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.RESET).getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.study.text", Component.translatable("tc.research_category." + cat).withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.RESET).getString();
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(cat, player.getRandom().nextIntBetweenInclusive(15, 25));
        return true;
    }
}
