package thaumcraft.common.blockentities.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.ForgeRegistries;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blockentities.Tickable;

public class BlockEntityNitor extends BlockEntity implements Tickable {
    int count;

    public BlockEntityNitor(BlockPos pPos, BlockState pBlockState) {
        super(ForgeRegistries.BLOCK_ENTITY_TYPES.getValue(new ResourceLocation("thaumcraft:nitor")), pPos, pBlockState);
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            BlockState state = level.getBlockState(getBlockPos());
            FXDispatcher.INSTANCE.drawNitorFlames(worldPosition.getX() + 0.5f + level.random.nextGaussian() * 0.025, worldPosition.getY() + 0.45f + level.random.nextGaussian() * 0.025, worldPosition.getZ() + 0.5f + level.random.nextGaussian() * 0.025, level.random.nextGaussian() * 0.0025, level.random.nextFloat() * 0.06, level.random.nextGaussian() * 0.0025, state.getBlock().getMapColor(state, level, getBlockPos(), MaterialColor.SNOW).col, 0);
            if (count++ % 10 == 0) {
                FXDispatcher.INSTANCE.drawNitorCore(worldPosition.getX() + 0.5f, worldPosition.getY() + 0.49f, worldPosition.getZ() + 0.5f, 0.0, 0.0, 0.0);
            }
        }
    }
}
