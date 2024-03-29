package thaumcraft.common.config;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegisterEvent;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.items.curios.ItemThaumonomicon;
import thaumcraft.common.items.resources.ItemCrystalEssence;
import thaumcraft.common.items.resources.ItemMagicDust;
import thaumcraft.common.items.tools.ItemScribingTools;
import thaumcraft.common.items.tools.ItemThaumometer;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ConfigItems {
    public static ItemStack startBook = new ItemStack(Items.WRITTEN_BOOK);
    public static CreativeModeTab TABTC = new CreativeTabThaumcraft("thaumcraft");

    public static void initMisc() {
        CompoundTag contents = new CompoundTag();
        contents.putInt("generation", 3);
        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.translatable("book.start.1"))));
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.translatable("book.start.2"))));
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.translatable("book.start.3"))));
        contents.put("pages", pages);
        ConfigItems.startBook.setTag(contents);
    }

    public static void initItems(RegisterEvent.RegisterHelper<Item> event) {
        event.register("thaumonomicon", (ItemsTC.thaumonomicon = new ItemThaumonomicon(new Item.Properties().stacksTo(1))));
        event.register("salis_mundus", (ItemsTC.salisMundus = new ItemMagicDust(new Item.Properties().rarity(Rarity.UNCOMMON))));
        event.register("crystal_essence", (ItemsTC.crystalEssence = new ItemCrystalEssence()));
        event.register("phial", (ItemsTC.phial = new ItemPhial()));
        event.register("scribing_tools", (ItemsTC.scribingTools = new ItemScribingTools(new Item.Properties().stacksTo(1).durability(100))));
        event.register("thaumometer", (ItemsTC.thaumometer = new ItemThaumometer(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))));
    }
}
