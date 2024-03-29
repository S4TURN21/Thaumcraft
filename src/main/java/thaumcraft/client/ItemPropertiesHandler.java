package thaumcraft.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;

public class ItemPropertiesHandler {
    public static void registerItemProperties() {
        ItemProperties.register(ItemsTC.phial, new ResourceLocation(Thaumcraft.MODID, "filled"), (stack, level, entity, seed) -> {
            ItemGenericEssentiaContainer item = (ItemGenericEssentiaContainer) stack.getItem();
            return item.getAspects(stack) != null && !item.getAspects(stack).aspects.isEmpty() ? 1.0f : 0.0f;
        });
    }
}
