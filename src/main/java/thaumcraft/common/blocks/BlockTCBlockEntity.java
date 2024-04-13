package thaumcraft.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.utils.InventoryUtils;

import java.lang.reflect.InvocationTargetException;

public class BlockTCBlockEntity extends BaseEntityBlock {
    private final Class<? extends BlockEntity> blockEntityClass;

    protected BlockTCBlockEntity(Class<? extends BlockEntity> tc, Properties pProperties) {
        super(pProperties);
        this.blockEntityClass = tc;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (blockEntityClass == null) {
            return null;
        }
        try {
            return blockEntityClass.getDeclaredConstructor(BlockPos.class, BlockState.class).newInstance(pPos, pState);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            Thaumcraft.log.catching(e);
        }
        return null;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        InventoryUtils.dropItems(pLevel, pPos);
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        pLevel.removeBlockEntity(pPos);
    }
}
