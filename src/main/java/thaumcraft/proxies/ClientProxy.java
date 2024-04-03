package thaumcraft.proxies;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thaumcraft.client.ColorHandler;

public class ClientProxy extends CommonProxy {
    ProxyEntities proxyEntities;

    public ClientProxy() {
        this.proxyEntities = new ProxyEntities();
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        super.init(event);
        ColorHandler.registerColourHandlers();
        this.proxyEntities.setupEntityRenderers();
    }

    @Override
    public Level getClientWorld() {
        return Minecraft.getInstance().level;
    }

}
