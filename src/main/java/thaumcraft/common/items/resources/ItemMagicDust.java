package thaumcraft.common.items.resources;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.Arrays;
import java.util.List;

public class ItemMagicDust extends ItemTCBase {
    public ItemMagicDust(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        InteractionHand hand = context.getHand();
        double hitX = context.getClickLocation().x;
        double hitY = context.getClickLocation().y;
        double hitZ = context.getClickLocation().z;

        if (!player.mayUseItemAt(pos, side, context.getItemInHand())) {
            return InteractionResult.FAIL;
        }
        if (player.isCrouching()) {
            return InteractionResult.PASS;
        }
        player.swing(hand);
        for (IDustTrigger trigger : IDustTrigger.triggers) {
            IDustTrigger.Placement place = trigger.getValidFace(world, player, pos, side);
            if (place != null) {
                if (!player.isCreative()) {
                    player.getItemInHand(hand).grow(-1);
                }
                trigger.execute(world, player, pos, place, side);
                if (world.isClientSide) {
                    this.doSparkles(player, world, pos, hitX, hitY, hitZ, hand, trigger, place);
                    break;
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.onItemUseFirst(stack, context);
    }

    private void doSparkles(Player player, Level world, BlockPos pos, double hitX, double hitY, double hitZ, InteractionHand hand, IDustTrigger trigger, IDustTrigger.Placement place) {
        Vec3 v1 = EntityUtils.posToHand(player, hand);
        Vec3 v2 = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        v2 = v2.add(0.5, 0.5, 0.5);
        v2 = v2.subtract(v1);

        for (int cnt = 50, a = 0; a < cnt; ++a) {
            boolean floaty = a < cnt / 3;
            float r = Mth.randomBetweenInclusive(world.random, 255, 255) / 255.0f;
            float g = Mth.randomBetweenInclusive(world.random, 189, 255) / 255.0f;
            float b = Mth.randomBetweenInclusive(world.random, 64, 255) / 255.0f;

            FXDispatcher.INSTANCE.drawSimpleSparkle(world.random, v1.x, v1.y, v1.z, v2.x / 6.0 + world.random.nextGaussian() * 0.05, v2.y / 6.0 + world.random.nextGaussian() * 0.05 + (floaty ? 0.05 : 0.15), v2.z / 6.0 + world.random.nextGaussian() * 0.05, 0.5f, r, g, b, world.random.nextInt(5), floaty ? (0.3f + world.random.nextFloat() * 0.5f) : 0.85f, floaty ? 0.2f : 0.5f, 16);
        }
        world.playLocalSound((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), SoundsTC.dust, SoundSource.PLAYERS, 0.33f, 1.0f + (float) world.random.nextGaussian() * 0.05f, false);
        List<BlockPos> sparkles = Arrays.asList(pos);
        if (sparkles != null) {
            var offset = pos.offset((double) hitX, (double) hitY, (double) hitZ);
            Vec3 v3 = new Vec3(offset.getX(), offset.getY(), offset.getZ());
            for (BlockPos p : sparkles) {
                FXDispatcher.INSTANCE.drawBlockSparkles(p, v3);
            }
        }
    }
}
