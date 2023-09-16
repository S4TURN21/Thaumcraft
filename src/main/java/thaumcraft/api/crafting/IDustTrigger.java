package thaumcraft.api.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface IDustTrigger {
    public static final ArrayList<IDustTrigger> triggers = new ArrayList<IDustTrigger>();

    Placement getValidFace(Level world, Player player, BlockPos pos, Direction facing);

    void execute(Level world, Player player, BlockPos pos, Placement placement, Direction facing);

    default List<BlockPos> sparkle(Level world, Player player, BlockPos pos, Placement placement) {
        return Arrays.asList(pos);
    }

    static void registerDustTrigger(IDustTrigger trigger) {
        IDustTrigger.triggers.add(trigger);
    }

    public static class Placement {
        public int xOffset;
        public int yOffset;
        public int zOffset;
        public Direction facing;

        public Placement(int xOffset, int yOffset, int zOffset, Direction facing) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.zOffset = zOffset;
            this.facing = facing;
        }
    }
}
