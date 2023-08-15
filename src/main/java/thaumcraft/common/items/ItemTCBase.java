package thaumcraft.common.items;

import net.minecraft.world.item.Item;
import thaumcraft.common.config.ConfigItems;

public class ItemTCBase extends Item {
    public ItemTCBase() {
        super(new Item.Properties().tab(ConfigItems.TABTC));
    }
}
