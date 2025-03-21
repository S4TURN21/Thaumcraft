package thaumcraft.common.lib;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.internal.IInternalMethodHandler;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXPollute;
import thaumcraft.common.lib.network.playerdata.PacketWarpMessage;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.world.aura.AuraHandler;

public class InternalMethodHandler implements IInternalMethodHandler {
    @Override
    public boolean addKnowledge(Player player, IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory field, int amount) {
        return amount != 0 && !player.level.isClientSide && ResearchManager.addKnowledge(player, type, field, amount);
    }

    @Override
    public void addWarpToPlayer(Player player, int amount, IPlayerWarp.EnumWarpType type) {
        if (amount == 0 || player.level.isClientSide) {
            return;
        }
        IPlayerWarp pw = ThaumcraftCapabilities.getWarp(player);
        int cur = pw.get(type);
        if (amount < 0 && cur + amount < 0) {
            amount = cur;
        }
        pw.add(type, amount);
        if (type == IPlayerWarp.EnumWarpType.PERMANENT) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new PacketWarpMessage(player, (byte) 0, amount));
        }
        if (type == IPlayerWarp.EnumWarpType.NORMAL) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new PacketWarpMessage(player, (byte) 1, amount));
        }
        if (type == IPlayerWarp.EnumWarpType.TEMPORARY) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new PacketWarpMessage(player, (byte) 2, amount));
        }
        if (amount > 0) {
            pw.setCounter(pw.get(IPlayerWarp.EnumWarpType.TEMPORARY) + pw.get(IPlayerWarp.EnumWarpType.PERMANENT) + pw.get(IPlayerWarp.EnumWarpType.NORMAL));
        }
        if (type != IPlayerWarp.EnumWarpType.TEMPORARY && ThaumcraftCapabilities.knowsResearchStrict(player, "FIRSTSTEPS") && !ThaumcraftCapabilities.knowsResearchStrict(player, "WARP")) {
            this.completeResearch(player, "WARP");
            player.displayClientMessage(Component.translatable("research.WARP.warn"), true);
        }
        pw.sync((ServerPlayer) player);
    }

    @Override
    public boolean progressResearch(Player player, String researchkey) {
        return researchkey != null && !player.level.isClientSide && ResearchManager.progressResearch(player, researchkey);
    }

    @Override
    public boolean completeResearch(Player player, final String researchkey) {
        return researchkey != null && !player.level.isClientSide && ResearchManager.completeResearch(player, researchkey);
    }

    @Override
    public AspectList getObjectAspects(ItemStack is) {
        return ThaumcraftCraftingManager.getObjectTags(is);
    }

    public AspectList generateTags(ItemStack is) {
        return ThaumcraftCraftingManager.generateTags(is);
    }

    @Override
    public void addFlux(Level world, BlockPos pos, float amount, boolean showEffect) {
        if (world.isClientSide) {
            return;
        }
        AuraHandler.addFlux(world, pos, amount);
        if (showEffect && amount > 0.0f) {
            PacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), 32.0f, world.dimension())), new PacketFXPollute(pos, amount));
        }
    }
}
