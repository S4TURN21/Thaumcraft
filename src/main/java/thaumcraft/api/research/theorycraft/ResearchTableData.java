package thaumcraft.api.research.theorycraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ResearchTableData {
    public BlockEntity table;

    public ResearchTableData(BlockEntity tileResearchTable) {
        table = tileResearchTable;
    }

    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();

        return nbt;
    }

    public void deserialize(CompoundTag nbt) {

    }

    public static int getAvailableInspiration(Player player) {
        float tot = 5;
        return Math.min(15, Math.round(tot));
    }
}
