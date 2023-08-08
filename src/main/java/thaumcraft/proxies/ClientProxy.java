package thaumcraft.proxies;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thaumcraft.client.ColorHandler;

public class ClientProxy extends CommonProxy {
    @Override
    public void init(FMLCommonSetupEvent event) {
        super.init(event);
        ColorHandler.registerColourHandlers();
    }
}
