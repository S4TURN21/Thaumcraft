package thaumcraft.common.lib.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.common.InventoryFake;
import thaumcraft.common.lib.events.ServerEvents;

public class DustTriggerOre implements IDustTrigger {
    String target;
    ItemStack result;
    String research;

    public DustTriggerOre(String research, String target, ItemStack result) {
        this.target = target;
        this.result = result;
        this.research = research;
    }

    @Override
    public Placement getValidFace(Level world, Player player, BlockPos pos, Direction facing) {
        BlockState bs = world.getBlockState(pos);
        boolean b = true;
//        try {
//            int[] oreIDs;
//            int[] ods = oreIDs = OreDictionary.getOreIDs(new ItemStack(bs.getBlock(), 1, bs.getBlock().damageDropped(bs)));
//            for (int q : oreIDs) {
//                if (q == OreDictionary.getOreID(target)) {
//                    b = true;
//                    break;
//                }
//            }
//        } catch (Exception ex) {
//        }
        return (b && (research == null || ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research))) ? new Placement(0, 0, 0, null) : null;
    }

    @Override
    public void execute(Level world, Player player, BlockPos pos, Placement placement, Direction facing) {
        ForgeEventFactory.firePlayerCraftingEvent(player, result, new InventoryFake(1));
        BlockState state = world.getBlockState(pos);
        ServerEvents.addRunnableServer(world, new Runnable() {
            @Override
            public void run() {
                ServerEvents.addSwapper(world, pos, state, result, false, 0, player, true, true, -9999, false, false, 0, ServerEvents.DEFAULT_PREDICATE, 0.0f);
            }
        }, 50);
    }
}
