package thaumcraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    }
}
