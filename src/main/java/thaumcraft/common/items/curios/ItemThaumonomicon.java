package thaumcraft.common.items.curios;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.client.gui.GuiResearchBrowser;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.research.ResearchManager;

import java.util.Collection;

public class ItemThaumonomicon extends ItemTCBase {
    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide) {
            Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
            for (ResearchCategory cat : rc) {
                Collection<ResearchEntry> rl = cat.research.values();
                for (ResearchEntry ri : rl) {
                    if (ThaumcraftCapabilities.knowsResearch(player, ri.getKey()) && ri.getSiblings() != null) {
                        for (String sib : ri.getSiblings()) {
                            if (!ThaumcraftCapabilities.knowsResearch(player, sib)) {
                                ResearchManager.completeResearch(player, sib);
                            }
                        }
                    }
                }
            }
            ThaumcraftCapabilities.getKnowledge(player).sync((ServerPlayer) player);
        } else {
            world.playLocalSound(player.getX(),player.getY(),player.getZ(), SoundsTC.page, SoundSource.PLAYERS, 1.0f, 1.0f, false);
        }

        if (world.isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft.getInstance().setScreen(new GuiResearchBrowser());
            });
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public Rarity getRarity(ItemStack itemstack) {
        return (itemstack.getDamageValue() != 1) ? Rarity.UNCOMMON : Rarity.EPIC;
    }
}