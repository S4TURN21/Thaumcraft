package thaumcraft.common.items.curios;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import thaumcraft.client.gui.GuiResearchBrowser;
import thaumcraft.common.items.ItemTCBase;

public class ItemThaumonomicon extends ItemTCBase {
    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}