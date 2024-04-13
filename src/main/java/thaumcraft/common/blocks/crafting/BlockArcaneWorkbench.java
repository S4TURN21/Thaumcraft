package thaumcraft.common.blocks.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import thaumcraft.common.blockentities.crafting.BlockEntityArcaneWorkbench;
import thaumcraft.common.blocks.BlockTCBlockEntity;

public class BlockArcaneWorkbench extends BlockTCBlockEntity {
    public BlockArcaneWorkbench() {
        super(BlockEntityArcaneWorkbench.class, Properties.of(Material.WOOD).sound(SoundType.WOOD).noOcclusion());
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity entity = pLevel.getBlockEntity(pPos);
        NetworkHooks.openScreen(((ServerPlayer) pPlayer), (BlockEntityArcaneWorkbench) entity, pPos);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity != null && blockEntity instanceof BlockEntityArcaneWorkbench) {
            Containers.dropContents(pLevel, pPos, ((BlockEntityArcaneWorkbench) blockEntity).inventoryCraft);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        pLevel.removeBlockEntity(pPos);
    }
}
