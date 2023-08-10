package thaumcraft;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.world.ThaumcraftWorldGenerator;
import thaumcraft.proxies.ClientProxy;
import thaumcraft.proxies.IProxy;
import thaumcraft.proxies.ServerProxy;

@Mod(Thaumcraft.MODID)
public class Thaumcraft
{
    public static final String MODID = "thaumcraft";
    public static IProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public Thaumcraft()
    {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        ModConfig.register(modLoadingContext);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(Thaumcraft.proxy::init);
    }
}
