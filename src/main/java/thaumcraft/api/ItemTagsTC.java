package thaumcraft.api;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.items.ItemsTC;

public class ItemTagsTC extends ItemTagsProvider {
    public static final TagKey<Item> SILVERWOOD_LOGS = ItemTags.create(new ResourceLocation("thaumcraft", "silverwood_logs"));
    public static final TagKey<Item> NITOR = ItemTags.create(new ResourceLocation("thaumcraft", "nitor"));
    public static final TagKey<Item> INGOTS_BRASS = ItemTags.create(new ResourceLocation("thaumcraft", "ingots/brass"));

    public ItemTagsTC(DataGenerator generator, BlockTagsProvider blockTagsProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, blockTagsProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.copy(BlockTagsTC.SILVERWOOD_LOGS, ItemTagsTC.SILVERWOOD_LOGS);
        this.copy(BlockTagsTC.NITOR, ItemTagsTC.NITOR);
        this.tag(INGOTS_BRASS).add(ItemsTC.brassIngot);
    }
}
