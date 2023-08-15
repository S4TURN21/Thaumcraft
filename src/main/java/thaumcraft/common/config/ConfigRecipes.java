package thaumcraft.common.config;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.RegisterEvent;
import thaumcraft.common.lib.crafting.RecipeMagicDust;

public class ConfigRecipes {
    public static void initializeNormalRecipes(RegisterEvent.RegisterHelper<RecipeSerializer<?>> event) {
        event.register("salismundus", RecipeMagicDust.Serializer.INSTANCE);
    }
}
