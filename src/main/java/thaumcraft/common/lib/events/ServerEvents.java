package thaumcraft.common.lib.events;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockBamf;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Thaumcraft.MODID)
public class ServerEvents {

    public static HashMap<ResourceKey<Level>, LinkedBlockingQueue<VirtualSwapper>> swapList = new HashMap<>();
    public static final Predicate<SwapperPredicate> DEFAULT_PREDICATE = pred -> true;
    private static HashMap<ResourceKey<Level>, LinkedBlockingQueue<RunnableEntry>> serverRunList = new HashMap<>();

    @SubscribeEvent
    public static void worldTick(TickEvent.LevelTickEvent event) {
        if (event.side == LogicalSide.CLIENT) {
            return;
        }
        ResourceKey<Level> dim = event.level.dimension();
        if (event.phase == TickEvent.Phase.START) {

        } else {
            LinkedBlockingQueue<RunnableEntry> rlist = ServerEvents.serverRunList.get(dim);
            if (rlist == null) {
                ServerEvents.serverRunList.put(dim, rlist = new LinkedBlockingQueue<RunnableEntry>());
            } else if (!rlist.isEmpty()) {
                LinkedBlockingQueue<RunnableEntry> temp = new LinkedBlockingQueue<RunnableEntry>();
                while (!rlist.isEmpty()) {
                    RunnableEntry current = rlist.poll();
                    if (current != null) {
                        if (current.delay > 0) {
                            RunnableEntry runnableEntry = current;
                            --runnableEntry.delay;
                            temp.offer(current);
                        } else {
                            try {
                                current.runnable.run();
                            } catch (Exception ex) {
                            }
                        }
                    }
                }
                while (!temp.isEmpty()) {
                    rlist.offer(temp.poll());
                }
            }
            tickBlockSwap(event.level);
        }
    }

    private static void tickBlockSwap(Level world) {
        ResourceKey<Level> dim = world.dimension();
        LinkedBlockingQueue<VirtualSwapper> queue = ServerEvents.swapList.get(dim);
        if (queue != null) {
            while (!queue.isEmpty()) {
                VirtualSwapper vs = queue.poll();
                if (vs != null) {
                    if (vs.target == null || vs.target.isEmpty()) {
                        world.setBlock(vs.pos, Blocks.AIR.defaultBlockState(), 1 | 2);
                    } else {
                        Block tb = vs.target.getItem() instanceof BlockItem ? ((BlockItem) vs.target.getItem()).getBlock() : Blocks.AIR;
                        if (tb != null && tb != Blocks.AIR) {
//                            world.setBlockState(vs.pos, tb.getStateFromMeta(vs.target.getItemDamage()), 3);
                        } else {
                            world.setBlock(vs.pos, Blocks.AIR.defaultBlockState(), 1 | 2);
                            EntitySpecialItem entityItem = new EntitySpecialItem(world, vs.pos.getX() + 0.5, vs.pos.getY() + 0.1, vs.pos.getZ() + 0.5, vs.target.copy());
                            entityItem.setNoGravity(true);
                            entityItem.setDeltaMovement(0, 0, 0);
                            world.addFreshEntity(entityItem);
                        }
                    }
                    if (vs.fx) {
                        PacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p((double)vs.pos.getX(), (double)vs.pos.getY(), (double)vs.pos.getZ(), 32.0, world.dimension())), new PacketFXBlockBamf(vs.pos, vs.color, true, vs.fancy, null));
                    }
                }
            }
        }
    }

    public static void addSwapper(Level world, BlockPos pos, Object source, ItemStack target, boolean consumeTarget, int life, Player player, boolean fx, boolean fancy, int color, boolean pickup, boolean silk, int fortune, Predicate<SwapperPredicate> allowSwap, float visCost) {
        ResourceKey<Level> dim = world.dimension();
        LinkedBlockingQueue<VirtualSwapper> queue = ServerEvents.swapList.get(dim);
        if (queue == null) {
            ServerEvents.swapList.put(dim, new LinkedBlockingQueue<>());
            queue = ServerEvents.swapList.get(dim);
        }
        queue.offer(new VirtualSwapper(pos, source, target, consumeTarget, life, player, fx, fancy, color, pickup, silk, fortune, allowSwap, visCost));
        ServerEvents.swapList.put(dim, queue);
    }

    public static void addRunnableServer(Level world, Runnable runnable, int delay) {
        if (world.isClientSide) {
            return;
        }
        LinkedBlockingQueue<RunnableEntry> rlist = ServerEvents.serverRunList.get(world.dimension());
        if (rlist == null) {
            ServerEvents.serverRunList.put(world.dimension(), rlist = new LinkedBlockingQueue<RunnableEntry>());
        }
        rlist.add(new RunnableEntry(runnable, delay));
    }

    public static class SwapperPredicate {
        public Level world;
        public Player player;
        public BlockPos pos;

        public SwapperPredicate(Level world, Player player, BlockPos pos) {
            this.world = world;
            this.player = player;
            this.pos = pos;
        }
    }

    public static class VirtualSwapper {
        int color;
        boolean fancy;
        Predicate<SwapperPredicate> allowSwap;
        int lifespan;
        BlockPos pos;
        Object source;
        ItemStack target;
        Player player;
        boolean fx;
        boolean silk;
        boolean pickup;
        boolean consumeTarget;
        int fortune;
        float visCost;

        VirtualSwapper(BlockPos pos, Object source, ItemStack t, boolean consumeTarget, int life, Player p, boolean fx, boolean fancy, int color, boolean pickup, boolean silk, int fortune, Predicate<SwapperPredicate> allowSwap, float cost) {
            this.lifespan = 0;
            this.player = null;
            this.pos = pos;
            this.source = source;
            this.target = t;
            this.lifespan = life;
            this.player = p;
            this.consumeTarget = consumeTarget;
            this.fx = fx;
            this.fancy = fancy;
            this.allowSwap = allowSwap;
            this.silk = silk;
            this.fortune = fortune;
            this.pickup = pickup;
            this.color = color;
            this.visCost = cost;
        }
    }

    public static class RunnableEntry {
        Runnable runnable;
        int delay;

        public RunnableEntry(final Runnable runnable, final int delay) {
            this.runnable = runnable;
            this.delay = delay;
        }
    }
}
