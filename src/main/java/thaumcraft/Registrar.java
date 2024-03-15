package thaumcraft;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.client.gui.ContainerArcaneWorkbench;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigRecipes;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.world.biomes.BiomeHandler;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = Thaumcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registrar {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(true, new RecipeProvider(generator) {
            @Override
            protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
                ConfigRecipes.initializeArcaneRecipes(pFinishedRecipeConsumer);
            }
        });
    }

    @SubscribeEvent
    public static void onRegisterEvent(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.BLOCKS, Registrar::registerBlocks);
        event.register(ForgeRegistries.Keys.ITEMS, Registrar::registerItems);
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, Registrar::registerVanillaRecipes);
        event.register(ForgeRegistries.Keys.BIOMES, Registrar::registerBiomes);
        event.register(ForgeRegistries.Keys.SOUND_EVENTS, Registrar::registerSounds);
        event.register(ForgeRegistries.Keys.ENTITY_TYPES, Registrar::registerEntities);
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, Registrar::registerRecipeSerializers);
        event.register(ForgeRegistries.Keys.MENU_TYPES, Registrar::registerMenuTypes);
    }

    private static void registerMenuTypes(RegisterEvent.RegisterHelper<MenuType<?>> event) {
        event.register("arcane_workbench", IForgeMenuType.create(ContainerArcaneWorkbench::new));
    }

    private static void registerRecipeSerializers(RegisterEvent.RegisterHelper<RecipeSerializer<?>> event) {
        event.register("arcane_shaped", new ShapedArcaneRecipe.Serializer());
    }

    private static void registerBlocks(RegisterEvent.RegisterHelper<Block> event) {
        ConfigBlocks.initBlocks(event);
        ConfigBlocks.initBlockEntities();
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
