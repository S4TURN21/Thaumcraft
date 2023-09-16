package thaumcraft.common.lib.utils;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityUtils {
    public static Vec3 posToHand(Entity e, InteractionHand hand) {
        double px = e.getX();
        double py = e.getBoundingBox().minY + e.getBbHeight() / 2.0f + 0.25;
        double pz = e.getZ();
        float m = (hand == InteractionHand.MAIN_HAND) ? 0.0f : 180.0f;
        px += -Mth.cos((e.getYRot() + m) / 180.0f * 3.141593f) * 0.3f;
        pz += -Mth.sin((e.getYRot() + m) / 180.0f * 3.141593f) * 0.3f;
        Vec3 vec3d = e.getLookAngle();
        px += vec3d.x * 0.3;
        py += vec3d.y * 0.3;
        pz += vec3d.z * 0.3;
        return new Vec3(px, py, pz);
    }
}
