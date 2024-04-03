package thaumcraft.proxies;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thaumcraft.client.ColorHandler;

public class ClientProxy extends CommonProxy {
    ProxyEntities proxyEntities;
    ProxyBER proxyBER;

    public ClientProxy() {
        this.proxyEntities = new ProxyEntities();
        this.proxyBER = new ProxyBER();
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        super.init(event);
        ColorHandler.registerColourHandlers();
        this.proxyEntities.setupEntityRenderers();
        this.proxyBER.setupBER();
    }

    @Override
    public Level getClientWorld() {
        return Minecraft.getInstance().level;
    }

}
