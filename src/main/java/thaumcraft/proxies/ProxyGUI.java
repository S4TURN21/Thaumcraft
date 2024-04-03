package thaumcraft.proxies;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ForgeRegistries;
import thaumcraft.Thaumcraft;
import thaumcraft.client.gui.ContainerArcaneWorkbench;
import thaumcraft.client.gui.GuiArcaneWorkbench;
import thaumcraft.client.gui.GuiResearchTable;
import thaumcraft.common.container.ContainerResearchTable;

public class ProxyGUI {
    public void registerGuiHandler() {
        MenuScreens.register((MenuType<ContainerArcaneWorkbench>) ForgeRegistries.MENU_TYPES.getValue(new ResourceLocation(Thaumcraft.MODID, "arcane_workbench")), GuiArcaneWorkbench::new);
        MenuScreens.register((MenuType<ContainerResearchTable>) ForgeRegistries.MENU_TYPES.getValue(new ResourceLocation(Thaumcraft.MODID, "research_table")), GuiResearchTable::new);
    }
}
