package thaumcraft.api.research;

import net.minecraft.world.entity.player.Player;

public interface IScanThing {
    boolean checkThing(Player player, Object obj);

    String getResearchKey(Player player, Object object);
}
