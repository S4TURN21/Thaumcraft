package thaumcraft.api;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.blocks.BlocksTC;

public class BlockTagsTC extends BlockTagsProvider {
    public static final TagKey<Block> SILVERWOOD_LOGS = BlockTags.create(new ResourceLocation("thaumcraft", "silverwood_logs"));
    public static final TagKey<Block> NITOR = BlockTags.create(new ResourceLocation("thaumcraft", "nitor"));

    public BlockTagsTC(DataGenerator generator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(SILVERWOOD_LOGS).add(BlocksTC.logSilverwood);
        tag(BlockTags.LOGS).add(BlocksTC.logSilverwood);
        tag(BlockTags.LEAVES).add(BlocksTC.leafSilverwood);

        var tag = tag(NITOR);
        for (DyeColor dye : DyeColor.values()) {
            tag.add(BlocksTC.nitor.get(dye));
        }
    }
}
