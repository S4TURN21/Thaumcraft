package thaumcraft.common.config;

import net.minecraft.core.NonNullList;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipeBuilder;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.crafting.DustTriggerSimple;
import thaumcraft.common.lib.crafting.RecipeScribingTools;

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
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("FIRSTSTEPS@1", Blocks.CRAFTING_TABLE, new ItemStack(BlocksTC.arcaneWorkbench)));
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("UNLOCKALCHEMY@1", Blocks.CAULDRON, new ItemStack(BlocksTC.crucible)));
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
        ShapedRecipeBuilder.shaped(ItemsTC.phial, 8).group(ConfigRecipes.defaultGroup).pattern(" C ").pattern("G G").pattern(" G ").define('G', Blocks.GLASS).define('C', Items.CLAY_BALL).unlockedBy("has_glass", has(Blocks.GLASS)).unlockedBy("has_clay", has(Items.CLAY)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(BlocksTC.tableWood).group(ConfigRecipes.defaultGroup).pattern("SSS").pattern("W W").define('S', ItemTags.WOODEN_SLABS).define('W', ItemTags.PLANKS).unlockedBy("has_table_wood", has(BlocksTC.tableWood)).save(pFinishedRecipeConsumer);
        String inkwellGroup = "thaumcraft:inkwell";
        SpecialRecipeBuilder.special((SimpleRecipeSerializer<RecipeScribingTools>) ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation("thaumcraft","scribing_tools"))).save(pFinishedRecipeConsumer, "thaumcraft:scribingtoolscraft1");
        ShapelessRecipeBuilder.shapeless(ItemsTC.scribingTools).group(inkwellGroup).requires(Items.GLASS_BOTTLE).requires(Items.FEATHER).requires(Tags.Items.DYES_BLACK).unlockedBy("has_dye", has(Tags.Items.DYES_BLACK)).save(pFinishedRecipeConsumer, "thaumcraft:scribingtoolscraft2");
        ShapelessRecipeBuilder.shapeless(ItemsTC.scribingTools).group(inkwellGroup).requires(ItemsTC.scribingTools).requires(Tags.Items.DYES_BLACK).unlockedBy("has_dye", has(ItemsTC.scribingTools)).save(pFinishedRecipeConsumer, "thaumcraft:scribingtoolsrefill");
    }

    public static void compileGroups(Level level) {
        for (Recipe<?> recipe : level.getRecipeManager().getRecipes()) {
            if (recipe != null) {
                String group = recipe.getGroup();
                if (group.trim().isEmpty()) {
                    continue;
                }
                if (recipe.getId().getNamespace().equals("minecraft")) {
                    continue;
                }
                if (!ConfigRecipes.recipeGroups.containsKey(group)) {
                    ConfigRecipes.recipeGroups.put(group, new ArrayList<>());
                }
                ArrayList<ResourceLocation> list = ConfigRecipes.recipeGroups.get(group);
                list.add(recipe.getId());
            }
        }
        for (ResourceLocation reg : CommonInternals.craftingRecipeCatalog.keySet()) {
            IThaumcraftRecipe recipe2 = CommonInternals.craftingRecipeCatalog.get(reg);
            if (recipe2 != null) {
                String group = recipe2.getGroup();
                if (group == null) {
                    continue;
                }
                if (group.trim().isEmpty()) {
                    continue;
                }
                if (!ConfigRecipes.recipeGroups.containsKey(group)) {
                    ConfigRecipes.recipeGroups.put(group, new ArrayList<>());
                }
                ArrayList<ResourceLocation> list = ConfigRecipes.recipeGroups.get(group);
                list.add(reg);
            }
        }
        for (ResourceLocation reg : CommonInternals.craftingRecipeCatalogFake.keySet()) {
            Object recipe3 = CommonInternals.craftingRecipeCatalogFake.get(reg);
            if (recipe3 != null) {
                String group = "";
                if (recipe3 instanceof CraftingRecipe) {
                    group = ((CraftingRecipe) recipe3).getGroup();
                } else if (recipe3 instanceof IThaumcraftRecipe) {
                    group = ((IThaumcraftRecipe) recipe3).getGroup();
                }
                if (group == null) {
                    continue;
                }
                if (group.trim().isEmpty()) {
                    continue;
                }
                if (!ConfigRecipes.recipeGroups.containsKey(group)) {
                    ConfigRecipes.recipeGroups.put(group, new ArrayList<ResourceLocation>());
                }
                ArrayList<ResourceLocation> list = ConfigRecipes.recipeGroups.get(group);
                list.add(reg);
            }
        }
    }
}
