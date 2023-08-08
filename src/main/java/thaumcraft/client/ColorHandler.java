package thaumcraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;

@OnlyIn(Dist.CLIENT)
public class ColorHandler {
    public static void registerColourHandlers() {
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        ItemColors itemColors = Minecraft.getInstance().getItemColors();
        registerBlockColourHandlers(blockColors);
        registerItemColourHandlers(blockColors, itemColors);
    }

    private static void registerBlockColourHandlers(BlockColors blockColors) {

    }

    private static void registerItemColourHandlers(BlockColors blockColors, ItemColors itemColors) {
        ItemColor itemEssentiaColourHandler = (stack, tintIndex) -> {
            ItemGenericEssentiaContainer item = (ItemGenericEssentiaContainer)stack.getItem();
            try {
                if (item != null && item.getAspects(stack) != null) {
                    return item.getAspects(stack).getAspects()[0].getColor();
                }
            }
            catch (Exception ex) {}
            return 0XFFFFFF;
        };
        itemColors.register(itemEssentiaColourHandler, new Item[] { ItemsTC.crystalEssence });
    }
}
