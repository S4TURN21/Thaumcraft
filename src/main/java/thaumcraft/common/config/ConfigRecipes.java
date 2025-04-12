package thaumcraft.common.config;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ItemTagsTC;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipeBuilder;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipeBuilder;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.items.ItemsTC;
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
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("FIRSTSTEPS@1", Blocks.CRAFTING_TABLE, new ItemStack(BlocksTC.arcaneWorkbench)));
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("UNLOCKALCHEMY@1", Blocks.CAULDRON, new ItemStack(BlocksTC.crucible)));
    }

    public static void initializeAlchemyRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ResourceLocation visCrystalGroup = new ResourceLocation("thaumcraft:viscrystalgroup");
        for (Aspect aspect : Aspect.aspects.values()) {
            CrucibleRecipeBuilder.smelting(ThaumcraftApiHelper.makeCrystal(aspect)).research("BASEALCHEMY").catalyst(ItemsTC.nuggetQuartz).aspects(new AspectList().add(aspect, 2)).group(visCrystalGroup.toString()).save(pFinishedRecipeConsumer, "thaumcraft:vis_crystal_" + aspect.getTag());
        }
        ResourceLocation nitorGroup = new ResourceLocation("thaumcraft", "nitorgroup");
        CrucibleRecipeBuilder.smelting(new ItemStack(BlocksTC.nitor.get(DyeColor.YELLOW))).group(nitorGroup.toString()).research("UNLOCKALCHEMY@3").catalyst(Tags.Items.DUSTS_GLOWSTONE).aspects(new AspectList().merge(Aspect.ENERGY, 10).merge(Aspect.FIRE, 10).merge(Aspect.LIGHT, 10)).save(pFinishedRecipeConsumer);
        for (DyeColor d : DyeColor.values()) {
            ShapelessRecipeBuilder.shapeless(BlocksTC.nitor.get(d)).group(nitorGroup.toString()).unlockedBy("has_nitor", has(ItemTagsTC.NITOR)).requires(ItemTagsTC.NITOR).requires(d.getTag()).save(pFinishedRecipeConsumer, "thaumcraft:nitor_dye_" + d.getName().toLowerCase());
        }
        CrucibleRecipeBuilder.smelting(new ItemStack(ItemsTC.alumentum)).research("ALUMENTUM").catalyst(ItemTags.COALS).aspects(new AspectList().merge(Aspect.ENERGY, 10).merge(Aspect.FIRE, 10).merge(Aspect.ENTROPY, 5)).save(pFinishedRecipeConsumer);
        CrucibleRecipeBuilder.smelting(new ItemStack(ItemsTC.brassIngot)).research("METALLURGY@1").catalyst(Tags.Items.INGOTS_IRON).aspects(new AspectList().merge(Aspect.TOOL, 5)).save(pFinishedRecipeConsumer);
        CrucibleRecipeBuilder.smelting(new ItemStack(ItemsTC.thaumiumIngot)).research("METALLURGY@2").catalyst(Tags.Items.INGOTS_IRON).aspects(new AspectList().merge(Aspect.MAGIC, 5).merge(Aspect.EARTH, 5)).save(pFinishedRecipeConsumer);
    }

    public static void initializeFakeRecipes() {
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:salismundusfake"), new ShapelessRecipe(new ResourceLocation("thaumcraft:salismundusfake"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.salisMundus), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.FLINT), Ingredient.of(Items.BOWL), Ingredient.of(Items.REDSTONE), Ingredient.of(new ItemStack(ItemsTC.crystalEssence, 1)), Ingredient.of(new ItemStack(ItemsTC.crystalEssence, 1)), Ingredient.of(new ItemStack(ItemsTC.crystalEssence, 1)))));
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ConfigRecipes.initializeNormalRecipes(pFinishedRecipeConsumer);
        ConfigRecipes.initializeArcaneRecipes(pFinishedRecipeConsumer);
        ConfigRecipes.initializeAlchemyRecipes(pFinishedRecipeConsumer);
    }

    public static void initializeArcaneRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ShapedArcaneRecipeBuilder.shaped(ItemsTC.thaumometer).group(ConfigRecipes.defaultGroup).research("FIRSTSTEPS@2").vis(20).crystals(new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1)).pattern(" I ").pattern("IGI").pattern(" I ").define('I', Items.GOLD_INGOT).define('G', Blocks.GLASS_PANE).unlockedBy("has_arcane_workbench", has(BlocksTC.arcaneWorkbench)).save(pFinishedRecipeConsumer);
        ShapedArcaneRecipeBuilder.shaped(ItemsTC.filter, 2).group(ConfigRecipes.defaultGroup).research("BASEALCHEMY").vis(15).crystals(new AspectList().add(Aspect.WATER, 1)).pattern("GWG").define('G', Items.GOLD_INGOT).define('W', BlocksTC.plankSilverwood).save(pFinishedRecipeConsumer);
    }

    public static void initializeNormalRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ResourceLocation brassGroup = new ResourceLocation("thaumcraft", "brass_stuff");
        ResourceLocation thaumiumGroup = new ResourceLocation("thaumcraft", "thaumium_stuff");
        ShapelessRecipeBuilder.shapeless(ItemsTC.nuggetQuartz, 9).requires(Items.QUARTZ).group(ConfigRecipes.defaultGroup).unlockedBy(getHasName(Items.QUARTZ), has(Items.QUARTZ)).save(pFinishedRecipeConsumer, new ResourceLocation(Thaumcraft.MODID, "quartz_to_nuggets"));
        nineBlockStorageRecipes(pFinishedRecipeConsumer, ItemsTC.thaumiumIngot, BlocksTC.metalBlockThaumium, "thaumcraft:thaumiumingotstoblock", thaumiumGroup.toString(), "thaumcraft:thaumiumblocktoingots", thaumiumGroup.toString());
        nineBlockStorageRecipes(pFinishedRecipeConsumer, ItemsTC.brassIngot, BlocksTC.metalBlockBrass, "thaumcraft:brassingotstoblock", brassGroup.toString(), "thaumcraft:brassblocktoingots", brassGroup.toString());
        ShapedRecipeBuilder.shaped(ItemsTC.brassPlate, 3).group(brassGroup.toString()).pattern("BBB").define('B', ItemsTC.brassIngot).unlockedBy("has_brass", has(ItemsTC.brassIngot)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(ItemsTC.thaumiumPlate, 3).group(thaumiumGroup.toString()).pattern("BBB").define('B', ItemsTC.thaumiumIngot).unlockedBy("has_thaumium", has(ItemsTC.thaumiumIngot)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(ItemsTC.thaumiumHelm).group(thaumiumGroup.toString()).pattern("III").pattern("I I").define('I', ItemsTC.thaumiumIngot).unlockedBy("has_thaumium", has(ItemsTC.thaumiumIngot)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(ItemsTC.thaumiumChest).group(thaumiumGroup.toString()).pattern("I I").pattern("III").pattern("III").define('I', ItemsTC.thaumiumIngot).unlockedBy("has_thaumium", has(ItemsTC.thaumiumIngot)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(ItemsTC.thaumiumLegs).group(thaumiumGroup.toString()).pattern("III").pattern("I I").pattern("I I").define('I', ItemsTC.thaumiumIngot).unlockedBy("has_thaumium", has(ItemsTC.thaumiumIngot)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(ItemsTC.thaumiumBoots).group(thaumiumGroup.toString()).pattern("I I").pattern("I I").define('I', ItemsTC.thaumiumIngot).unlockedBy("has_thaumium", has(ItemsTC.thaumiumIngot)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(ItemsTC.thaumiumAxe).group(thaumiumGroup.toString()).pattern("II").pattern("SI").pattern("S ").define('I', ItemsTC.thaumiumIngot).define('S', Items.STICK).unlockedBy("has_thaumium", has(ItemsTC.thaumiumIngot)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(ItemsTC.thaumiumSword).group(thaumiumGroup.toString()).pattern("I").pattern("I").pattern("S").define('I', ItemsTC.thaumiumIngot).define('S', Items.STICK).unlockedBy("has_thaumium", has(ItemsTC.thaumiumIngot)).save(pFinishedRecipeConsumer);
        planksFromLog(pFinishedRecipeConsumer, BlocksTC.plankSilverwood, ItemTagsTC.SILVERWOOD_LOGS);
        ShapedRecipeBuilder.shaped(BlocksTC.stoneArcane, 9).group(ConfigRecipes.defaultGroup).pattern("KKK").pattern("KCK").pattern("KKK").define('K', Items.STONE).define('C', ItemsTC.crystalEssence).unlockedBy("has_crystal", has(ItemsTC.crystalEssence)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(BlocksTC.stoneArcaneBrick, 4).group(ConfigRecipes.defaultGroup).pattern("KK").pattern("KK").define('K', BlocksTC.stoneArcane).unlockedBy("has_stone_arcane", has(BlocksTC.stoneArcane)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(ItemsTC.phial, 8).group(ConfigRecipes.defaultGroup).pattern(" C ").pattern("G G").pattern(" G ").define('G', Blocks.GLASS).define('C', Items.CLAY_BALL).unlockedBy("has_glass", has(Blocks.GLASS)).unlockedBy("has_clay", has(Items.CLAY)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(BlocksTC.tableWood).group(ConfigRecipes.defaultGroup).pattern("SSS").pattern("W W").define('S', ItemTags.WOODEN_SLABS).define('W', ItemTags.PLANKS).unlockedBy("has_table_wood", has(BlocksTC.tableWood)).save(pFinishedRecipeConsumer);
        String inkwellGroup = "thaumcraft:inkwell";
        ShapelessRecipeBuilder.shapeless(ItemsTC.scribingTools).group(inkwellGroup).requires(ItemsTC.phial).requires(Items.FEATHER).requires(Tags.Items.DYES_BLACK).unlockedBy("has_dye", has(Tags.Items.DYES_BLACK)).save(pFinishedRecipeConsumer, "thaumcraft:scribingtoolscraft1");
        ShapelessRecipeBuilder.shapeless(ItemsTC.scribingTools).group(inkwellGroup).requires(Items.GLASS_BOTTLE).requires(Items.FEATHER).requires(Tags.Items.DYES_BLACK).unlockedBy("has_dye", has(Tags.Items.DYES_BLACK)).save(pFinishedRecipeConsumer, "thaumcraft:scribingtoolscraft2");
        ShapelessRecipeBuilder.shapeless(ItemsTC.scribingTools).group(inkwellGroup).requires(ItemsTC.scribingTools).requires(Tags.Items.DYES_BLACK).unlockedBy("has_scribingTools", has(ItemsTC.scribingTools)).save(pFinishedRecipeConsumer, "thaumcraft:scribingtoolsrefill");
    }

    public static void compileGroups() {
        for (Recipe<?> recipe : Minecraft.getInstance().getConnection().getRecipeManager().getRecipes()) {
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
