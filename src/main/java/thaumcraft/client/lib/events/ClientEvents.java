package thaumcraft.client.lib.events;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thaumcraft.Thaumcraft;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = Thaumcraft.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void textureStitchEventPre(TextureStitchEvent.Pre event) {
            if (event.getAtlas().location() == TextureAtlas.LOCATION_BLOCKS) {
                event.addSprite(new ResourceLocation("thaumcraft", "research/quill"));
            }
        }
    }
}
