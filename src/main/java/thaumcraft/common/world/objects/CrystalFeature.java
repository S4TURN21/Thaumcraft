package thaumcraft.common.world.objects;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Material;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blocks.world.ore.BlockCrystal;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.world.biomes.BiomeHandler;

public class CrystalFeature extends Feature<NoneFeatureConfiguration> {

    public CrystalFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
        RandomSource random = pContext.random();
        WorldGenLevel world = pContext.level();
        var chunkPos = world.getChunk(pContext.origin()).getPos();

        float density = ModConfig.CONFIG_WORLD.oreDensity.get() / 100.0f;

        if (ModConfig.CONFIG_WORLD.generateCrystals.get()) {
            int t = 8;
            int maxCrystals = Math.round(64.0f * density);
            int cc = 0;

            for (int j = 0; j < Math.round(t * density); ++j) {
                int randPosX2 = chunkPos.x * 16 + 8 + Mth.randomBetweenInclusive(random, -6, 6);
                int randPosZ3 = chunkPos.z * 16 + 8 + Mth.randomBetweenInclusive(random, -6, 6);
                int randPosY3 = Mth.randomBetweenInclusive(random, -64, Math.max(5, world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, new BlockPos(randPosX2, 0, randPosZ3)).getY() - 5));
                BlockPos bp = new BlockPos(randPosX2, randPosY3, randPosZ3);
                int md = random.nextInt(6);
                if (random.nextInt(3) == 0) {
                    Aspect tag = BiomeHandler.getRandomBiomeTag(world.getBiome(bp), random);
                    if (tag == null) {
                        md = random.nextInt(6);
                    } else {
                        md = ShardType.getMetaByAspect(tag);
                    }
                }
                Block oreBlock = ShardType.byMetadata(md).getOre();

                for (int xx = -1; xx <= 1; ++xx) {
                    for (int yy = -1; yy <= 1; ++yy) {
                        for (int zz = -1; zz <= 1; ++zz) {
                            if (random.nextInt(3) != 0) {
                                BlockState bs = world.getBlockState(bp.offset(xx, yy, zz));
                                Material bm = bs.getMaterial();
                                if (!bm.isLiquid() && (world.getBlockState(bp.offset(xx, yy, zz)).isAir() || world.getBlockState(bp.offset(xx, yy, zz)).getMaterial().isReplaceable()) && BlockUtils.isBlockTouching(world, bp.offset(xx, yy, zz), Material.STONE, true)) {
                                    int amt = 1 + random.nextInt(3);
                                    world.setBlock(bp.offset(xx, yy, zz), ((BlockCrystal) oreBlock).getExtendedState(((BlockCrystal) oreBlock).getStateFromMeta(amt), world, bp.offset(xx, yy, zz)), 0);
                                    cc += amt;
                                }
                            }
                        }
                    }
                }
                if (cc > maxCrystals) {
                    break;
                }
            }
            return true;
        }
        return false;
    }
}
