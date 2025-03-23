package thaumcraft.api.research;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ScanBlock implements IScanThing {
    String research;
    Block[] blocks;

    public ScanBlock(String research, Block... blocks) {
        this.research = research;
        this.blocks = blocks;
        for (Block block : blocks) {
            ScanningManager.addScannableThing(new ScanItem(research, new ItemStack(block)));
        }
    }

    @Override
    public boolean checkThing(Player player, Object obj) {
        if (obj instanceof BlockPos pos) {
            for (Block block : blocks) {
                if (player.level.getBlockState(pos).getBlock() == block) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        return research;
    }
}
