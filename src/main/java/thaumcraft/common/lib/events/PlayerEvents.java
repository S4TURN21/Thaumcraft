package thaumcraft.common.lib.events;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.items.resources.ItemCrystalEssence;
import thaumcraft.common.lib.capabilities.PlayerKnowledge;
import thaumcraft.common.lib.utils.InventoryUtils;

@Mod.EventBusSubscriber(modid = Thaumcraft.MODID)
public class PlayerEvents {
    @SubscribeEvent
    public static void pickupItem(EntityItemPickupEvent event) {
        if (event.getEntity() != null && !event.getItem().level.isClientSide && event.getItem() != null && event.getItem().getItem() != null) {
            IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(event.getEntity());
            if (event.getItem().getItem().getItem() instanceof ItemCrystalEssence && !knowledge.isResearchKnown("!gotcrystals")) {
                knowledge.addResearch("!gotcrystals");
                knowledge.sync((ServerPlayer) event.getEntity());
                event.getEntity().sendSystemMessage(Component.translatable("got.crystals").withStyle(ChatFormatting.DARK_PURPLE));
                if (ModConfig.CONFIG_MISC.noSleep.get() && !knowledge.isResearchKnown("!gotdream")) {
                    giveDreamJournal(event.getEntity());
                }
            }
        }
    }

    @SubscribeEvent
    public static void wakeUp(PlayerWakeUpEvent event) {
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(event.getEntity());
        if (event.getEntity() != null && !event.getEntity().level.isClientSide && knowledge.isResearchKnown("!gotcrystals") && !knowledge.isResearchKnown("!gotdream")) {
            giveDreamJournal(event.getEntity());
        }
    }

    private static void giveDreamJournal(Player player) {
        final IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        knowledge.addResearch("!gotdream");
        knowledge.sync((ServerPlayer) player);
        ItemStack book = ConfigItems.startBook.copy();
        book.getTag().putString("title", I18n.get("book.start.title"));
        book.getTag().putString("author", player.getName().getString());
        if (!player.getInventory().add(book)) {
            InventoryUtils.dropItemAtEntity(player.level, book, player);
        }
        try {
            player.sendSystemMessage(Component.translatable("got.dream").withStyle(ChatFormatting.DARK_PURPLE));
        } catch (Exception ex) {
        }
    }

    @SubscribeEvent
    public static void attachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(PlayerKnowledge.Provider.NAME, new PlayerKnowledge.Provider());
        }
    }

    @SubscribeEvent
    public static void playerJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            IPlayerKnowledge pk = ThaumcraftCapabilities.getKnowledge(player);
            if (pk != null) {
                pk.sync(player);
            }
        }
    }

    @SubscribeEvent
    public static void cloneCapabilitiesEvent(PlayerEvent.Clone event) {
        try {
            CompoundTag nbtKnowledge = ThaumcraftCapabilities.getKnowledge(event.getOriginal()).serializeNBT();
            ThaumcraftCapabilities.getKnowledge(event.getEntity()).deserializeNBT(nbtKnowledge);
        } catch (Exception e) {
            Thaumcraft.log.error("Could not clone player [" + event.getOriginal().getName() + "] knowledge when changing dimensions");
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IPlayerKnowledge.class);
    }
}
