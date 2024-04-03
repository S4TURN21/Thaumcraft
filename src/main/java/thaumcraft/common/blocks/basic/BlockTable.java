package thaumcraft.common.blocks.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.ForgeEventFactory;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.common.InventoryFake;
import thaumcraft.common.blockentities.crafting.BlockEntityResearchTable;
import thaumcraft.common.blocks.IBlockFacingHorizontal;

public class BlockTable extends Block {
    public BlockTable(Properties pProperties) {
        super(pProperties.noOcclusion());
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.CONSUME;
        }

        if (this == BlocksTC.tableWood && pPlayer.getItemInHand(pHand).getItem() instanceof IScribeTools) {
            BlockState bs = BlocksTC.researchTable.defaultBlockState();
            bs = bs.setValue(IBlockFacingHorizontal.FACING, pPlayer.getDirection());
            pLevel.setBlock(pPos, bs, 1 | 2);
            BlockEntityResearchTable blockEntity = (BlockEntityResearchTable) pLevel.getBlockEntity(pPos);
            blockEntity.setItem(0, pPlayer.getItemInHand(pHand).copy());
            pPlayer.setItemInHand(pHand, ItemStack.EMPTY);
            pPlayer.getInventory().setChanged();
            blockEntity.setChanged();
            pLevel.markAndNotifyBlock(pPos, pLevel.getChunkAt(pPos), bs, bs, 1 | 2, 512);
            ForgeEventFactory.firePlayerCraftingEvent(pPlayer, new ItemStack(BlocksTC.researchTable), new InventoryFake(1));
        }

        return InteractionResult.CONSUME;
    }
}
