package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardCelestial extends TheorycraftCard {
    private static final Item[] NOTES = {
            ItemsTC.celestialNotesSun,
            ItemsTC.celestialNotesStars1,
            ItemsTC.celestialNotesStars2,
            ItemsTC.celestialNotesStars3,
            ItemsTC.celestialNotesStars4,
            ItemsTC.celestialNotesMoon1,
            ItemsTC.celestialNotesMoon2,
            ItemsTC.celestialNotesMoon3,
            ItemsTC.celestialNotesMoon4,
            ItemsTC.celestialNotesMoon5,
            ItemsTC.celestialNotesMoon6,
            ItemsTC.celestialNotesMoon7,
            ItemsTC.celestialNotesMoon8,
    };
    int md1;
    int md2;
    String cat;

    public CardCelestial() {
        cat = "BASICS";
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putInt("md1", md1);
        nbt.putInt("md2", md2);
        nbt.putString("cat", cat);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        md1 = nbt.getInt("md1");
        md2 = nbt.getInt("md2");
        cat = nbt.getString("cat");
    }

    @Override
    public String getResearchCategory() {
        return cat;
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        if (data.categoryTotals.isEmpty() || !ThaumcraftCapabilities.knowsResearch(player, "CELESTIALSCANNING")) {
            return false;
        }
        RandomSource r = RandomSource.create(getSeed());
        md1 = Mth.randomBetweenInclusive(r, 0, 12);
        md2 = md1;
        while (md1 == md2) {
            md2 = r.nextInt(0, 12);
        }
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
        return cat != null;
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getLocalizedName() {
        return Component.translatable("card.celestial.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.celestial.text", Component.translatable("tc.research_category." + cat).withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.RESET).getString();
    }

    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[]{new ItemStack(NOTES[md1], 1), new ItemStack(NOTES[md2], 1)};
    }

    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[]{true, true};
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), Mth.randomBetweenInclusive(player.getRandom(), 25, 50));
        boolean sun = md1 == 0 || md2 == 0;
        boolean moon = md1 > 4 || md2 > 4;
        boolean stars = (md1 > 0 && md1 < 5) || (md2 > 0 && md2 < 5);
        if (stars) {
            int amt = Mth.randomBetweenInclusive(player.getRandom(), 0, 5);
            data.addTotal("ELDRITCH", amt * 2);
            ThaumcraftApi.internalMethods.addWarpToPlayer(player, amt, IPlayerWarp.EnumWarpType.TEMPORARY);
        }
        if (sun) {
            ++data.penaltyStart;
        }
        if (moon) {
            ++data.bonusDraws;
        }
        return true;
    }
}
