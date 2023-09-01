package thaumcraft.common.config;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegisterEvent;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.curios.ItemThaumonomicon;
import thaumcraft.common.items.resources.ItemCrystalEssence;
import thaumcraft.common.items.resources.ItemMagicDust;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ConfigItems {
    public static ItemStack startBook = new ItemStack(Items.WRITTEN_BOOK);
    public static CreativeModeTab TABTC = new CreativeTabThaumcraft("thaumcraft");

    public static void initMisc() {
        CompoundTag contents = new CompoundTag();
        contents.putInt("generation", 3);
        contents.putString("title", "Strange Dreams");
        ConfigItems.startBook.getOrCreateTagElement("display").putString("Name", Component.Serializer.toJson(Component.translatable("book.start.title")));
        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.translatable("book.start.1"))));
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.translatable("book.start.2"))));
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.translatable("book.start.3"))));
        contents.put("pages", pages);
        ConfigItems.startBook.setTag(contents);
    }

    public static void initItems(RegisterEvent.RegisterHelper<Item> event) {
        event.register("thaumonomicon", (ItemsTC.thaumonomicon = new ItemThaumonomicon()));
        event.register("crystal_essence", (ItemsTC.crystalEssence = new ItemCrystalEssence()));
        event.register("salis_mundus", (ItemsTC.salisMundus = new ItemMagicDust()));
    }
}
