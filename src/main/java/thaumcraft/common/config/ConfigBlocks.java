package thaumcraft.common.config;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.world.ore.BlockCrystal;
import thaumcraft.common.blocks.world.ore.ShardType;

public class ConfigBlocks {
    public static void initBlocks(RegisterEvent.RegisterHelper<Block> event) {
        BlocksTC.crystalAir = registerBlock("crystal_aer", new BlockCrystal(Aspect.AIR));
        BlocksTC.crystalFire = registerBlock("crystal_ignis", new BlockCrystal(Aspect.FIRE));
        BlocksTC.crystalWater = registerBlock("crystal_aqua", new BlockCrystal(Aspect.WATER));
        BlocksTC.crystalEarth = registerBlock("crystal_terra", new BlockCrystal(Aspect.EARTH));
        BlocksTC.crystalOrder = registerBlock("crystal_ordo", new BlockCrystal(Aspect.ORDER));
        BlocksTC.crystalEntropy = registerBlock("crystal_perditio", new BlockCrystal(Aspect.ENTROPY));
        BlocksTC.crystalTaint = registerBlock("crystal_vitium", new BlockCrystal(Aspect.FLUX));

        ShardType.AIR.setOre(BlocksTC.crystalAir);
        ShardType.FIRE.setOre(BlocksTC.crystalFire);
        ShardType.WATER.setOre(BlocksTC.crystalWater);
        ShardType.EARTH.setOre(BlocksTC.crystalEarth);
        ShardType.ORDER.setOre(BlocksTC.crystalOrder);
        ShardType.ENTROPY.setOre(BlocksTC.crystalEntropy);
        ShardType.FLUX.setOre(BlocksTC.crystalTaint);
    }

    private static Block registerBlock(String name, Block block) {
        return registerBlock(name, block, new BlockItem(block, new Item.Properties().tab(ConfigItems.TABTC)));
    }

    private static Block registerBlock(String name, Block block, BlockItem itemBlock) {
        ForgeRegistries.BLOCKS.register(name, block);
        ForgeRegistries.ITEMS.register(name, itemBlock);
        return block;
    }
}