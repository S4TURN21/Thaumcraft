package thaumcraft.common.items;

import net.minecraft.world.item.Item;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.common.config.ConfigItems;

public class ItemTCEssentiaContainer extends ItemGenericEssentiaContainer {
    public ItemTCEssentiaContainer(int base) {
        super(new Item.Properties().tab(ConfigItems.TABTC), base);
    }
}
