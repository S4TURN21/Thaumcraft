package thaumcraft.common.config;

import net.minecraft.core.NonNullList;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Blocks;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.crafting.ShapedArcaneRecipeBuilder;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.crafting.DustTriggerOre;
import thaumcraft.common.lib.crafting.DustTriggerSimple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class ConfigRecipes extends RecipeProvider {
    static String defaultGroup = "";
    public static HashMap<String, ArrayList<ResourceLocation>> recipeGroups = new HashMap<>();

    public ConfigRecipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    public static void initializeCompoundRecipes() {
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("!gotdream", Blocks.BOOKSHELF, new ItemStack(ItemsTC.thaumonomicon)));
        IDustTrigger.registerDustTrigger(new DustTriggerOre("FIRSTSTEPS@1", "workbench", new ItemStack(BlocksTC.arcaneWorkbench)));
    }

    public static void initializeFakeRecipes() {
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:salismundusfake"), new ShapelessRecipe(new ResourceLocation("thaumcraft:salismundusfake"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.salisMundus), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.FLINT), Ingredient.of(Items.BOWL), Ingredient.of(Items.REDSTONE), Ingredient.of(new ItemStack(ItemsTC.crystalEssence, 1)), Ingredient.of(new ItemStack(ItemsTC.crystalEssence, 1)), Ingredient.of(new ItemStack(ItemsTC.crystalEssence, 1)))));
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ConfigRecipes.initializeNormalRecipes(pFinishedRecipeConsumer);
        ConfigRecipes.initializeArcaneRecipes(pFinishedRecipeConsumer);
    }

    public static void initializeArcaneRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ShapedArcaneRecipeBuilder.shaped(ItemsTC.thaumometer).group(ConfigRecipes.defaultGroup).research("FIRSTSTEPS@2").vis(20).crystals(new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1)).pattern(" I ").pattern("IGI").pattern(" I ").define('I', Items.GOLD_INGOT).define('G', Blocks.GLASS_PANE).unlockedBy("has_arcane_workbench", has(BlocksTC.arcaneWorkbench)).save(pFinishedRecipeConsumer);
    }

    public static void initializeNormalRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ShapedRecipeBuilder.shaped(BlocksTC.stoneArcane, 9).group(ConfigRecipes.defaultGroup).pattern("KKK").pattern("KCK").pattern("KKK").define('K', Items.STONE).define('C', ItemsTC.crystalEssence).unlockedBy("has_crystal", has(ItemsTC.crystalEssence)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(BlocksTC.stoneArcaneBrick, 4).group(ConfigRecipes.defaultGroup).pattern("KK").pattern("KK").define('K', BlocksTC.stoneArcane).unlockedBy("has_stone_arcane", has(BlocksTC.stoneArcane)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(BlocksTC.tableWood).group(ConfigRecipes.defaultGroup).pattern("SSS").pattern("W W").define('S', ItemTags.WOODEN_SLABS).define('W', ItemTags.PLANKS).unlockedBy("has_table_wood", has(BlocksTC.tableWood)).save(pFinishedRecipeConsumer);
    }
}
