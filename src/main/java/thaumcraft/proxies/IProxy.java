package thaumcraft.proxies;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public interface IProxy {
    void init(FMLCommonSetupEvent event);
}
