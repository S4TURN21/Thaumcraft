package thaumcraft.common.world;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thaumcraft.Thaumcraft;
import thaumcraft.common.world.objects.CrystalFeature;

import java.util.List;

public class ThaumcraftWorldGenerator {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, Thaumcraft.MODID);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, Thaumcraft.MODID);
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, Thaumcraft.MODID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CRYSTAL = FEATURES.register("crystal",
            () -> new CrystalFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<ConfiguredFeature<?, ?>> CRYSTAL_FEATURE = CONFIGURED_FEATURES.register("crystal_feature",
            () -> new ConfiguredFeature<>(ThaumcraftWorldGenerator.CRYSTAL.get(), NoneFeatureConfiguration.INSTANCE));
    public static final RegistryObject<PlacedFeature> CRYSTAL_PLACED = PLACED_FEATURES.register("crystal_placed",
            () -> new PlacedFeature(CRYSTAL_FEATURE.getHolder().get(), List.of(BiomeFilter.biome())));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
        CONFIGURED_FEATURES.register(eventBus);
        PLACED_FEATURES.register(eventBus);
    }
}