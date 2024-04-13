package thaumcraft.common.blocks.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.blockentities.Tickable;
import thaumcraft.common.blockentities.crafting.BlockEntityResearchTable;
import thaumcraft.common.blocks.BlockTCBlockEntity;
import thaumcraft.common.blocks.IBlockFacingHorizontal;

public class BlockResearchTable extends BlockTCBlockEntity implements IBlockFacingHorizontal {
    public BlockResearchTable(Properties pProperties) {
        super(BlockEntityResearchTable.class, pProperties);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity entity = pLevel.getBlockEntity(pPos);
        NetworkHooks.openScreen(((ServerPlayer) pPlayer), (BlockEntityResearchTable) entity, pPos);

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState bs = defaultBlockState();
        bs = bs.setValue(IBlockFacingHorizontal.FACING, pContext.getHorizontalDirection());
        return bs;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(IBlockFacingHorizontal.FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (level, pos, state, blockEntity) -> ((Tickable) blockEntity).tick();
    }
}
