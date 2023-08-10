package thaumcraft.common.lib.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.Material;

public class BlockUtils {
    public static boolean isBlockTouching(BlockGetter world, BlockPos pos, final Material mat, final boolean solid) {
        for (final Direction face : Direction.values()) {
            if (world.getBlockState(pos.relative(face)).getMaterial() == mat && (!solid || world.getBlockState(pos.relative(face)).isFaceSturdy(world, pos.relative(face), face.getOpposite()))) {
                return true;
            }
        }
        return false;
    }
}
