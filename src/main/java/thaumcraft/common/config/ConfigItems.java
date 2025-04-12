package thaumcraft.common.config;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegisterEvent;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.items.armor.ItemGoggles;
import thaumcraft.common.items.armor.ItemThaumiumArmor;
import thaumcraft.common.items.consumables.ItemAlumentum;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.items.curios.ItemCelestialNotes;
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
        event.register("celestial_notes_sun", (ItemsTC.celestialNotesSun = new ItemCelestialNotes(new Item.Properties())));
        event.register("celestial_notes_stars_1", (ItemsTC.celestialNotesStars1 = new ItemCelestialNotes(new Item.Properties())));
        event.register("celestial_notes_stars_2", (ItemsTC.celestialNotesStars2 = new ItemCelestialNotes(new Item.Properties())));
        event.register("celestial_notes_stars_3", (ItemsTC.celestialNotesStars3 = new ItemCelestialNotes(new Item.Properties())));
        event.register("celestial_notes_stars_4", (ItemsTC.celestialNotesStars4 = new ItemCelestialNotes(new Item.Properties())));
        event.register("celestial_notes_moon_1", (ItemsTC.celestialNotesMoon1 = new ItemCelestialNotes(new Item.Properties())));
        event.register("celestial_notes_moon_2", (ItemsTC.celestialNotesMoon2 = new ItemCelestialNotes(new Item.Properties())));
        event.register("celestial_notes_moon_3", (ItemsTC.celestialNotesMoon3 = new ItemCelestialNotes(new Item.Properties())));
        event.register("celestial_notes_moon_4", (ItemsTC.celestialNotesMoon4 = new ItemCelestialNotes(new Item.Properties())));
        event.register("celestial_notes_moon_5", (ItemsTC.celestialNotesMoon5 = new ItemCelestialNotes(new Item.Properties())));
        event.register("celestial_notes_moon_6", (ItemsTC.celestialNotesMoon6 = new ItemCelestialNotes(new Item.Properties())));
        event.register("celestial_notes_moon_7", (ItemsTC.celestialNotesMoon7 = new ItemCelestialNotes(new Item.Properties())));
        event.register("celestial_notes_moon_8", (ItemsTC.celestialNotesMoon8 = new ItemCelestialNotes(new Item.Properties())));
        event.register("nugget_quartz", (ItemsTC.nuggetQuartz = new ItemTCBase(new Item.Properties())));
        event.register("plate_brass", (ItemsTC.brassPlate = new ItemTCBase(new Item.Properties())));
        event.register("plate_thaumium", (ItemsTC.thaumiumPlate = new ItemTCBase(new Item.Properties())));
        event.register("filter", (ItemsTC.filter = new ItemTCBase(new Item.Properties())));
        event.register("salis_mundus", (ItemsTC.salisMundus = new ItemMagicDust(new Item.Properties().rarity(Rarity.UNCOMMON))));
        event.register("crystal_essence", (ItemsTC.crystalEssence = new ItemCrystalEssence()));
        event.register("phial", (ItemsTC.phial = new ItemPhial()));
        event.register("alumentum", (ItemsTC.alumentum = new ItemAlumentum(new Item.Properties())));
        event.register("ingot_thaumium", (ItemsTC.thaumiumIngot = new ItemTCBase(new Item.Properties())));
        event.register("ingot_brass", (ItemsTC.brassIngot = new ItemTCBase(new Item.Properties())));
        event.register("scribing_tools", (ItemsTC.scribingTools = new ItemScribingTools(new Item.Properties().stacksTo(1).durability(100))));
        event.register("thaumometer", (ItemsTC.thaumometer = new ItemThaumometer(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))));
        event.register("thaumium_axe", (ItemsTC.thaumiumAxe = new AxeItem(ThaumcraftMaterials.TOOLMAT_THAUMIUM, 8.0f, -3.0f, new Item.Properties().tab(ConfigItems.TABTC))));
        event.register("thaumium_sword", (ItemsTC.thaumiumSword = new SwordItem(ThaumcraftMaterials.TOOLMAT_THAUMIUM, 3, -2.4F, new Item.Properties().tab(ConfigItems.TABTC))));
        event.register("thaumium_shovel", (ItemsTC.thaumiumShovel = new ShovelItem(ThaumcraftMaterials.TOOLMAT_THAUMIUM, 1.5F, -3.0F, new Item.Properties().tab(ConfigItems.TABTC))));
        event.register("goggles", (ItemsTC.goggles = new ItemGoggles(new Item.Properties().durability(350).tab(ConfigItems.TABTC).rarity(Rarity.RARE))));
        event.register("thaumium_helm", (ItemsTC.thaumiumHelm = new ItemThaumiumArmor(ThaumcraftMaterials.ARMORMAT_THAUMIUM, EquipmentSlot.HEAD, new Item.Properties().tab(ConfigItems.TABTC).rarity(Rarity.UNCOMMON))));
        event.register("thaumium_chest", (ItemsTC.thaumiumChest = new ItemThaumiumArmor(ThaumcraftMaterials.ARMORMAT_THAUMIUM, EquipmentSlot.CHEST, new Item.Properties().tab(ConfigItems.TABTC).rarity(Rarity.UNCOMMON))));
        event.register("thaumium_legs", (ItemsTC.thaumiumLegs = new ItemThaumiumArmor(ThaumcraftMaterials.ARMORMAT_THAUMIUM, EquipmentSlot.LEGS, new Item.Properties().tab(ConfigItems.TABTC).rarity(Rarity.UNCOMMON))));
        event.register("thaumium_boots", (ItemsTC.thaumiumBoots = new ItemThaumiumArmor(ThaumcraftMaterials.ARMORMAT_THAUMIUM, EquipmentSlot.FEET, new Item.Properties().tab(ConfigItems.TABTC).rarity(Rarity.UNCOMMON))));
    }
}
