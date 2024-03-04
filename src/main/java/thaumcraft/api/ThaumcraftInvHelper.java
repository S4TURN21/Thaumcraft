package thaumcraft.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ThaumcraftInvHelper {
    public static class InvFilter {
        public boolean igDmg;
        public boolean igNBT;
        public boolean useOre;
        public boolean useMod;
        public boolean relaxedNBT = false;

        public InvFilter(boolean ignoreDamage, boolean ignoreNBT, boolean useOre, boolean useMod) {
            igDmg = ignoreDamage;
            igNBT = ignoreNBT;
            this.useOre = useOre;
            this.useMod = useMod;
        }

        public InvFilter setRelaxedNBT() {
            relaxedNBT = true;
            return this;
        }

        public static InvFilter STRICT = new InvFilter(false, false, false, false);
        public static InvFilter BASEORE = new InvFilter(false, false, true, false);
    }

    public static boolean areItemStackTagsEqualRelaxed(final ItemStack prime, final ItemStack other) {
        return (prime.isEmpty() && other.isEmpty()) || (!prime.isEmpty() && !other.isEmpty() && (prime.getTag() == null || compareTagsRelaxed(prime.getTag(), other.getTag())));
    }

    public static boolean compareTagsRelaxed(CompoundTag prime, CompoundTag other) {
        for (String key : prime.getAllKeys()) {
            if (!other.contains(key) || !prime.get(key).equals(other.get(key))) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsMatch(boolean strict, ItemStack[] inputs, List<ItemStack> targets) {
        for (ItemStack input : inputs) {
            for (ItemStack target : targets) {
                if (ItemStack.tagMatches(target, input)) {
                    return true;
                }
            }
        }
        return false;
    }
}
