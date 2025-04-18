package thaumcraft.common.world;

import com.google.common.base.Suppliers;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thaumcraft.Thaumcraft;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.world.objects.CrystalFeature;
import thaumcraft.common.world.objects.SilverwoodTreeFeature;

import java.util.List;
import java.util.function.Supplier;

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

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SILVERWOOD = FEATURES.register("silverwood",
            () -> new SilverwoodTreeFeature(NoneFeatureConfiguration.CODEC, 7, 4));

    public static final RegistryObject<ConfiguredFeature<?, ?>> SILVERWOOD_FEATURE = CONFIGURED_FEATURES.register("silverwood_feature",
            () -> new ConfiguredFeature<>(ThaumcraftWorldGenerator.SILVERWOOD.get(), NoneFeatureConfiguration.INSTANCE));

    public static final Supplier<List<OreConfiguration.TargetBlockState>> OVERWORLD_CINNABAR_ORES = Suppliers.memoize(() -> List.of(
            OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, BlocksTC.oreCinnabar.defaultBlockState())));

    public static final RegistryObject<ConfiguredFeature<?, ?>> CINNABAR_ORE = CONFIGURED_FEATURES.register("cinnabar_ore",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(OVERWORLD_CINNABAR_ORES.get(), 3)));

    public static final RegistryObject<PlacedFeature> CINNABAR_ORE_PLACED = PLACED_FEATURES.register("cinnabar_ore_placed",
            () -> new PlacedFeature(CINNABAR_ORE.getHolder().get(), List.of(CountPlacement.of(18), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(51)), BiomeFilter.biome())));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
        CONFIGURED_FEATURES.register(eventBus);
        PLACED_FEATURES.register(eventBus);
    }
}