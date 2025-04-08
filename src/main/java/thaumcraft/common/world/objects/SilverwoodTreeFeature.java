package thaumcraft.common.world.objects;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.world.plants.BlockLogsTC;

public class SilverwoodTreeFeature extends Feature<NoneFeatureConfiguration> {
    private final int minTreeHeight;
    private final int randomTreeHeight;

    public SilverwoodTreeFeature(Codec<NoneFeatureConfiguration> pCodec, int pMinTreeHeight, int pRandomTreeHeight) {
        super(pCodec);
        this.minTreeHeight = pMinTreeHeight;
        this.randomTreeHeight = pRandomTreeHeight;
    }

    @Override
    public boolean place(@NotNull FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
        var level = pContext.level();
        var random = pContext.random();
        var pos = pContext.origin();

        int height = random.nextInt(randomTreeHeight) + minTreeHeight;

        boolean flag = true;

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (y < 1 || y + height + 1 > level.getMaxBuildHeight()) {
            return false;
        }

        for (int i = y; i <= y + 1 + height; i++) {
            byte spread = 1;

            if (i == y) {
                spread = 0;
            }

            if (i >= y + 1 + height - 2) {
                spread = 3;
            }

            for (int j = x - spread; j <= x + spread && flag; j++) {
                for (int k = z - spread; k <= z + spread && flag; k++) {
                    if (i >= 0 && i < level.getMaxBuildHeight()) {
                        BlockState state = level.getBlockState(new BlockPos(j, i, k));
                        if (!state.isAir() && !state.is(BlockTags.LEAVES) && !state.getMaterial().isReplaceable() && i > y) {
                            flag = false;
                        }
                    } else {
                        flag = false;
                    }
                }
            }
        }

        if (!flag) {
            return false;
        }

        BlockState state = level.getBlockState(new BlockPos(x, y - 1, z));
        boolean isSoil = state.canSustainPlant(level, new BlockPos(x, y - 1, z), Direction.UP, (BushBlock) BlocksTC.saplingSilverwood);

        if (isSoil && y < level.getMaxBuildHeight() - height - 1) {
            int start = y + height - 5;
            for (int end = y + height + 3 + random.nextInt(3), k2 = start; k2 <= end; ++k2) {
                int cty = Mth.clamp(k2, y + height - 3, y + height);
                for (int xx = x - 5; xx <= x + 5; ++xx) {
                    for (int zz = z - 5; zz <= z + 5; ++zz) {
                        double d3 = xx - x;
                        double d4 = k2 - cty;
                        double d5 = zz - z;
                        double dist = d3 * d3 + d4 * d4 + d5 * d5;
                        BlockState s2 = level.getBlockState(new BlockPos(xx, k2, zz));
                        if (dist < 10 + random.nextInt(8) && s2.isAir() || s2.is(BlockTags.LEAVES)) {
                            setBlock(level, new BlockPos(xx, k2, zz), BlocksTC.leafSilverwood.defaultBlockState());
                        }
                    }
                }
            }
            int k2;
            for (k2 = 0; k2 < height; ++k2) {
                BlockState s3 = level.getBlockState(new BlockPos(x, y + k2, z));
                if (s3.isAir() || s3.is(BlockTags.LEAVES) || s3.getMaterial().isReplaceable()) {
                    setBlock(level, new BlockPos(x, y + k2, z), BlocksTC.logSilverwood.defaultBlockState());
                    setBlock(level, new BlockPos(x - 1, y + k2, z), BlocksTC.logSilverwood.defaultBlockState());
                    setBlock(level, new BlockPos(x + 1, y + k2, z), BlocksTC.logSilverwood.defaultBlockState());
                    setBlock(level, new BlockPos(x, y + k2, z - 1), BlocksTC.logSilverwood.defaultBlockState());
                    setBlock(level, new BlockPos(x, y + k2, z + 1), BlocksTC.logSilverwood.defaultBlockState());
                }
            }
            setBlock(level, new BlockPos(x, y + k2, z), BlocksTC.logSilverwood.defaultBlockState());
            setBlock(level, new BlockPos(x - 1, y, z - 1), BlocksTC.logSilverwood.defaultBlockState());
            setBlock(level, new BlockPos(x + 1, y, z + 1), BlocksTC.logSilverwood.defaultBlockState());
            setBlock(level, new BlockPos(x - 1, y, z + 1), BlocksTC.logSilverwood.defaultBlockState());
            setBlock(level, new BlockPos(x + 1, y, z - 1), BlocksTC.logSilverwood.defaultBlockState());
            if (random.nextInt(3) != 0) {
                setBlock(level, new BlockPos(x - 1, y + 1, z - 1), BlocksTC.logSilverwood.defaultBlockState());
            }
            if (random.nextInt(3) != 0) {
                setBlock(level, new BlockPos(x + 1, y + 1, z + 1), BlocksTC.logSilverwood.defaultBlockState());
            }
            if (random.nextInt(3) != 0) {
                setBlock(level, new BlockPos(x - 1, y + 1, z + 1), BlocksTC.logSilverwood.defaultBlockState());
            }
            if (random.nextInt(3) != 0) {
                setBlock(level, new BlockPos(x + 1, y + 1, z - 1), BlocksTC.logSilverwood.defaultBlockState());
            }
            setBlock(level, new BlockPos(x - 2, y, z), BlocksTC.logSilverwood.defaultBlockState().setValue(BlockLogsTC.AXIS, Direction.Axis.X));
            setBlock(level, new BlockPos(x + 2, y, z), BlocksTC.logSilverwood.defaultBlockState().setValue(BlockLogsTC.AXIS, Direction.Axis.X));
            setBlock(level, new BlockPos(x, y, z - 2), BlocksTC.logSilverwood.defaultBlockState().setValue(BlockLogsTC.AXIS, Direction.Axis.Z));
            setBlock(level, new BlockPos(x, y, z + 2), BlocksTC.logSilverwood.defaultBlockState().setValue(BlockLogsTC.AXIS, Direction.Axis.Z));
            setBlock(level, new BlockPos(x - 2, y - 1, z), BlocksTC.logSilverwood.defaultBlockState());
            setBlock(level, new BlockPos(x + 2, y - 1, z), BlocksTC.logSilverwood.defaultBlockState());
            setBlock(level, new BlockPos(x, y - 1, z - 2), BlocksTC.logSilverwood.defaultBlockState());
            setBlock(level, new BlockPos(x, y - 1, z + 2), BlocksTC.logSilverwood.defaultBlockState());
            setBlock(level, new BlockPos(x - 1, y + (height - 4), z - 1), BlocksTC.logSilverwood.defaultBlockState());
            setBlock(level, new BlockPos(x + 1, y + (height - 4), z + 1), BlocksTC.logSilverwood.defaultBlockState());
            setBlock(level, new BlockPos(x - 1, y + (height - 4), z + 1), BlocksTC.logSilverwood.defaultBlockState());
            setBlock(level, new BlockPos(x + 1, y + (height - 4), z - 1), BlocksTC.logSilverwood.defaultBlockState());
            if (random.nextInt(3) == 0) {
                setBlock(level, new BlockPos(x - 1, y + (height - 5), z - 1), BlocksTC.logSilverwood.defaultBlockState());
            }
            if (random.nextInt(3) == 0) {
                setBlock(level, new BlockPos(x + 1, y + (height - 5), z + 1), BlocksTC.logSilverwood.defaultBlockState());
            }
            if (random.nextInt(3) == 0) {
                setBlock(level, new BlockPos(x - 1, y + (height - 5), z + 1), BlocksTC.logSilverwood.defaultBlockState());
            }
            if (random.nextInt(3) == 0) {
                setBlock(level, new BlockPos(x + 1, y + (height - 5), z - 1), BlocksTC.logSilverwood.defaultBlockState());
            }
            setBlock(level, new BlockPos(x - 2, y + (height - 4), z), BlocksTC.logSilverwood.defaultBlockState().setValue(BlockLogsTC.AXIS, Direction.Axis.X));
            setBlock(level, new BlockPos(x + 2, y + (height - 4), z), BlocksTC.logSilverwood.defaultBlockState().setValue(BlockLogsTC.AXIS, Direction.Axis.X));
            setBlock(level, new BlockPos(x, y + (height - 4), z - 2), BlocksTC.logSilverwood.defaultBlockState().setValue(BlockLogsTC.AXIS, Direction.Axis.Z));
            setBlock(level, new BlockPos(x, y + (height - 4), z + 2), BlocksTC.logSilverwood.defaultBlockState().setValue(BlockLogsTC.AXIS, Direction.Axis.Z));

            return true;
        }
        return false;
    }
}
