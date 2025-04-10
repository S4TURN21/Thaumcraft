package thaumcraft.api.research.theorycraft;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;

public class CardReactions extends TheorycraftCard {
    Aspect aspect1;
    Aspect aspect2;

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("aspect1", aspect1.getTag());
        nbt.putString("aspect2", aspect2.getTag());
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        aspect1 = Aspect.getAspect(nbt.getString("aspect1"));
        aspect2 = Aspect.getAspect(nbt.getString("aspect2"));
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        RandomSource r = RandomSource.create(getSeed());
        int num = Mth.randomBetweenInclusive(r, 0, Aspect.getCompoundAspects().size() - 1);
        aspect1 = Aspect.getCompoundAspects().get(num);
        int num2;
        for (num2 = num; num2 == num; num2 = Mth.randomBetweenInclusive(r, 0, Aspect.getCompoundAspects().size() - 1)) {}
        aspect2 = Aspect.getCompoundAspects().get(num2);
        return true;
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "ALCHEMY";
    }

    @Override
    public MutableComponent getLocalizedName() {
        return Component.translatable("card.reactions.name");
    }

    @Override
    public MutableComponent getLocalizedText() {
        return Component.translatable("card.reactions.text",  Component.translatable(aspect1.getName()).withStyle(ChatFormatting.RESET).withStyle(ChatFormatting.BOLD), Component.translatable(aspect2.getName()).withStyle(ChatFormatting.RESET).withStyle(ChatFormatting.BOLD));
    }

    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[]{ThaumcraftApiHelper.makeCrystal(aspect1), ThaumcraftApiHelper.makeCrystal(aspect2)};
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 25);
        if (player.getRandom().nextFloat() < 0.33) {
            data.addInspiration(1);
        }
        return true;
    }
}