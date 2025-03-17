package thaumcraft.common.lib.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.items.IGoggles;

import java.util.ArrayList;
import java.util.List;

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
