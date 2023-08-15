package thaumcraft.proxies;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public interface IProxy {
    void preInit(FMLCommonSetupEvent event);
    void init(FMLCommonSetupEvent event);
}
