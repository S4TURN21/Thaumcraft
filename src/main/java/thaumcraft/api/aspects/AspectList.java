package thaumcraft.api.aspects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class AspectList implements Serializable {
    public LinkedHashMap<Aspect, Integer> aspects;

    public AspectList() {
        this.aspects = new LinkedHashMap<>();
    }

    public int size() {
        return this.aspects.size();
    }

    public Aspect[] getAspects() {
        return this.aspects.keySet().toArray(new Aspect[0]);
    }

    public int getAmount(final Aspect key) {
        return (this.aspects.get(key) == null) ? 0 : this.aspects.get(key);
    }

    public AspectList add(Aspect aspect, int amount) {
        if (this.aspects.containsKey(aspect)) {
            int oldamount = this.aspects.get(aspect);
            amount += oldamount;
        }
        this.aspects.put(aspect, amount);
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
