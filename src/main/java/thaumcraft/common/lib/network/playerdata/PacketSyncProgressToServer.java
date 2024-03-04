package thaumcraft.common.lib.network.playerdata;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.network.NetworkEvent;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.InventoryUtils;

import java.util.function.Supplier;

public class PacketSyncProgressToServer {
    private String key;
    private boolean first;
    private boolean checks;
    private boolean noFlags;

    public PacketSyncProgressToServer() {
    }

    public PacketSyncProgressToServer(String key, boolean first, boolean checks, boolean noFlags) {
        this.key = key;
        this.first = first;
        this.checks = checks;
        this.noFlags = noFlags;
    }

    public PacketSyncProgressToServer(String key, boolean first) {
        this(key, first, false, true);
    }

    public PacketSyncProgressToServer(FriendlyByteBuf buffer) {
        key = buffer.readUtf();
        first = buffer.readBoolean();
        checks = buffer.readBoolean();
        noFlags = buffer.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUtf(key);
        buffer.writeBoolean(first);
        buffer.writeBoolean(checks);
        buffer.writeBoolean(noFlags);
    }

    public static PacketSyncProgressToServer fromBytes(FriendlyByteBuf buffer) {
        return new PacketSyncProgressToServer(buffer);
    }

    public static void onMessage(PacketSyncProgressToServer message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null && message.first != ThaumcraftCapabilities.knowsResearch(player, message.key)) {
                if (message.checks && !checkRequisites(player, message.key)) {
                    return;
                }
                if (message.noFlags) {
                    ResearchManager.noFlags = true;
                }
                ResearchManager.progressResearch(player, message.key);
            }
        });
    }

    private static boolean checkRequisites(Player player, String key) {
        ResearchEntry research = ResearchCategories.getResearch(key);
        if (research.getStages() != null) {
            int currentStage = ThaumcraftCapabilities.getKnowledge(player).getResearchStage(key) - 1;
            if (currentStage < 0) {
                return false;
            }
            if (currentStage >= research.getStages().length) {
                return true;
            }
            ResearchStage stage = research.getStages()[currentStage];
            Object[] o = stage.getObtain();
            if (o != null) {
                for (int a = 0; a < o.length; ++a) {
                    ItemStack ts = ItemStack.EMPTY;
                    boolean ore = false;
                    if (o[a] instanceof ItemStack itemStack) {
                        ts = itemStack;
                        if(itemStack.is(Tags.Items.ORES)) {
                            ore = true;
                        }
                    }
                    if (!InventoryUtils.isPlayerCarryingAmount(player, ts, ore)) {
                        return false;
                    }
                }
                for (int a = 0; a < o.length; ++a) {
                    boolean ore2 = false;
                    ItemStack ts2 = ItemStack.EMPTY;
                    if (o[a] instanceof ItemStack itemStack) {
                        ts2 = itemStack;
                        if(itemStack.is(Tags.Items.ORES)) {
                            ore2 = true;
                        }
                    }
                    InventoryUtils.consumePlayerItem(player, ts2, true, ore2);
                }
            }
            Object[] c = stage.getCraft();
            if (c != null) {
                for (int a2 = 0; a2 < c.length; ++a2) {
                    if (!ThaumcraftCapabilities.getKnowledge(player).isResearchKnown("[#]" + stage.getCraftReference()[a2])) {
                        return false;
                    }
                }
            }
            String[] r = stage.getResearch();
            if (r != null) {
                for (int a3 = 0; a3 < r.length; ++a3) {
                    if (!ThaumcraftCapabilities.knowsResearchStrict(player, r[a3])) {
                        return false;
                    }
                }
            }
            ResearchStage.Knowledge[] k = stage.getKnow();
            if (k != null) {
                for (int a4 = 0; a4 < k.length; ++a4) {
                    int pk = ThaumcraftCapabilities.getKnowledge(player).getKnowledge(k[a4].type, k[a4].category);
                    if (pk < k[a4].amount) {
                        return false;
                    }
                }
                for (int a4 = 0; a4 < k.length; ++a4) {
                    ResearchManager.addKnowledge(player, k[a4].type, k[a4].category, -k[a4].amount * k[a4].type.getProgression());
                }
            }
        }
        return true;
    }
}
