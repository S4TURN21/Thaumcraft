package thaumcraft.api.research;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;

public class ResearchEvent extends Event {
    private Player player;

    public ResearchEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public static class Knowledge extends ResearchEvent {
        private EnumKnowledgeType type;
        private ResearchCategory category;
        private int amount;

        public Knowledge(Player player, EnumKnowledgeType type, ResearchCategory category, int amount) {
            super(player);
            this.type = type;
            this.category = category;
            this.amount = amount;
        }

        public EnumKnowledgeType getType() {
            return type;
        }

        public ResearchCategory getCategory() {
            return category;
        }

        public int getAmount() {
            return amount;
        }

    }

    public static class Research extends ResearchEvent {
        private String researchKey;

        public Research(Player player, String researchKey) {
            super(player);
            this.researchKey = researchKey;
        }

        public String getResearchKey() {
            return researchKey;
        }
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
