package thaumcraft.proxies;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.lib.network.PacketHandler;

public class CommonProxy implements IProxy {
    @Override
    public void preInit(FMLCommonSetupEvent event) {
        PacketHandler.preInit();
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        ConfigResearch.init();
    }

    @Override
    public void postInit(FMLCommonSetupEvent event) {
        ConfigResearch.postInit();
    }
}
