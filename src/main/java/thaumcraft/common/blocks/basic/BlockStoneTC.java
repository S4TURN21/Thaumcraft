package thaumcraft.common.blocks.basic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class BlockStoneTC extends Block {
    public BlockStoneTC() {
        super(Properties.of(Material.STONE)
                .destroyTime(2.0f)
                .explosionResistance(10.0f)
                .sound(SoundType.STONE));
    }
}
