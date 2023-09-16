package thaumcraft.common.config;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegisterEvent;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.crafting.DustTriggerSimple;
import thaumcraft.common.lib.crafting.RecipeMagicDust;

public class ConfigRecipes {
    public static void initializeCompoundRecipes() {
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("!gotdream", Blocks.BOOKSHELF, new ItemStack(ItemsTC.thaumonomicon)));
    }

    public static void initializeNormalRecipes(RegisterEvent.RegisterHelper<RecipeSerializer<?>> event) {
        event.register("salismundus", RecipeMagicDust.Serializer.INSTANCE);
    }
}
