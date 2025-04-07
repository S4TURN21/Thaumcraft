package thaumcraft.common.blocks.world.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.world.ThaumcraftWorldGenerator;

//public class BlockSaplingTC extends SaplingBlock {
//    public BlockSaplingTC(AbstractTreeGrower pTreeGrower, Properties pProperties) {
//        super(pTreeGrower, pProperties);
//    }
//}


public class BlockSaplingTC extends BushBlock implements BonemealableBlock {
    public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    public BlockSaplingTC(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    public @NotNull VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public void grow(ServerLevel worldIn, BlockPos pos, BlockState state, RandomSource rand) {
        if ((int) state.getValue(BlockSaplingTC.STAGE) == 0) {
            worldIn.setBlock(pos, state.cycle(BlockSaplingTC.STAGE), 4);
        } else {
            generateTree(worldIn, pos, state, rand);
        }
    }

    public void generateTree(ServerLevel pLevel, BlockPos pPos, BlockState pState, RandomSource pRandom) {
        if (pState.getBlock() == BlocksTC.saplingSilverwood) {
            var event = ForgeEventFactory.blockGrowFeature(pLevel, pRandom, pPos, ThaumcraftWorldGenerator.SILVERWOOD_FEATURE.getHolder().get());
            if (event.getResult().equals(net.minecraftforge.eventbus.api.Event.Result.DENY)) {
                return;
            }
            ConfiguredFeature<?, ?> configuredfeature = event.getFeature().value();

            pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 4);

            if (configuredfeature.place(pLevel, pLevel.getChunkSource().getGenerator(), pRandom, pPos)) {
                return;
//                pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 4);
            }
        }
//        Object object = null;
//        if(pState.getBlock() == BlocksTC.saplingSilverwood) {
////            object =
//        }
//        var test = new SilverwoodTreeFeature();
//        test.place(new FeaturePlaceContext<>())
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        return pRandom.nextFloat() < 0.25;
    }

    @Override
    public void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        grow(pLevel, pPos, pState, pRandom);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(STAGE);
    }
}
