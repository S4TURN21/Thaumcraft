package thaumcraft.api.research.theorycraft;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class CardInspired extends TheorycraftCard {
    String cat = null;
    int amt;

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("cat", cat);
        nbt.putInt("amt", amt);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        cat = nbt.getString("cat");
        amt = nbt.getInt("amt");
    }

    @Override
    public String getResearchCategory() {
        return cat;
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        if (data.categoryTotals.size() < 1) return false;
        int hVal = 0;
        String hKey = "";
        for (String category : data.categoryTotals.keySet()) {
            int q = data.getTotal(category);
            if (q > hVal) {
                hVal = q;
                hKey = category;
            }
        }
        cat = hKey;
        amt = 10 + (hVal / 2);
        return true;
    }

    @Override
    public int getInspirationCost() {
        return 2;
    }

    @Override
    public MutableComponent getLocalizedName() {
        return Component.translatable("card.inspired.name");
    }

    @Override
    public MutableComponent getLocalizedText() {
        return Component.translatable("card.inspired.text", amt, Component.translatable("tc.research_category." + cat).withStyle(ChatFormatting.RESET).withStyle(ChatFormatting.BOLD));
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(cat, amt);
        return true;
    }
}
