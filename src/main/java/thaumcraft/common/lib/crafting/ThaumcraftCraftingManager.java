package thaumcraft.common.lib.crafting;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;

import java.util.Optional;

public class ThaumcraftCraftingManager {
    public static IArcaneRecipe findMatchingArcaneRecipe(CraftingContainer matrix, Player player) {
        int var2 = 0;
        ItemStack var3 = null;
        ItemStack var4 = null;
        for (int i = 0; i < 15; ++i) {
            ItemStack var6 = matrix.getItem(i);
            if (!var6.isEmpty()) {
                if (var2 == 0) {
                    var3 = var6;
                }
                if (var2 == 1) {
                    var4 = var6;
                }
                ++var2;
            }
        }
        Optional<ShapedArcaneRecipe> optional = player.level.getRecipeManager().getRecipeFor(ShapedArcaneRecipe.Type.INSTANCE, matrix, player.level);
        return optional.orElse(null);
    }
}
