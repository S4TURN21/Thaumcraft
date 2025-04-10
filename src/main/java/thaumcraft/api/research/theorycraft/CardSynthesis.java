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

public class CardSynthesis extends TheorycraftCard {
    Aspect aspect1;
    Aspect aspect2;
    Aspect aspect3;

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("aspect1", aspect1.getTag());
        nbt.putString("aspect2", aspect2.getTag());
        nbt.putString("aspect3", aspect3.getTag());
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        aspect1 = Aspect.getAspect(nbt.getString("aspect1"));
        aspect2 = Aspect.getAspect(nbt.getString("aspect2"));
        aspect3 = Aspect.getAspect(nbt.getString("aspect3"));
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        RandomSource r = RandomSource.create(getSeed());
        int num = Mth.randomBetweenInclusive(r, 0, Aspect.getCompoundAspects().size() - 1);
        aspect3 = Aspect.getCompoundAspects().get(num);
        aspect1 = aspect3.getComponents()[0];
        aspect2 = aspect3.getComponents()[1];
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
        return Component.translatable("card.synthesis.name");
    }

    @Override
    public MutableComponent getLocalizedText() {
        return Component.translatable("card.synthesis.text", Component.translatable(aspect1.getName()).withStyle(ChatFormatting.RESET).withStyle(ChatFormatting.BOLD), Component.translatable(aspect2.getName()).withStyle(ChatFormatting.RESET).withStyle(ChatFormatting.BOLD));
    }

    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[]{ThaumcraftApiHelper.makeCrystal(aspect1), ThaumcraftApiHelper.makeCrystal(aspect2)};
    }

    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[]{true, true};
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        ItemStack res = ThaumcraftApiHelper.makeCrystal(aspect3);
        data.addTotal(getResearchCategory(), 40);
        if (player.getRandom().nextFloat() < 0.33) {
            data.addInspiration(1);
        }
        if (!player.getInventory().add(res)) {
            player.drop(res, true);
        }
        return true;
    }
}
