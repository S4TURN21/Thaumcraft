package thaumcraft.common.items;

import net.minecraft.world.item.Item;
import thaumcraft.common.config.ConfigItems;

public class ItemTCBase extends Item {
    public ItemTCBase(Properties pProperties) {
        super(pProperties.tab(ConfigItems.TABTC));
    }
}
