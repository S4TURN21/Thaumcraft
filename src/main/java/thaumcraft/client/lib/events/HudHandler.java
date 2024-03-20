package thaumcraft.client.lib.events;

import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.research.ResearchCategory;

import java.util.concurrent.LinkedBlockingQueue;

public class HudHandler {
    public LinkedBlockingQueue<KnowledgeGainTracker> knowledgeGainTrackers;
    public static ResourceLocation[] KNOW_TYPE = new ResourceLocation[]{new ResourceLocation("thaumcraft", "textures/research/knowledge_theory.png"), new ResourceLocation("thaumcraft", "textures/research/knowledge_observation.png")};

    public HudHandler() {
        this.knowledgeGainTrackers = new LinkedBlockingQueue<>();
    }

    public static class KnowledgeGainTracker {
        IPlayerKnowledge.EnumKnowledgeType type;
        ResearchCategory category;
        int progress;
        int max;
        long seed;
        boolean sparks;

        public KnowledgeGainTracker(IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int progress, long seed) {
            this.sparks = false;
            this.type = type;
            this.category = category;
            if (type == IPlayerKnowledge.EnumKnowledgeType.THEORY) {
                progress += 10;
            }
            this.progress = progress;
            this.max = progress;
            this.seed = seed;
        }
    }
}
