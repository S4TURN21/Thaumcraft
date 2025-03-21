package thaumcraft.common.blocks.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import thaumcraft.common.blockentities.Tickable;
import thaumcraft.common.blockentities.crafting.BlockEntityCrucible;
import thaumcraft.common.blocks.BlockTCBlockEntity;
import thaumcraft.common.entities.EntitySpecialItem;

public class BlockCrucible extends BlockTCBlockEntity {
    private static final VoxelShape INSIDE = box(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    protected static final VoxelShape SHAPE = Shapes.join(Shapes.block(), Shapes.or(box(0.0D, 0.0D, 3.0D, 16.0D, 3.0D, 13.0D), box(3.0D, 0.0D, 0.0D, 13.0D, 3.0D, 16.0D), box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE), BooleanOp.ONLY_FIRST);
    private int delay;

    public BlockCrucible(Properties pProperties) {
        super(BlockEntityCrucible.class, pProperties);
        delay = 0;
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        if (!pLevel.isClientSide) {
            BlockEntityCrucible blockEntity = (BlockEntityCrucible) pLevel.getBlockEntity(pPos);
            if (blockEntity != null && pEntity instanceof ItemEntity && !(pEntity instanceof EntitySpecialItem) && blockEntity.heat > 150 && blockEntity.tank.getFluidAmount() > 0) {
                blockEntity.attemptSmelt((ItemEntity) pEntity);
            } else {
                ++this.delay;
                if (this.delay < 10) {
                    return;
                }
                this.delay = 0;
                if (pEntity instanceof LivingEntity && blockEntity != null && blockEntity.heat > 150 && blockEntity.tank.getFluidAmount() > 0) {
                    pEntity.hurt(DamageSource.IN_FIRE, 1.0f);
                    pLevel.playSound(null, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.4f, 2.0f + pLevel.random.nextFloat() * 0.4f);
                }
            }
        }

        super.stepOn(pLevel, pPos, pState, pEntity);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        BlockEntity te = pLevel.getBlockEntity(pPos);
        if (te instanceof BlockEntityCrucible crucible) {
            crucible.spillRemnants();
        }
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        var fs = FluidUtil.getFluidContained(pPlayer.getItemInHand(pHand));
        if (fs.isPresent()) {
            FluidStack fluidStack = fs.get();
            Fluid water = Fluids.WATER;
            if (fluidStack.containsFluid(new FluidStack(water, 1000))) {
                BlockEntity be = pLevel.getBlockEntity(pPos);
                if (be instanceof BlockEntityCrucible crucible) {
                    if (crucible.tank.getFluidAmount() < crucible.tank.getCapacity()) {
                        if (FluidUtil.interactWithFluidHandler(pPlayer, pHand, crucible.tank)) {
                            pPlayer.getInventory().setChanged();
                            be.setChanged();
                            pLevel.markAndNotifyBlock(pPos, pLevel.getChunkAt(pPos), pState, pState, 1 | 2, 512);
                            pLevel.playSound(null, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.33f, 1.0f + (pLevel.random.nextFloat() - pLevel.random.nextFloat()) * 0.3f);
                        }
                        return InteractionResult.sidedSuccess(pLevel.isClientSide);
                    }
                }
                return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
            }
        }
        if (pPlayer.getItemInHand(pHand).isEmpty() && pPlayer.isCrouching()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BlockEntityCrucible crucible) {
                crucible.spillRemnants();
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (level, pos, state, blockEntity) -> ((Tickable) blockEntity).tick();
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        BlockEntity te = pLevel.getBlockEntity(pPos);
        if (te instanceof BlockEntityCrucible crucible) {
            float n = (float) crucible.aspects.visSize();
            float r = n / 500.0f;
            return Mth.floor(r * 14.0f) + ((crucible.aspects.visSize() > 0) ? 1 : 0);
        }
        return 0;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pRandom.nextInt(10) == 0) {
            BlockEntity te = pLevel.getBlockEntity(pPos);
            if (te != null && te instanceof BlockEntityCrucible && ((BlockEntityCrucible) te).tank.getFluidAmount() > 0 && ((BlockEntityCrucible) te).heat > 150) {
                pLevel.playLocalSound(pPos.getX(), pPos.getY(), pPos.getZ(), SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.1f + pRandom.nextFloat() * 0.1f, 1.2f + pRandom.nextFloat() * 0.2f, false);
            }
        }
    }
}
