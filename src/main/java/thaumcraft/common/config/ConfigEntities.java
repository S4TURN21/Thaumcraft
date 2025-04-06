package thaumcraft.common.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegisterEvent;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.entities.projectile.EntityAlumentum;

public class ConfigEntities {
    public static final EntityType<EntitySpecialItem> SPECIAL_ITEM = EntityType.Builder.<EntitySpecialItem>of(EntitySpecialItem::new, MobCategory.MISC).clientTrackingRange(64).updateInterval(20).setShouldReceiveVelocityUpdates(true).noSummon().build("special_item");
    public static final EntityType<EntityAlumentum> ALUMENTUM = EntityType.Builder.<EntityAlumentum>of(EntityAlumentum::new, MobCategory.MISC).clientTrackingRange(64).updateInterval(20).setShouldReceiveVelocityUpdates(true).noSummon().build("alumentum");

    public static void initEntities(RegisterEvent.RegisterHelper<EntityType<?>> iForgeRegistry) {
        iForgeRegistry.register(new ResourceLocation("thaumcraft", "special_item"), SPECIAL_ITEM);
        iForgeRegistry.register(new ResourceLocation("thaumcraft", "alumentum"), ALUMENTUM);
    }
}
