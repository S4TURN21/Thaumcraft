package thaumcraft.api.research.theorycraft;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class CardNotation extends TheorycraftCard {

    private String cat1, cat2;

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("cat1", cat1);
        nbt.putString("cat2", cat2);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        cat1 = nbt.getString("cat1");
        cat2 = nbt.getString("cat2");
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
    public MutableComponent getLocalizedName() {
        return Component.translatable("card.notation.name");
    }

    @Override
    public MutableComponent getLocalizedText() {
        return Component.translatable("card.notation.text",
                Component.translatable("tc.research_category." + cat1).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RESET),
                Component.translatable("tc.research_category." + cat2).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RESET));
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        if (data.categoryTotals.size() < 2) return false;
        int lVal = Integer.MAX_VALUE;
        String lKey = "";
        int hVal = 0;
        String hKey = "";
        for (String category : data.categoryTotals.keySet()) {
            int q = data.getTotal(category);
            if (q < lVal) {
                lVal = q;
                lKey = category;
            }
            if (q > hVal) {
                hVal = q;
                hKey = category;
            }
        }
        if (hKey.equals(lKey) || lVal <= 0) return false;
        cat1 = lKey;
        cat2 = hKey;
        return true;
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        if (cat1 == null || cat2 == null) return false;
        int lVal = data.getTotal(cat1);
        data.addTotal(cat1, -lVal);
        data.addTotal(cat2, lVal / 2 + player.getRandom().nextIntBetweenInclusive(0, lVal / 2));
        return true;
    }
}