package thaumcraft.api.aura;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import thaumcraft.api.ThaumcraftApi;

public class AuraHelper {
    public static void polluteAura(Level world, BlockPos pos, float amount, boolean showEffect) {
        ThaumcraftApi.internalMethods.addFlux(world,pos,amount,showEffect);
    }
}
