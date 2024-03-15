package thaumcraft.common.config;

import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegisterEvent;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.crafting.ShapedArcaneRecipeBuilder;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.crafting.DustTriggerOre;
import thaumcraft.common.lib.crafting.DustTriggerSimple;
import thaumcraft.common.lib.crafting.RecipeMagicDust;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class ConfigRecipes {
    static ResourceLocation defaultGroup = new ResourceLocation("");
    public static HashMap<String, ArrayList<ResourceLocation>> recipeGroups = new HashMap<>();

    public static void initializeCompoundRecipes() {
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("!gotdream", Blocks.BOOKSHELF, new ItemStack(ItemsTC.thaumonomicon)));
        IDustTrigger.registerDustTrigger(new DustTriggerOre("FIRSTSTEPS@1", "workbench", new ItemStack(BlocksTC.arcaneWorkbench)));
    }

    public static void initializeArcaneRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ShapedArcaneRecipeBuilder.shaped(ItemsTC.thaumometer)
                .research("FIRSTSTEPS@2")
                .vis(20)
                .crystals(new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1))
                .pattern(" I ")
                .pattern("IGI")
                .pattern(" I ")
                .define('I', Items.GOLD_INGOT)
                .define('G', Blocks.GLASS_PANE)
                .save(pFinishedRecipeConsumer);
    }

    public static void initializeNormalRecipes(RegisterEvent.RegisterHelper<RecipeSerializer<?>> event) {
        event.register("salismundus", RecipeMagicDust.Serializer.INSTANCE);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:salismundusfake"), new ShapelessRecipe(ConfigRecipes.defaultGroup, "", new ItemStack(ItemsTC.salisMundus), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.FLINT), Ingredient.of(Items.BOWL), Ingredient.of(Items.REDSTONE), Ingredient.of(new ItemStack(ItemsTC.crystalEssence, 1)), Ingredient.of(new ItemStack(ItemsTC.crystalEssence, 1)), Ingredient.of(new ItemStack(ItemsTC.crystalEssence, 1)))));
    }
}
