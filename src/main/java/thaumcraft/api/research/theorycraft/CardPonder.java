package thaumcraft.api.research.theorycraft;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class CardPonder extends TheorycraftCard {
    @Override
    public int getInspirationCost() {
        return 2;
    }

    @Override
    public String getLocalizedName() {
        return Component.translatable("card.ponder.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.ponder.text").getString();
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        return data.categoriesBlocked.size() < data.categoryTotals.size();
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        int a = 25;
        int tries = 0;
        while (a > 0 && tries < 1000) {
            tries++;
            for (String category : data.categoryTotals.keySet()) {
                if (data.categoriesBlocked.contains(category)) {
                    if (data.categoryTotals.size() <= 1) return false;
                    continue;
                }
                data.addTotal(category, 1);
                a--;
                if (a <= 0) break;
            }
        }
        data.addTotal("BASICS", 5);
        data.bonusDraws++;
        return a != 20;
    }
}