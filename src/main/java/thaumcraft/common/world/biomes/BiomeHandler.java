package thaumcraft.common.world.biomes;

import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;
import thaumcraft.api.aspects.Aspect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BiomeHandler {
    public static HashMap<TagKey<Biome>, List> biomeInfo = new HashMap<>();

    public static void registerBiomeInfo(TagKey<Biome> type, float auraLevel, Aspect tag, boolean greatwood, float greatwoodchance) {
        BiomeHandler.biomeInfo.put(type, Arrays.asList(auraLevel, tag, greatwood, greatwoodchance));
    }

    public static float getBiomeAuraModifier(Holder<Biome> biome) {
        try {
            Set<TagKey<Biome>> types = biome.tags().collect(Collectors.toSet());
            float average = 0.0f;
            int count = 0;
            for (TagKey<Biome> type : types) {
                average += (float) BiomeHandler.biomeInfo.get(type).get(0);
                ++count;
            }
            return average / count;
        } catch (Exception ex) {
            return 0.5f;
        }
    }

    public static Aspect getRandomBiomeTag(Holder<Biome> biome, RandomSource random) {
        try {
            Set<TagKey<Biome>> types = biome.tags().collect(Collectors.toSet());
            TagKey<Biome> type = (TagKey<Biome>) (biome.tags().toArray()[random.nextInt(types.size())]);
            return (Aspect) BiomeHandler.biomeInfo.get(type).get(1);
        } catch (Exception ex) {
            return null;
        }
    }

    public static void registerBiomes() {
        registerBiomeInfo(Tags.Biomes.IS_WATER, 0.33f, Aspect.WATER, false, 0.0f);
        registerBiomeInfo(BiomeTags.IS_OCEAN, 0.33f, Aspect.WATER, false, 0.0f);
        registerBiomeInfo(BiomeTags.IS_RIVER, 0.4f, Aspect.WATER, false, 0.0f);
        registerBiomeInfo(Tags.Biomes.IS_WET, 0.4f, Aspect.WATER, false, 0.0f);
        registerBiomeInfo(Tags.Biomes.IS_LUSH, 0.5f, Aspect.WATER, true, 0.5f);
        registerBiomeInfo(Tags.Biomes.IS_HOT, 0.33f, Aspect.FIRE, false, 0.0f);
        registerBiomeInfo(Tags.Biomes.IS_DRY, 0.25f, Aspect.FIRE, false, 0.0f);
        registerBiomeInfo(BiomeTags.IS_NETHER, 0.125f, Aspect.FIRE, false, 0.0f);
        registerBiomeInfo(BiomeTags.IS_BADLANDS, 0.33f, Aspect.FIRE, false, 0.0f);
        registerBiomeInfo(Tags.Biomes.IS_SPOOKY, 0.5f, Aspect.FIRE, false, 0.0f);
        registerBiomeInfo(Tags.Biomes.IS_DENSE, 0.4f, Aspect.ORDER, false, 0.0f);
        registerBiomeInfo(Tags.Biomes.IS_SNOWY, 0.25f, Aspect.ORDER, false, 0.0f);
        registerBiomeInfo(Tags.Biomes.IS_COLD, 0.25f, Aspect.ORDER, false, 0.0f);
        registerBiomeInfo(Tags.Biomes.IS_MUSHROOM, 0.75f, Aspect.ORDER, false, 0.0f);
        registerBiomeInfo(Tags.Biomes.IS_MAGICAL, 0.75f, Aspect.ORDER, true, 1.0f);
        registerBiomeInfo(Tags.Biomes.IS_CONIFEROUS, 0.33f, Aspect.EARTH, true, 0.2f);
        registerBiomeInfo(BiomeTags.IS_FOREST, 0.5f, Aspect.EARTH, true, 1.0f);
        registerBiomeInfo(Tags.Biomes.IS_SANDY, 0.25f, Aspect.EARTH, false, 0.0f);
        registerBiomeInfo(BiomeTags.IS_BEACH, 0.3f, Aspect.EARTH, false, 0.0f);
        registerBiomeInfo(BiomeTags.IS_JUNGLE, 0.6f, Aspect.EARTH, false, 0.0f);
        registerBiomeInfo(BiomeTags.IS_SAVANNA, 0.25f, Aspect.AIR, true, 0.2f);
        registerBiomeInfo(Tags.Biomes.IS_MOUNTAIN, 0.3f, Aspect.AIR, false, 0.0f);
//        registerBiomeInfo(Tags.Biomes.IS_HILLS, 0.33f, Aspect.AIR, false, 0.0f);
        registerBiomeInfo(Tags.Biomes.IS_PLAINS, 0.3f, Aspect.AIR, true, 0.2f);
//        registerBiomeInfo(Tags.Biomes.IS_THE_END, 0.125f, Aspect.AIR, false, 0.0f);
        registerBiomeInfo(Tags.Biomes.IS_DRY, 0.125f, Aspect.ENTROPY, false, 0.0f);
        registerBiomeInfo(Tags.Biomes.IS_SPARSE, 0.2f, Aspect.ENTROPY, false, 0.0f);
        registerBiomeInfo(Tags.Biomes.IS_SWAMP, 0.5f, Aspect.ENTROPY, true, 0.2f);
        registerBiomeInfo(Tags.Biomes.IS_WASTELAND, 0.125f, Aspect.ENTROPY, false, 0.0f);
        registerBiomeInfo(Tags.Biomes.IS_DEAD, 0.1f, Aspect.ENTROPY, false, 0.0f);
    }
}
