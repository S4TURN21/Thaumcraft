package thaumcraft.client.renderers.entity.projectle;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import org.jetbrains.annotations.NotNull;

public class RenderNoProjectile extends EntityRenderer<ThrowableProjectile> {
    public RenderNoProjectile(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.shadowRadius = 0.1f;
    }

    @Override
    public void render(@NotNull ThrowableProjectile pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ThrowableProjectile pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
