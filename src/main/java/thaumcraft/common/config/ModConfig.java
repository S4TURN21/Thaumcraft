package thaumcraft.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {
    private static final ForgeConfigSpec CONFIG_GRAPHICS;
    private static final ForgeConfigSpec CONFIG_WORLD;
    private static final ForgeConfigSpec CONFIG_MISC;

    public static class CONFIG_GRAPHICS {
        public static ForgeConfigSpec.BooleanValue largeTagText;

        public CONFIG_GRAPHICS(ForgeConfigSpec.Builder builder) {
            largeTagText = builder.comment("Setting this to true will make the amount text in aspect tags twice as large. Useful for certain resolutions and custom fonts.")
                    .define("largeTagText", false);
        }
    }

    public static class CONFIG_WORLD {
        public static ForgeConfigSpec.IntValue oreDensity;
        public static ForgeConfigSpec.BooleanValue generateCrystals;

        public CONFIG_WORLD(ForgeConfigSpec.Builder builder) {
            oreDensity = builder.comment("The % of normal ore amounts that will be spawned. For example 50 will spawn half", "the ores while 200 will spawn double. Default 100")
                    .worldRestart()
                    .defineInRange("oreDensity", 100, 1, 500);
            generateCrystals = builder.define("generateCrystals", true);
        }
    }

    public static class CONFIG_MISC {
        public static ForgeConfigSpec.BooleanValue noSleep;
        public static ForgeConfigSpec.BooleanValue wussMode;

        public CONFIG_MISC(ForgeConfigSpec.Builder builder) {
            noSleep = builder.comment("Setting this to true will make you get the recipe book for salis mundus without having to sleep first.")
                    .define("noSleep", false);
            wussMode = builder.comment("Setting this to true disables Warp, Taint spread and similar mechanics. You wuss.")
                    .define("wussMode", false);
        }
    }

    public static void register(ModLoadingContext context) {
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, CONFIG_GRAPHICS, "thaumcraft_graphics.toml");
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, CONFIG_WORLD, "thaumcraft_world.toml");
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, CONFIG_MISC, "thaumcraft_misc.toml");
    }

    static {
        Pair<CONFIG_GRAPHICS, ForgeConfigSpec> graphics_pair = new ForgeConfigSpec.Builder().configure(CONFIG_GRAPHICS::new);
        CONFIG_GRAPHICS = graphics_pair.getRight();
        Pair<CONFIG_WORLD, ForgeConfigSpec> world_pair = new ForgeConfigSpec.Builder().configure(CONFIG_WORLD::new);
        CONFIG_WORLD = world_pair.getRight();
        Pair<CONFIG_MISC, ForgeConfigSpec> misc_pair = new ForgeConfigSpec.Builder().configure(CONFIG_MISC::new);
        CONFIG_MISC = misc_pair.getRight();
    }
}
