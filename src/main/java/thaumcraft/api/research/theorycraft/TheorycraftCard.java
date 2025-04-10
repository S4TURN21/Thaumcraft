package thaumcraft.api.research.theorycraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public abstract class TheorycraftCard {
    private long seed = -1;

    public long getSeed() {
        if (seed < 0) {
            setSeed(System.nanoTime());
        }
        return seed;
    }

    public boolean initialize(Player player, ResearchTableData data) {
        return true;
    }

    public boolean isAidOnly() {
        return false;
    }

    public abstract int getInspirationCost();

    public String getResearchCategory() {
        return null;
    }

    public abstract MutableComponent getLocalizedName();

    public abstract MutableComponent getLocalizedText();

    public ItemStack[] getRequiredItems() {
        return null;
    }

    public boolean[] getRequiredItemsConsumed() {
        if (getRequiredItems() != null) {
            boolean[] b = new boolean[getRequiredItems().length];
            Arrays.fill(b, false);
            return b;
        }
        return null;
    }

    public abstract boolean activate(Player player, ResearchTableData data);

    public void setSeed(long seed) {
        this.seed = Math.abs(seed);
    }

    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("seed", seed);
        return nbt;
    }

    public void deserialize(CompoundTag nbt) {
        if (nbt == null) return;
        seed = nbt.getLong("seed");
    }
}
