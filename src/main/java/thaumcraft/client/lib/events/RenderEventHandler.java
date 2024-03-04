package thaumcraft.client.lib.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RenderEventHandler {
    public static RenderEventHandler INSTANCE = new RenderEventHandler();
    @OnlyIn(Dist.CLIENT)
    public static HudHandler hudHandler = new HudHandler();
}
