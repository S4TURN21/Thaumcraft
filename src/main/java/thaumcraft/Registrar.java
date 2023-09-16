package thaumcraft;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigRecipes;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.world.biomes.BiomeHandler;

@Mod.EventBusSubscriber(modid = Thaumcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registrar {
    @SubscribeEvent
    public static void onRegisterEvent(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.BLOCKS, Registrar::registerBlocks);
        event.register(ForgeRegistries.Keys.ITEMS, Registrar::registerItems);
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, Registrar::registerVanillaRecipes);
        event.register(ForgeRegistries.Keys.BIOMES, Registrar::registerBiomes);
        event.register(ForgeRegistries.Keys.SOUND_EVENTS, Registrar::registerSounds);
        event.register(ForgeRegistries.Keys.ENTITY_TYPES, Registrar::registerEntities);
    }

    private static void registerBlocks(RegisterEvent.RegisterHelper<Block> event) {
        ConfigBlocks.initBlocks(event);
    }

    private static void registerItems(RegisterEvent.RegisterHelper<Item> event) {
        ConfigItems.initItems(event);
        ConfigItems.initMisc();
    }

    private static void registerEntities(RegisterEvent.RegisterHelper<EntityType<?>> event) {
        ConfigEntities.initEntities(event);
    }

    private static void registerVanillaRecipes(RegisterEvent.RegisterHelper<RecipeSerializer<?>> event) {
        ConfigRecipes.initializeNormalRecipes(event);
        ConfigRecipes.initializeCompoundRecipes();
    }

    private static void registerBiomes(RegisterEvent.RegisterHelper<Biome> event) {
        BiomeHandler.registerBiomes();
    }

    private static void registerSounds(RegisterEvent.RegisterHelper<SoundEvent> event) {
        SoundsTC.registerSounds(event);
        SoundsTC.registerSoundTypes();
    }
}
