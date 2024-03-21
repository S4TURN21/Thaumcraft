package thaumcraft.common.items.tools;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketAuraToClient;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;

public class ItemThaumometer extends ItemTCBase {
    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        boolean held = pIsSelected || pSlotId == 0;
        if (held && !pLevel.isClientSide && pEntity.tickCount % 20 == 0 && pEntity instanceof ServerPlayer) {
            updateAura(pStack, pLevel, (ServerPlayer)pEntity);
        }
    }
    private void updateAura(ItemStack stack, Level world, ServerPlayer player) {
        AuraChunk ac = AuraHandler.getAuraChunk(world.dimension(), player.getOnPos().getX() >> 4, player.getOnPos().getZ() >> 4);
        if (ac != null) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketAuraToClient(ac));
        }
    }
}
