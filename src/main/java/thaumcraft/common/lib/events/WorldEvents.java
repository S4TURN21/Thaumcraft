package thaumcraft.common.lib.events;

import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thaumcraft.common.config.ConfigAspects;
import thaumcraft.common.config.ConfigRecipes;
import thaumcraft.common.world.aura.AuraHandler;

@Mod.EventBusSubscriber
public class WorldEvents {
    private static boolean loaded;

    @SubscribeEvent
    public static void worldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof Level level) {
            if (!level.isClientSide) {
                AuraHandler.addAuraWorld(level.dimension());
            }
        }
    }

    @SubscribeEvent
    public static void recipesUpdated(RecipesUpdatedEvent event)
    {
        ConfigAspects.postInit();
        ConfigRecipes.compileGroups();
    }

    @SubscribeEvent
    public static void worldUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            if (level.isClientSide) {
                return;
            }
            AuraHandler.removeAuraWorld(level.dimension());
        }
    }
}
