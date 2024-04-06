package thaumcraft.api.research.theorycraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

public class ResearchTableData {
    public BlockEntity table;
    public String player;
    public int inspiration;
    public int inspirationStart;
    public int bonusDraws;
    public int placedCards;
    public int aidsChosen;
    public int penaltyStart;
    public ArrayList<Long> savedCards = new ArrayList<>();
    public ArrayList<String> aidCards = new ArrayList<>();
    public TreeMap<String, Integer> categoryTotals = new TreeMap<>();
    public ArrayList<String> categoriesBlocked = new ArrayList<>();
    public ArrayList<CardChoice> cardChoices = new ArrayList<>();
    public CardChoice lastDraw;

    public class CardChoice {
        public TheorycraftCard card;
        public String key;
        public boolean fromAid;
        public boolean selected;

        public CardChoice(String key, TheorycraftCard card, boolean aid, boolean selected) {
            this.key = key;
            this.card = card;
            fromAid = aid;
            this.selected = selected;
        }

        @Override
        public String toString() {
            return "key:" + key + " card:" + card.getSeed() + " fromAid:" + fromAid + " selected:" + selected;
        }
    }

    public ResearchTableData(BlockEntity tileResearchTable) {
        this.table = tileResearchTable;
    }

    public ResearchTableData(Player player, BlockEntity tileResearchTable) {
        this.player = player.getName().getString();
        this.table = tileResearchTable;
    }

    public boolean isComplete() {
        return inspiration <= 0;
    }

    public boolean hasTotal(String cat) {
        return categoryTotals.containsKey(cat);
    }

    public int getTotal(String cat) {
        return categoryTotals.containsKey(cat) ? categoryTotals.get(cat) : 0;
    }

    public void addTotal(String cat, int amt) {
        int current = categoryTotals.containsKey(cat) ? categoryTotals.get(cat) : 0;
        current += amt;
        if (current <= 0)
            categoryTotals.remove(cat);
        else
            categoryTotals.put(cat, current);
    }

    public void addInspiration(int amt) {
        inspiration += amt;
        if (inspiration > inspirationStart) {
            inspiration = inspirationStart;
        }
    }

    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();

        nbt.putString("player", player);
        nbt.putInt("inspiration", inspiration);
        nbt.putInt("inspirationStart", inspirationStart);
        nbt.putInt("placedCards", placedCards);
        nbt.putInt("bonusDraws", bonusDraws);
        nbt.putInt("aidsChosen", aidsChosen);
        nbt.putInt("penaltyStart", penaltyStart);

        ListTag savedTag = new ListTag();
        for (Long card : savedCards) {
            CompoundTag gt = new CompoundTag();
            gt.putLong("card", card);
            savedTag.add(gt);
        }
        nbt.put("savedCards", savedTag);

        ListTag categoriesBlockedTag = new ListTag();
        for (String category : categoriesBlocked) {
            CompoundTag gt = new CompoundTag();
            gt.putString("category", category);
            categoriesBlockedTag.add(gt);
        }
        nbt.put("categoriesBlocked", categoriesBlockedTag);

        ListTag categoryTotalsTag = new ListTag();
        for (String category : categoryTotals.keySet()) {
            CompoundTag gt = new CompoundTag();
            gt.putString("category", category);
            gt.putInt("total", categoryTotals.get(category));
            categoryTotalsTag.add(gt);
        }
        nbt.put("categoryTotals", categoryTotalsTag);

        ListTag aidCardsTag = new ListTag();
        for (String mc : aidCards) {
            CompoundTag gt = new CompoundTag();
            gt.putString("aidCard", mc);
            aidCardsTag.add(gt);
        }
        nbt.put("aidCards", aidCardsTag);

        ListTag cardChoicesTag = new ListTag();
        for (CardChoice mc : cardChoices) {
            CompoundTag gt = serializeCardChoice(mc);
            cardChoicesTag.add(gt);
        }
        nbt.put("cardChoices", cardChoicesTag);

        if (lastDraw != null) nbt.put("lastDraw", serializeCardChoice(lastDraw));

        return nbt;
    }

    public CompoundTag serializeCardChoice(CardChoice mc) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("cardChoice", mc.key);
        nbt.putBoolean("aid", mc.fromAid);
        nbt.putBoolean("select", mc.selected);
        try {
            nbt.put("cardNBT", mc.card.serialize());
        } catch (Exception ignored) {
        }
        return nbt;
    }

    public void deserialize(CompoundTag nbt) {
        if (nbt == null) return;
        inspiration = nbt.getInt("inspiration");
        inspirationStart = nbt.getInt("inspirationStart");
        placedCards = nbt.getInt("placedCards");
        bonusDraws = nbt.getInt("bonusDraws");
        aidsChosen = nbt.getInt("aidsChosen");
        penaltyStart = nbt.getInt("penaltyStart");
        player = nbt.getString("player");

        ListTag savedTag = nbt.getList("savedCards", (byte) 10);
        savedCards = new ArrayList<>();
        for (int x = 0; x < savedTag.size(); x++) {
            CompoundTag nbtdata = savedTag.getCompound(x);
            savedCards.add(nbtdata.getLong("card"));
        }

        ListTag categoriesBlockedTag = nbt.getList("categoriesBlocked", (byte) 10);
        categoriesBlocked = new ArrayList<String>();
        for (int x = 0; x < categoriesBlockedTag.size(); x++) {
            CompoundTag nbtdata = categoriesBlockedTag.getCompound(x);
            categoriesBlocked.add(nbtdata.getString("category"));
        }

        ListTag categoryTotalsTag = nbt.getList("categoryTotals", (byte) 10);
        categoryTotals = new TreeMap<String, Integer>();
        for (int x = 0; x < categoryTotalsTag.size(); x++) {
            CompoundTag nbtdata = categoryTotalsTag.getCompound(x);
            categoryTotals.put(nbtdata.getString("category"), nbtdata.getInt("total"));
        }

        ListTag aidCardsTag = nbt.getList("aidCards", (byte) 10);
        aidCards = new ArrayList<String>();
        for (int x = 0; x < aidCardsTag.size(); x++) {
            CompoundTag nbtdata = aidCardsTag.getCompound(x);
            aidCards.add(nbtdata.getString("aidCard"));
        }

        Player pe = null;
        if (table != null && table.getLevel() != null && !table.getLevel().isClientSide) {
            pe = table.getLevel().getServer().getPlayerList().getPlayerByName(player);
        }

        ListTag cardChoicesTag = nbt.getList("cardChoices", (byte) 10);
        cardChoices = new ArrayList<CardChoice>();
        for (int x = 0; x < cardChoicesTag.size(); x++) {
            CompoundTag nbtdata = cardChoicesTag.getCompound(x);
            CardChoice cc = deserializeCardChoice(nbtdata);
            if (cc != null) cardChoices.add(cc);
        }

        lastDraw = deserializeCardChoice(nbt.getCompound("lastDraw"));
    }

    public CardChoice deserializeCardChoice(CompoundTag nbt) {
        if (nbt == null) return null;
        String key = nbt.getString("cardChoice");
        TheorycraftCard tc = generateCardWithNBT(nbt.getString("cardChoice"), nbt.getCompound("cardNBT"));
        if (tc == null) return null;
        return new CardChoice(key, tc, nbt.getBoolean("aid"), nbt.getBoolean("select"));
    }

    private boolean isCategoryBlocked(String cat) {
        return categoriesBlocked.contains(cat);
    }

    public void drawCards(int draw, Player pe) {
        if (draw == 3) {
            if (bonusDraws > 0) {
                bonusDraws--;
            } else {
                draw = 2;
            }
        }
        cardChoices.clear();
        player = pe.getName().toString();
        ArrayList<String> availCats = getAvailableCategories(pe);
        ArrayList<String> drawnCards = new ArrayList<>();
        boolean aidDrawn = false;
        int failsafe = 0;
        while (draw > 0 && failsafe < 10000) {
            failsafe++;
            if (!aidDrawn && !aidCards.isEmpty() && pe.getRandom().nextFloat() <= .25) {
                int idx = pe.getRandom().nextInt(aidCards.size());
                String key = aidCards.get(idx);
                TheorycraftCard card = generateCard(key, -1, pe);
                if (card == null || card.getInspirationCost() > inspiration || isCategoryBlocked(card.getResearchCategory()))
                    continue;

                if (drawnCards.contains(key)) continue;
                drawnCards.add(key);
                cardChoices.add(new CardChoice(key, card, true, false));
                aidCards.remove(idx);
            } else {
                try {
                    String[] cards = TheorycraftManager.cards.keySet().toArray(new String[]{});
                    int idx = pe.getRandom().nextInt(cards.length);
                    TheorycraftCard card = generateCard(cards[idx], -1, pe);
                    if (card == null || card.isAidOnly() || card.getInspirationCost() > inspiration) continue;
                    if (card.getResearchCategory() != null) {
                        boolean found = false;
                        for (String cn : availCats) {
                            if (cn.equals(card.getResearchCategory())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) continue;
                    }

                    if (drawnCards.contains(cards[idx])) continue;
                    drawnCards.add(cards[idx]);
                    cardChoices.add(new CardChoice(cards[idx], card, false, false));
                } catch (Exception e) {
                    continue;
                }
            }
            draw--;
        }
    }

    private TheorycraftCard generateCard(String key, long seed, Player pe) {
        if (key == null) return null;
        Class<TheorycraftCard> tcc = TheorycraftManager.cards.get(key);
        if (tcc == null) return null;
        TheorycraftCard tc = null;
        try {
            tc = tcc.newInstance();
            if (seed < 0)
                if (pe != null)
                    tc.setSeed(pe.getRandom().nextLong());
                else
                    tc.setSeed(System.nanoTime());
            else
                tc.setSeed(seed);
            if (pe != null && !tc.initialize(pe, this)) return null;
        } catch (Exception ignored) {
        }
        return tc;
    }

    private TheorycraftCard generateCardWithNBT(String key, CompoundTag nbt) {
        if (key == null) return null;
        Class<TheorycraftCard> tcc = TheorycraftManager.cards.get(key);
        if (tcc == null) return null;
        TheorycraftCard tc = null;
        try {
            tc = tcc.newInstance();
            tc.deserialize(nbt);
        } catch (Exception ignored) {
        }
        return tc;
    }

    public void initialize(Player player, Set<String> aids) {
        inspirationStart = getAvailableInspiration(player);
        inspiration = inspirationStart - aids.size();
    }

    public ArrayList<String> getAvailableCategories(Player player) {
        ArrayList<String> cats = new ArrayList<String>();
        for (String rck : ResearchCategories.researchCategories.keySet()) {
            ResearchCategory rc = ResearchCategories.getResearchCategory(rck);
            if (rc == null || isCategoryBlocked(rck)) continue;
            if (rc.researchKey == null || ThaumcraftCapabilities.knowsResearchStrict(player, rc.researchKey)) {
                cats.add(rck);
            }
        }
        return cats;
    }

    public static int getAvailableInspiration(Player player) {
        float tot = 5;
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        for (String s : knowledge.getResearchList()) {
            if (ThaumcraftCapabilities.knowsResearchStrict(player, s)) {
                ResearchEntry re = ResearchCategories.getResearch(s);
                if (re == null) {
                    continue;
                }
                if (re.hasMeta(ResearchEntry.EnumResearchMeta.SPIKY)) {
                    tot += .5f;
                }
                if (re.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN)) {
                    tot += .1f;
                }
            }
        }
        return Math.min(15, Math.round(tot));
    }
}
