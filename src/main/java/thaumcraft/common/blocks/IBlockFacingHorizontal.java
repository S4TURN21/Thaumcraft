package thaumcraft.common.blocks;

import com.google.common.base.Predicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public interface IBlockFacingHorizontal {
    public static DirectionProperty FACING = DirectionProperty.create("facing", new Predicate() {
        public boolean apply(Direction facing) {
            return facing != Direction.UP && facing != Direction.DOWN;
        }

        public boolean apply(Object facing) {
            return apply((Direction) facing);
        }
    });
}
