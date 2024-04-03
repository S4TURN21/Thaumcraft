package thaumcraft.proxies;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import thaumcraft.client.renderers.blockentity.BlockEntityResearchTableRenderer;
import thaumcraft.common.blockentities.crafting.BlockEntityResearchTable;

public class ProxyBER {
    public void setupBER() {
        BlockEntityRenderers.register(((BlockEntityType<BlockEntityResearchTable>)ForgeRegistries.BLOCK_ENTITY_TYPES.getValue(new ResourceLocation("thaumcraft", "research_table"))), BlockEntityResearchTableRenderer::new);
    }
}
