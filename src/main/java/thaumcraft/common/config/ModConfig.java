package thaumcraft.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {
    private static final ForgeConfigSpec CONFIG_WORLD;

    public static class CONFIG_WORLD {
        public static ForgeConfigSpec.IntValue oreDensity;

        public CONFIG_WORLD(ForgeConfigSpec.Builder builder) {
            oreDensity = builder.comment("The % of normal ore amounts that will be spawned. For example 50 will spawn half", "the ores while 200 will spawn double. Default 100")
                    .worldRestart()
                    .defineInRange("oreDensity", 100, 1, 500);
        }
    }

    public static void register(ModLoadingContext context) {
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, CONFIG_WORLD, "thaumcraft_world.toml");
    }

    static {
        Pair<CONFIG_WORLD, ForgeConfigSpec> world_pair = new ForgeConfigSpec.Builder().configure(CONFIG_WORLD::new);
        CONFIG_WORLD = world_pair.getRight();
    }
}
