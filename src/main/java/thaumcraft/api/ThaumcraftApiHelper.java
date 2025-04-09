package thaumcraft.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;

public class ThaumcraftApiHelper {
    public static Object getNBTDataFromId(CompoundTag nbt, byte id, String key) {
        switch (id) {
            case 1: return nbt.getByte(key);
            case 2: return nbt.getShort(key);
            case 3: return nbt.getInt(key);
            case 4: return nbt.getLong(key);
            case 5: return nbt.getFloat(key);
            case 6: return nbt.getDouble(key);
            case 7: return nbt.getByteArray(key);
            case 8: return nbt.getString(key);
            case 9: return nbt.getList(key, (byte) 10);
            case 10: return nbt.getCompound(key);
            case 11: return nbt.getIntArray(key);
            default: return null;
        }
    }
    public static ItemStack makeCrystal(Aspect aspect, int stackSize) {
        if (aspect == null) {
            return null;
        }
        ItemStack is = new ItemStack(ItemsTC.crystalEssence, stackSize);
        ((ItemGenericEssentiaContainer) ItemsTC.crystalEssence).setAspects(is, new AspectList().add(aspect, 1));
        return is;
    }

    public static ItemStack makeCrystal(Aspect aspect) {
        return makeCrystal(aspect, 1);
    }
}
