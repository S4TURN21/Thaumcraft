package thaumcraft.api.aspects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class AspectList implements Serializable {
    public LinkedHashMap<Aspect, Integer> aspects = new LinkedHashMap<>();

    public AspectList() {
    }

    public AspectList copy() {
        AspectList out = new AspectList();
        for (Aspect a : getAspects())
            out.add(a, getAmount(a));
        return out;
    }

    public int size() {
        return this.aspects.size();
    }

    public int visSize() {
        int q = 0;

        for (Aspect as : aspects.keySet()) {
            q += getAmount(as);
        }

        return q;
    }

    public Aspect[] getAspects() {
        return this.aspects.keySet().toArray(new Aspect[]{});
    }

    public Aspect[] getAspectsSortedByName() {
        try {
            Aspect[] out = aspects.keySet().toArray(new Aspect[]{});
            boolean change = false;
            do {
                change = false;
                for (int a = 0; a < out.length - 1; a++) {
                    Aspect e1 = out[a];
                    Aspect e2 = out[a + 1];
                    if (e1 != null && e2 != null && e1.getTag().compareTo(e2.getTag()) > 0) {
                        out[a] = e2;
                        out[a + 1] = e1;
                        change = true;
                        break;
                    }
                }
            } while (change == true);
            return out;
        } catch (Exception e) {
            return getAspects();
        }
    }

    public int getAmount(final Aspect key) {
        return (this.aspects.get(key) == null) ? 0 : this.aspects.get(key);
    }

    public boolean reduce(Aspect key, int amount) {
        if (getAmount(key) >= amount) {
            int am = getAmount(key) - amount;
            aspects.put(key, am);
            return true;
        }
        return false;
    }

    public AspectList remove(Aspect key, int amount) {
        int am = getAmount(key) - amount;
        if (am <= 0) {
            aspects.remove(key);
        } else {
            aspects.put(key, am);
        }
        return this;
    }

    public AspectList remove(Aspect key) {
        aspects.remove(key);
        return this;
    }

    public AspectList add(Aspect aspect, int amount) {
        if (this.aspects.containsKey(aspect)) {
            int oldamount = this.aspects.get(aspect);
            amount += oldamount;
        }
        this.aspects.put(aspect, amount);
        return this;
    }

    public AspectList merge(Aspect aspect, int amount) {
        if (aspects.containsKey(aspect)) {
            int oldamount = aspects.get(aspect);
            if (amount < oldamount) amount = oldamount;

        }
        aspects.put(aspect, amount);
        return this;
    }

    public void readFromNBT(CompoundTag nbttagcompound) {
        this.aspects.clear();
        ListTag tlist = nbttagcompound.getList("Aspects", 10);
        for (int j = 0; j < tlist.size(); ++j) {
            CompoundTag rs = tlist.getCompound(j);
            if (rs.contains("key")) {
                this.add(Aspect.getAspect(rs.getString("key")), rs.getInt("amount"));
            }
        }
    }

    public void writeToNBT(CompoundTag nbttagcompound) {
        ListTag tlist = new ListTag();
        nbttagcompound.put("Aspects", tlist);
        for (final Aspect aspect : this.getAspects()) {
            if (aspect != null) {
                CompoundTag f = new CompoundTag();
                f.putString("key", aspect.getTag());
                f.putInt("amount", this.getAmount(aspect));
                tlist.add(f);
            }
        }
    }
}
