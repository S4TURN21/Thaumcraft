package thaumcraft.api.capabilities;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ThaumcraftCapabilities {
    public static final Capability<IPlayerKnowledge> KNOWLEDGE = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<IPlayerWarp> WARP = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static IPlayerKnowledge getKnowledge(@NotNull Player player) {
        return player.getCapability(ThaumcraftCapabilities.KNOWLEDGE, null).resolve().get();
    }

    public static boolean knowsResearch(Player player, String... research) {
        for (final String r : research) {
            if (r.contains("&&")) {
                final String[] rr = r.split("&&");
                if (!knowsResearch(player, rr)) {
                    return false;
                }
            } else if (r.contains("||")) {
                final String[] split;
                final String[] rr = split = r.split("||");
                for (final String str : split) {
                    if (knowsResearch(player, str)) {
                        return true;
                    }
                }
            } else if (!getKnowledge(player).isResearchKnown(r)) {
                return false;
            }
        }
        return true;
    }

    public static boolean knowsResearchStrict(Player player, String... research) {
        for (final String r : research) {
            if (r.contains("&&")) {
                final String[] rr = r.split("&&");
                if (!knowsResearchStrict(player, rr)) {
                    return false;
                }
            } else if (r.contains("||")) {
                final String[] split;
                final String[] rr = split = r.split("||");
                for (final String str : split) {
                    if (knowsResearchStrict(player, str)) {
                        return true;
                    }
                }
            } else if (r.contains("@")) {
                if (!getKnowledge(player).isResearchKnown(r)) {
                    return false;
                }
            } else if (!getKnowledge(player).isResearchComplete(r)) {
                return false;
            }
        }
        return true;
    }

    public static IPlayerWarp getWarp(@Nonnull Player player) {
        return player.getCapability(ThaumcraftCapabilities.WARP, null).resolve().get();
    }
}
