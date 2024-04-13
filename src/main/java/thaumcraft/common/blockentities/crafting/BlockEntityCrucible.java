package thaumcraft.common.blockentities.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import thaumcraft.common.blockentities.BlockEntityThaumcraft;

public class BlockEntityCrucible extends BlockEntityThaumcraft {
    public BlockEntityCrucible(BlockPos pPos, BlockState pBlockState) {
        super(ForgeRegistries.BLOCK_ENTITY_TYPES.getValue(new ResourceLocation("thaumcraft:crucible")), pPos, pBlockState);
    }
}
