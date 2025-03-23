package thaumcraft.common.config;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.internal.CommonInternals;

public class ConfigAspects {
    public static void postInit() {
        CommonInternals.objectTags.clear();
        registerItemAspects();
    }

    private static void registerItemAspects() {
        ThaumcraftApi.registerObjectTag(BlockTags.LAPIS_ORES, new AspectList().add(Aspect.EARTH, 5).add(Aspect.SENSES, 15));
        ThaumcraftApi.registerObjectTag(BlockTags.DIAMOND_ORES, new AspectList().add(Aspect.EARTH, 5).add(Aspect.DESIRE, 15).add(Aspect.CRYSTAL, 15));
        ThaumcraftApi.registerObjectTag(Tags.Items.GEMS_DIAMOND, new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.DESIRE, 15));
        ThaumcraftApi.registerObjectTag(BlockTags.REDSTONE_ORES, new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENERGY, 15));
//        RedStoneOreBlock.LIT
        ThaumcraftApi.registerObjectTag(BlockTags.EMERALD_ORES, new AspectList().add(Aspect.EARTH, 5).add(Aspect.DESIRE, 10).add(Aspect.CRYSTAL, 15));
        ThaumcraftApi.registerObjectTag(Tags.Items.GEMS_EMERALD, new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(Tags.Blocks.ORES_QUARTZ, new AspectList().add(Aspect.EARTH, 5).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag(Tags.Items.GEMS_QUARTZ, new AspectList().add(Aspect.CRYSTAL, 5));
        ThaumcraftApi.registerObjectTag(Tags.Blocks.ORES_IRON, new AspectList().add(Aspect.EARTH, 5).add(Aspect.METAL, 15));
        ThaumcraftApi.registerObjectTag(Tags.Items.INGOTS_IRON, new AspectList().add(Aspect.METAL, 15));
        ThaumcraftApi.registerObjectTag(Tags.Blocks.ORES_GOLD, new AspectList().add(Aspect.EARTH, 5).add(Aspect.METAL, 10).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(Tags.Items.INGOTS_GOLD, new AspectList().add(Aspect.METAL, 10).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(Tags.Blocks.ORES_COAL, new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENERGY, 15).add(Aspect.FIRE, 15));
        ThaumcraftApi.registerObjectTag(ItemTags.COALS, new AspectList().add(Aspect.ENERGY, 10).add(Aspect.FIRE, 10));
        ThaumcraftApi.registerObjectTag(Tags.Items.DUSTS_REDSTONE, new AspectList().add(Aspect.ENERGY, 10));
        ThaumcraftApi.registerObjectTag(Tags.Items.DUSTS_GLOWSTONE, new AspectList().add(Aspect.SENSES, 5).add(Aspect.LIGHT, 10));
//        glowstone
        ThaumcraftApi.registerObjectTag(Tags.Blocks.ORES_COPPER, new AspectList().add(Aspect.EARTH, 5).add(Aspect.METAL, 10).add(Aspect.EXCHANGE, 5));
        ThaumcraftApi.registerObjectTag(Tags.Items.INGOTS_COPPER, new AspectList().add(Aspect.METAL, 10).add(Aspect.EXCHANGE, 5));
        ThaumcraftApi.registerObjectTag(BlockTags.BASE_STONE_OVERWORLD, new AspectList().add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag(BlockTags.SAND, new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5));
        ThaumcraftApi.registerObjectTag(Tags.Blocks.COBBLESTONE, new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag(BlockTags.DIRT, new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 2));
        ThaumcraftApi.registerObjectTag(BlockTags.WOOL, new AspectList().add(Aspect.BEAST, 15).add(Aspect.CRAFT, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.TRIPWIRE_HOOK, 1), new AspectList().add(Aspect.SENSES, 5).add(Aspect.MECHANISM, 5).add(Aspect.TRAP, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.TORCH, 1), new AspectList().add(Aspect.LIGHT, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalAir, 1), new AspectList().add(Aspect.AIR, 15).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalFire, 1), new AspectList().add(Aspect.FIRE, 15).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalWater, 1), new AspectList().add(Aspect.WATER, 15).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalEarth, 1), new AspectList().add(Aspect.EARTH, 15).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalOrder, 1), new AspectList().add(Aspect.ORDER, 15).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalEntropy, 1), new AspectList().add(Aspect.ENTROPY, 15).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalTaint, 1), new AspectList().add(Aspect.FLUX, 15).add(Aspect.CRYSTAL, 10));
    }
}
