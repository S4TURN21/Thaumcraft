package thaumcraft.api.research;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

import java.util.ArrayList;

public class ScanningManager {
    static ArrayList<IScanThing> things = new ArrayList<>();

    public static void addScannableThing(IScanThing obj) {
        things.add(obj);
    }

    public static void scanTheThing(Player player, Object object) {
        boolean found = false;
        boolean suppress = false;
        for (IScanThing thing : things) {
            if (thing.checkThing(player, object)) {
                if (thing.getResearchKey(player, object) == null || thing.getResearchKey(player, object).isEmpty() ||
                        ThaumcraftApi.internalMethods.progressResearch(player, thing.getResearchKey(player, object))) {
                    if (thing.getResearchKey(player, object) == null || thing.getResearchKey(player, object).isEmpty())
                        suppress = true;
                    found = true;
                    thing.onSuccess(player, object);
                }
            }
        }
        if (!suppress) {
            if (!found) {
                ((ServerPlayer) player).sendSystemMessage(Component.translatable("tc.unknownobject").withStyle(ChatFormatting.DARK_PURPLE).withStyle(ChatFormatting.ITALIC), true);
            } else {
                ((ServerPlayer) player).sendSystemMessage(Component.translatable("tc.knownobject").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.ITALIC), true);
            }
        }
    }

    public static boolean isThingStillScannable(Player player, Object object) {
        for (IScanThing thing : things) {
            if (thing.checkThing(player, object)) {
                try {
                    if (!ThaumcraftCapabilities.knowsResearch(player, thing.getResearchKey(player, object))) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return false;
    }

    public static ItemStack getItemFromParms(Player player, Object obj) {
        ItemStack is = ItemStack.EMPTY;
        if (obj instanceof ItemStack)
            is = (ItemStack) obj;
        if (obj instanceof ItemEntity && ((ItemEntity) obj).getItem() != null)
            is = ((ItemEntity) obj).getItem();
        if (obj instanceof BlockPos) {
            BlockState state = player.level.getBlockState((BlockPos) obj);
            is = state.getBlock().getCloneItemStack(player.level, (BlockPos) obj, state);
            try {
                if (is == null || is.isEmpty())
                    is = state.getBlock().getCloneItemStack(state, rayTrace(player), player.level, (BlockPos) obj, player);
            } catch (Exception ignored) {
            }
            try {
                if ((is == null || is.isEmpty()) && state.getMaterial() == Material.WATER) {
                    is = new ItemStack(Items.WATER_BUCKET);
                }
                if ((is == null || is.isEmpty()) && state.getMaterial() == Material.LAVA) {
                    is = new ItemStack(Items.LAVA_BUCKET);
                }
            } catch (Exception ignored) {
            }
        }
        return is;
    }

    private static BlockHitResult rayTrace(Player player) {
        Vec3 vec3d = player.getEyePosition(0);
        Vec3 vec3d1 = player.getLookAngle();
        Vec3 vec3d2 = vec3d.add(vec3d1.x * 4, vec3d1.y * 4, vec3d1.z * 4);
        return player.level.clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player));
    }
}
