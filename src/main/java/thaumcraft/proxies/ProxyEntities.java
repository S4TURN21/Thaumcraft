package thaumcraft.proxies;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import thaumcraft.client.renderers.entity.RenderSpecialItem;
import thaumcraft.common.entities.EntitySpecialItem;

public class ProxyEntities {
    public void setupEntityRenderers() {
        EntityRenderers.register((EntityType<EntitySpecialItem>) Registry.ENTITY_TYPE.get(new ResourceLocation("thaumcraft", "special_item")), RenderSpecialItem::new);
    }
}
