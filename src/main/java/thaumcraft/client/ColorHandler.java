package thaumcraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.world.ore.BlockCrystal;

@OnlyIn(Dist.CLIENT)
public class ColorHandler {
    public static void registerColourHandlers() {
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        ItemColors itemColors = Minecraft.getInstance().getItemColors();
        registerBlockColourHandlers(blockColors);
        registerItemColourHandlers(blockColors, itemColors);
    }

    private static void registerBlockColourHandlers(BlockColors blockColors) {
        BlockColor basicColourHandler = (state, blockAccess, pos, tintIndex) -> state.getBlock().getMapColor(state, blockAccess, pos, MaterialColor.SNOW).col;
        Block[] basicBlocks = new Block[BlocksTC.nitor.size()];
        int i = 0;
        for (Block b : BlocksTC.nitor.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        blockColors.register(basicColourHandler, basicBlocks);

        BlockColor crystalColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() instanceof BlockCrystal) {
                return ((BlockCrystal)state.getBlock()).aspect.getColor();
            }
            return 0xFFFFFF;
        };
        blockColors.register(crystalColourHandler, BlocksTC.crystalAir, BlocksTC.crystalEarth, BlocksTC.crystalFire, BlocksTC.crystalWater, BlocksTC.crystalEntropy, BlocksTC.crystalOrder, BlocksTC.crystalTaint);
    }

    private static void registerItemColourHandlers(BlockColors blockColors, ItemColors itemColors) {
        ItemColor itemBlockColourHandler = (stack, tintIndex) -> {
            BlockState state = ((BlockItem)stack.getItem()).getBlock().defaultBlockState();
            return blockColors.getColor(state, null, null, tintIndex);
        };
        Block[] basicBlocks = new Block[BlocksTC.nitor.size()];
        int i = 0;
        for (Block b : BlocksTC.nitor.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        itemColors.register(itemBlockColourHandler, basicBlocks);

        ItemColor itemEssentiaColourHandler = (stack, tintIndex) -> {
            ItemGenericEssentiaContainer item = (ItemGenericEssentiaContainer)stack.getItem();
            try {
                if (item.getAspects(stack) != null) {
                    return item.getAspects(stack).getAspects()[0].getColor();
                }
            }
            catch (Exception ex) {}
            return 0XFFFFFF;
        };
        itemColors.register(itemEssentiaColourHandler, new Item[] { ItemsTC.crystalEssence });

        ItemColor itemCrystalPlanterColourHandler = (stack, tintIndex) -> {
            Item item = stack.getItem();
            if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof BlockCrystal) {
                return ((BlockCrystal)((BlockItem)item).getBlock()).aspect.getColor();
            }
            return 0XFFFFFF;
        };
        itemColors.register(itemCrystalPlanterColourHandler, new Block[] { BlocksTC.crystalAir });
        itemColors.register(itemCrystalPlanterColourHandler, new Block[] { BlocksTC.crystalEarth });
        itemColors.register(itemCrystalPlanterColourHandler, new Block[] { BlocksTC.crystalFire });
        itemColors.register(itemCrystalPlanterColourHandler, new Block[] { BlocksTC.crystalWater });
        itemColors.register(itemCrystalPlanterColourHandler, new Block[] { BlocksTC.crystalEntropy });
        itemColors.register(itemCrystalPlanterColourHandler, new Block[] { BlocksTC.crystalOrder });
        itemColors.register(itemCrystalPlanterColourHandler, new Block[] { BlocksTC.crystalTaint });

        ItemColor itemEssentiaAltColourHandler = (stack, tintIndex) -> {
            ItemGenericEssentiaContainer item = (ItemGenericEssentiaContainer)stack.getItem();
            if (item.getAspects(stack) != null && tintIndex == 1) {
                return item.getAspects(stack).getAspects()[0].getColor();
            }
            return 0xFFFFFF;
        };
        itemColors.register(itemEssentiaAltColourHandler, ItemsTC.phial);
    }
}
