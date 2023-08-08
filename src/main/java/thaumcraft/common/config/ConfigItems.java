package thaumcraft.common.config;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegisterEvent;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.resources.ItemCrystalEssence;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ConfigItems {
    public static CreativeModeTab TABTC = new CreativeTabThaumcraft("thaumcraft");
    public static void initItems(RegisterEvent.RegisterHelper<Item> event) {
        event.register("crystal_essence", (ItemsTC.crystalEssence = new ItemCrystalEssence()));
    }
}
