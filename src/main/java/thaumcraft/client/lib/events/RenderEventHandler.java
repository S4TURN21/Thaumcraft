package thaumcraft.client.lib.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class RenderEventHandler {
    public static RenderEventHandler INSTANCE = new RenderEventHandler();
    @OnlyIn(Dist.CLIENT)
    public static HudHandler hudHandler = new HudHandler();

    @SubscribeEvent
    public static void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {

        } else {
            Minecraft mc = Minecraft.getInstance();
            if (Minecraft.getInstance().getCameraEntity() instanceof Player) {
                Player player = (Player) Minecraft.getInstance().getCameraEntity();
                long time = System.currentTimeMillis();
                if (player != null) {
                    RenderEventHandler.hudHandler.renderHuds(new PoseStack(), mc, event.renderTickTime, player, time);
                }
            }
        }
    }
}