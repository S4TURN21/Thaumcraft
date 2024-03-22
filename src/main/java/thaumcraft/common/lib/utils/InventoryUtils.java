package thaumcraft.common.lib.utils;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.registries.ForgeRegistries;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.items.ItemsTC;

import java.util.List;
import java.util.Map;

public class InventoryUtils {
    public static boolean consumePlayerItem(Player player, ItemStack item, boolean nocheck, boolean ore) {
        if (!nocheck && !isPlayerCarryingAmount(player, item, ore)) {
            return false;
        }
        int count = item.getCount();
        for (int var2 = 0; var2 < player.getInventory().getContainerSize(); ++var2) {
            if (checkEnchantedPlaceholder(item, player.getInventory().getItem(var2)) || areItemStacksEqual(player.getInventory().getItem(var2), item, new ThaumcraftInvHelper.InvFilter(false, !item.hasTag(), ore, false).setRelaxedNBT())) {
                if (player.getInventory().getItem(var2).getCount() > count) {
                    player.getInventory().getItem(var2).shrink(count);
                    count = 0;
                } else {
                    count -= player.getInventory().getItem(var2).getCount();
                    player.getInventory().setItem(var2, ItemStack.EMPTY);
                }
                if (count <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPlayerCarryingAmount(Player player, ItemStack stack, boolean ore) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        int count = stack.getCount();
        for (int var2 = 0; var2 < player.getInventory().getContainerSize(); ++var2) {
            if (checkEnchantedPlaceholder(stack, player.getInventory().getItem(var2)) || areItemStacksEqual(player.getInventory().getItem(var2), stack, new ThaumcraftInvHelper.InvFilter(false, !stack.hasTag(), ore, false).setRelaxedNBT())) {
                count -= player.getInventory().getItem(var2).getCount();
                if (count <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkEnchantedPlaceholder(ItemStack stack, ItemStack stack2) {
        if (stack.getItem() != ItemsTC.enchantedPlaceholder) {
            return false;
        }
        Map<Enchantment, Integer> en = EnchantmentHelper.getEnchantments(stack);
        boolean b = !en.isEmpty();
        Label_0181:
        for (Enchantment e : en.keySet()) {
            Map<Enchantment, Integer> en2 = EnchantmentHelper.getEnchantments(stack2);
            if (en2.isEmpty()) {
                return false;
            }
            b = false;
            for (Enchantment e2 : en2.keySet()) {
                if (!e2.equals(e)) {
                    continue;
                }
                b = true;
                if (en2.get(e2) < en.get(e)) {
                    b = false;
                    break Label_0181;
                }
            }
        }
        return b;
    }

    public static boolean areItemStacksEqual(ItemStack stack0, ItemStack stack1, ThaumcraftInvHelper.InvFilter filter) {
        if (stack0 == null && stack1 != null) {
            return false;
        }
        if (stack0 != null && stack1 == null) {
            return false;
        }
        if (stack0 == null && stack1 == null) {
            return true;
        }
        if (stack0.isEmpty() && !stack1.isEmpty()) {
            return false;
        }
        if (!stack0.isEmpty() && stack1.isEmpty()) {
            return false;
        }
        if (stack0.isEmpty() && stack1.isEmpty()) {
            return true;
        }
        if (filter.useMod) {
            String m1 = "A";
            String m2 = "B";
            String a = ForgeRegistries.ITEMS.getKey(stack0.getItem()).getNamespace();
            if (a != null) {
                m1 = a;
            }
            String b = ForgeRegistries.ITEMS.getKey(stack1.getItem()).getNamespace();
            if (b != null) {
                m2 = b;
            }
            return m1.equals(m2);
        }
        boolean t1 = true;
        if (!filter.igNBT) {
            t1 = (filter.relaxedNBT ? ThaumcraftInvHelper.areItemStackTagsEqualRelaxed(stack0, stack1) : ItemStack.tagMatches(stack0, stack1));
        }
        if (stack0.getDamageValue() == stack0.getMaxDamage() || stack1.getDamageValue() == stack0.getMaxDamage()) {
            filter.igDmg = true;
        }
        boolean t2 = !filter.igDmg && stack0.getDamageValue() != stack1.getDamageValue();
        return stack0.getItem() == stack1.getItem() && !t2 && t1;
    }

    public static void dropItemAtEntity(Level world, ItemStack item, Entity entity) {
        if (!world.isClientSide && item != null && !item.isEmpty() && item.getCount() > 0) {
            ItemEntity entityItem = new ItemEntity(world, entity.getX(), entity.getY() + entity.getEyeHeight() / 2.0f, entity.getZ(), item.copy());
            world.addFreshEntity(entityItem);
        }
    }

    public static ItemStack cycleItemStack(final Object input) {
        return cycleItemStack(input, 0);
    }

    public static ItemStack cycleItemStack(Object input, int counter) {
        ItemStack it = ItemStack.EMPTY;
        if (input instanceof Ingredient) {
            boolean b = !((Ingredient) input).isSimple() && !(input instanceof PartialNBTIngredient);
            NonNullList<ItemStack> lst = NonNullList.create();
            for (ItemStack s : ((Ingredient) input).getItems()) {
                if (s.isEmpty())
                    continue;
                if (!s.hasTag()) {
                    s.getItem().fillItemCategory(CreativeModeTab.TAB_SEARCH, lst);
                } else {
                    lst.add(s);
                }
            }
            input = getMatchingStacks((Ingredient) input);
            if (b) {
                ItemStack[] q = (ItemStack[]) input;
                ItemStack[] r = new ItemStack[q.length];
                for (int a = 0; a < q.length; ++a) {
                    (r[a] = q[a].copy()).setDamageValue(r[a].getMaxDamage());
                }
                input = r;
            }
        }
        if (input instanceof ItemStack[]) {
            ItemStack[] q2 = (ItemStack[]) input;
            if (q2 != null && q2.length > 0) {
                int idx = (int) ((counter + System.currentTimeMillis() / 1000L) % q2.length);
                it = cycleItemStack(q2[idx], counter++);
            }
        } else if (input instanceof ItemStack) {
            it = (ItemStack) input;
            if (it != null && !it.isEmpty() && it.getItem() != null && it.isDamageableItem() && it.getDamageValue() == Short.MAX_VALUE) {
                int q3 = 5000 / it.getMaxDamage();
                int md = (int) ((counter + System.currentTimeMillis() / q3) % it.getMaxDamage());
                ItemStack it2 = new ItemStack(it.getItem(), 1);
                it2.setTag(it.getTag());
                it = it2;
            }
        } else if (input instanceof List) {
            List<ItemStack> q4 = (List<ItemStack>) input;
            if (q4 != null && q4.size() > 0) {
                int idx = (int) ((counter + System.currentTimeMillis() / 1000L) % q4.size());
                it = cycleItemStack(q4.get(idx), counter++);
            }
        }
        return it;
    }

    public static ItemStack[] getMatchingStacks(Ingredient ingredient) {
        NonNullList<ItemStack> lst = NonNullList.create();
        for (ItemStack s : ingredient.getItems()) {
            if (s.isEmpty())
                continue;
            if (!s.hasTag()) {
                s.getItem().fillItemCategory(CreativeModeTab.TAB_SEARCH, lst);
            } else {
                lst.add(s);
            }
        }
        return lst.toArray(new ItemStack[lst.size()]);
    }
}
