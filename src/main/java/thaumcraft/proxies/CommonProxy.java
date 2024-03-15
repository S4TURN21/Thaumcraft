package thaumcraft.proxies;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.lib.InternalMethodHandler;
import thaumcraft.common.lib.network.PacketHandler;

public class CommonProxy implements IProxy {

    private final ProxyGUI proxyGUI;

    public CommonProxy() {
        proxyGUI = new ProxyGUI();
    }

    @Override
    public void preInit(FMLCommonSetupEvent event) {
        ThaumcraftApi.internalMethods = new InternalMethodHandler();
        proxyGUI.registerGuiHandler();
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
