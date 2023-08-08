package thaumcraft;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import thaumcraft.common.config.ConfigItems;

@Mod.EventBusSubscriber(modid = Thaumcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registrar {
    @SubscribeEvent
    public static void onRegisterEvent(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ITEMS, Registrar::registerItems);
    }

    private static void registerItems(RegisterEvent.RegisterHelper<Item> event) {
        ConfigItems.initItems(event);
    }
}
