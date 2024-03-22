package thaumcraft.common.items.tools;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.client.fx.FXDispatcher;
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
            updateAura(pStack, pLevel, (ServerPlayer) pEntity);
        }
        if (held && pLevel.isClientSide && pEntity.tickCount % 5 == 0 && pEntity instanceof Player) {
            BlockHitResult mop = getRayTraceResultFromPlayerWild(pLevel, (Player) pEntity, true);
            if (mop != null && mop.getBlockPos() != null && ScanningManager.isThingStillScannable((Player) pEntity, mop.getBlockPos())) {
                FXDispatcher.INSTANCE.scanHighlight(mop.getBlockPos());
            }
        }
    }

    protected BlockHitResult getRayTraceResultFromPlayerWild(Level worldIn, Player playerIn, boolean useLiquids) {
        float f = playerIn.xRotO + (playerIn.getXRot() - playerIn.xRotO) + worldIn.random.nextInt(25) - worldIn.random.nextInt(25);
        float f2 = playerIn.yRotO + (playerIn.getYRot() - playerIn.yRotO) + worldIn.random.nextInt(25) - worldIn.random.nextInt(25);
        double d0 = playerIn.xo + (playerIn.getX() - playerIn.xo);
        double d2 = playerIn.yo + (playerIn.getY() - playerIn.yo) + playerIn.getEyeHeight();
        double d3 = playerIn.zo + (playerIn.getZ() - playerIn.zo);
        Vec3 vec3 = new Vec3(d0, d2, d3);
        float f3 = Mth.cos(-f2 * 0.017453292f - 3.1415927f);
        float f4 = Mth.sin(-f2 * 0.017453292f - 3.1415927f);
        float f5 = -Mth.cos(-f * 0.017453292f);
        float f6 = Mth.sin(-f * 0.017453292f);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d4 = 16.0;
        Vec3 vec4 = vec3.add(f7 * d4, f6 * d4, f8 * d4);
        return worldIn.clip(new ClipContext(vec3, vec4, ClipContext.Block.COLLIDER, useLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, playerIn));
    }

    private void updateAura(ItemStack stack, Level world, ServerPlayer player) {
        AuraChunk ac = AuraHandler.getAuraChunk(world.dimension(), player.getOnPos().getX() >> 4, player.getOnPos().getZ() >> 4);
        if (ac != null) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketAuraToClient(ac));
        }
    }
}
