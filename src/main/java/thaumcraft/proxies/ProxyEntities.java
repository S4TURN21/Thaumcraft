package thaumcraft.proxies;

import net.minecraft.client.renderer.entity.EntityRenderers;
import thaumcraft.client.renderers.entity.RenderSpecialItem;
import thaumcraft.common.config.ConfigEntities;

public class ProxyEntities {
    public void setupEntityRenderers() {
        EntityRenderers.register(ConfigEntities.SPECIAL_ITEM, RenderSpecialItem::new);
    }
}
