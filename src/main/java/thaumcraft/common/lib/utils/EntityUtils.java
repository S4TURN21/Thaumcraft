package thaumcraft.common.lib.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.items.IGoggles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public static boolean hasGoggles(Entity e) {
        if (!(e instanceof Player)) {
            return false;
        }
        Player viewer = (Player) e;
        if (viewer.getMainHandItem().getItem() instanceof IGoggles && showPopups(viewer.getMainHandItem(), viewer)) {
            return true;
        }
        for (int a = 0; a < 4; ++a) {
            if (viewer.getInventory().getArmor(a).getItem() instanceof IGoggles && showPopups(viewer.getInventory().getArmor(a), viewer)) {
                return true;
            }
        }
        return false;
    }

    private static boolean showPopups(ItemStack stack, Player player) {
        return ((IGoggles) stack.getItem()).showIngamePopups(stack, player);
    }

    public static Entity getPointedEntity(Level world, Entity entity, double minrange, double range, float padding, boolean nonCollide) {
        return getPointedEntity(world, new EntityHitResult(entity, entity.getEyePosition(1.0f)), entity.getViewVector(1.0f), minrange, range, padding, nonCollide);
    }

    static Entity getPointedEntity(Level world, EntityHitResult ray, Vec3 lookVec, double minrange, double range, float padding, boolean nonCollide) {
        Entity pointedEntity = null;
        Vec3 eyePos = ray.getLocation();
        Vec3 targetVec = eyePos.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        AABB searchArea = ray.getEntity().getBoundingBox().expandTowards(lookVec.scale(range)).inflate(padding);
        List<Entity> list = world.getEntities(ray.getEntity(), searchArea);
        double d2 = range;
        for (Entity entity : list) {
            if (ray.getLocation().distanceTo(entity.getPosition(0.0f)) >= minrange) {
                AABB entityBB = entity.getBoundingBox();
                Optional<Vec3> optionalHit = entityBB.clip(eyePos, targetVec);

                if (optionalHit.isPresent()) {
                    double distance = eyePos.distanceToSqr(optionalHit.get());
                    if (distance < d2 * d2) {
                        pointedEntity = entity;
                        d2 = Math.sqrt(distance);
                    }
                }
            }
        }
        return pointedEntity;
    }

    public static <T extends Entity> List<T> getEntitiesInRange(Level world, BlockPos pos, Entity entity, Class<? extends T> classEntity, double range) {
        return getEntitiesInRange(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, entity, classEntity, range);
    }

    public static <T extends Entity> List<T> getEntitiesInRange(Level world, double x, double y, double z, Entity entity, Class<? extends T> classEntity, double range) {
        ArrayList<T> out = new ArrayList<T>();
        List list = world.getEntitiesOfClass(classEntity, new AABB(x, y, z, x, y, z).inflate(range, range, range));
        if (list.size() > 0) {
            for (Object e : list) {
                Entity ent = (Entity) e;

                if (entity != null && entity.getId() == ent.getId()) {
                    continue;
                }
                out.add((T) ent);
            }
        }
        return out;
    }
}
