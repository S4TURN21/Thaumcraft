package thaumcraft.proxies;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import static thaumcraft.Thaumcraft.MODID;

public class ProxyBlock {
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class BakeBlockEventHandler {

    }
}
