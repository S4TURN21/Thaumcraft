package thaumcraft.proxies;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thaumcraft.client.renderers.block.CrystalModel;

import static thaumcraft.Thaumcraft.MODID;

public class ProxyBlock {
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class BakeBlockEventHandler {
        @SubscribeEvent
        public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
            event.register("crystal", CrystalModel.Loader.INSTANCE);
        }
    }
}
