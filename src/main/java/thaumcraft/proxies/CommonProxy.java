package thaumcraft.proxies;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.lib.BehaviorDispenseAlumetum;
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
        DispenserBlock.registerBehavior(ItemsTC.alumentum, new BehaviorDispenseAlumetum());
        ConfigResearch.init();
    }

    @Override
    public void postInit(FMLCommonSetupEvent event) {
        ConfigResearch.postInit();
    }

    @Override
    public Level getClientWorld() {
        return null;
    }
}
