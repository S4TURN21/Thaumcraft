package thaumcraft.common.config;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public class ConfigBlocks {
    public static void initBlocks(RegisterEvent.RegisterHelper<Block> event) {
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