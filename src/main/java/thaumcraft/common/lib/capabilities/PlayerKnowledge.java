package thaumcraft.common.lib.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncKnowledge;

import javax.annotation.Nonnull;
import java.util.*;

public class PlayerKnowledge {
    private static class DefaultImpl implements IPlayerKnowledge {
        private final HashSet<String> research;
        private final Map<String, Integer> stages;
        private final Map<String, HashSet<EnumResearchFlag>> flags;
        private final Map<String, Integer> knowledge;

        private DefaultImpl() {
            this.research = new HashSet<>();
            this.stages = new HashMap<>();
            this.flags = new HashMap<>();
            this.knowledge = new HashMap<>();
        }

        @Override
        public void clear() {
            this.research.clear();
            this.flags.clear();
            this.stages.clear();
            this.knowledge.clear();
        }

        @Override
        public EnumResearchStatus getResearchStatus(@Nonnull String res) {
            if (!this.isResearchKnown(res)) {
                return EnumResearchStatus.UNKNOWN;
            }
            ResearchEntry entry = ResearchCategories.getResearch(res);
            if (entry == null || entry.getStages() == null || this.getResearchStage(res) > entry.getStages().length) {
                return EnumResearchStatus.COMPLETE;
            }
            return EnumResearchStatus.IN_PROGRESS;
        }

        @Override
        public boolean isResearchKnown(String res) {
            if (res == null) {
                return false;
            }
            if (res.equals("")) {
                return true;
            }
            String[] ss = res.split("@");
            return (ss.length <= 1 || this.getResearchStage(ss[0]) >= Mth.getInt(ss[1], 0)) && this.research.contains(ss[0]);
        }

        @Override
        public boolean isResearchComplete(String res) {
            return this.getResearchStatus(res) == EnumResearchStatus.COMPLETE;
        }

        @Override
        public int getResearchStage(@Nonnull String res) {
            if (res == null || !this.research.contains(res)) {
                return -1;
            }
            Integer stage = this.stages.get(res);
            return (stage == null) ? 0 : stage;
        }

        @Override
        public boolean setResearchStage(String res, int stage) {
            if (res == null || !this.research.contains(res) || stage <= 0) {
                return false;
            }
            this.stages.put(res, stage);
            return true;
        }

        @Override
        public boolean addResearch(@Nonnull String res) {
            if (!this.isResearchKnown(res)) {
                this.research.add(res);
                return true;
            }
            return false;
        }

        @Override
        public boolean removeResearch(@Nonnull String res) {
            if (this.isResearchKnown(res)) {
                this.research.remove(res);
                return true;
            }
            return false;
        }

        @Nonnull
        @Override
        public Set<String> getResearchList() {
            return Collections.unmodifiableSet(this.research);
        }

        @Override
        public boolean setResearchFlag(@Nonnull String res, @Nonnull EnumResearchFlag flag) {
            HashSet<EnumResearchFlag> list = this.flags.get(res);
            if (list == null) {
                list = new HashSet<EnumResearchFlag>();
                this.flags.put(res, list);
            }
            if (list.contains(flag)) {
                return false;
            }
            list.add(flag);
            return true;
        }

        @Override
        public boolean clearResearchFlag(@Nonnull String res, @Nonnull EnumResearchFlag flag) {
            HashSet<EnumResearchFlag> list = this.flags.get(res);
            if (list != null) {
                boolean b = list.remove(flag);
                if (list.isEmpty()) {
                    this.flags.remove(this.research);
                }
                return b;
            }
            return false;
        }

        @Override
        public boolean hasResearchFlag(@Nonnull String res, @Nonnull EnumResearchFlag flag) {
            return this.flags.get(res) != null && this.flags.get(res).contains(flag);
        }

        private String getKey(EnumKnowledgeType type, ResearchCategory category) {
            return type.getAbbreviation() + "_" + ((category == null) ? "" : category.key);
        }

        @Override
        public boolean addKnowledge(EnumKnowledgeType type, ResearchCategory category, int amount) {
            String key = this.getKey(type, category);
            int c = this.getKnowledgeRaw(type, category);
            if (c + amount < 0) {
                return false;
            }
            c += amount;
            this.knowledge.put(key, c);
            return true;
        }

        @Override
        public int getKnowledge(EnumKnowledgeType type, ResearchCategory category) {
            String key = this.getKey(type, category);
            int c = this.knowledge.containsKey(key) ? this.knowledge.get(key) : 0;
            return (int) Math.floor(c / (double) type.getProgression());
        }

        @Override
        public int getKnowledgeRaw(EnumKnowledgeType type, ResearchCategory category) {
            String key = this.getKey(type, category);
            return this.knowledge.containsKey(key) ? this.knowledge.get(key) : 0;
        }

        @Override
        public void sync(@Nonnull ServerPlayer player) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketSyncKnowledge(player));
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag rootTag = new CompoundTag();
            ListTag researchList = new ListTag();
            for (String resKey : this.research) {
                CompoundTag tag = new CompoundTag();
                tag.putString("key", resKey);
                if (this.stages.containsKey(resKey)) {
                    tag.putInt("stage", (int) this.stages.get(resKey));
                }
                if (this.flags.containsKey(resKey)) {
                    HashSet<EnumResearchFlag> list = this.flags.get(resKey);
                    if (list != null) {
                        String fs = "";
                        for (EnumResearchFlag flag : list) {
                            if (fs.length() > 0) {
                                fs += ",";
                            }
                            fs += flag.name();
                        }
                        tag.putString("flags", fs);
                    }
                }
                researchList.add(tag);
            }
            rootTag.put("research", researchList);
            ListTag knowledgeList = new ListTag();
            for (String key : this.knowledge.keySet()) {
                Integer c = this.knowledge.get(key);
                if (c != null && c > 0 && key != null && !key.isEmpty()) {
                    CompoundTag tag2 = new CompoundTag();
                    tag2.putString("key", key);
                    tag2.putInt("amount", (int) c);
                    knowledgeList.add(tag2);
                }
            }
            rootTag.put("knowledge", knowledgeList);
            return rootTag;
        }

        @Override
        public void deserializeNBT(CompoundTag rootTag) {
            if (rootTag == null) {
                return;
            }
            this.clear();
            ListTag researchList = rootTag.getList("research", 10);
            for (int i = 0; i < researchList.size(); ++i) {
                CompoundTag tag = researchList.getCompound(i);
                String know = tag.getString("key");
                if (know != null && !this.isResearchKnown(know)) {
                    this.research.add(know);
                    int stage = tag.getInt("stage");
                    if (stage > 0) {
                        this.stages.put(know, stage);
                    }
                    String fs = tag.getString("flags");
                    if (fs.length() > 0) {
                        String[] split;
                        String[] ss = split = fs.split(",");
                        for (String s : split) {
                            EnumResearchFlag flag = null;
                            try {
                                flag = EnumResearchFlag.valueOf(s);
                            } catch (Exception ex) {
                            }
                            if (flag != null) {
                                this.setResearchFlag(know, flag);
                            }
                        }
                    }
                }
            }
            ListTag knowledgeList = rootTag.getList("knowledge", 10);
            for (int j = 0; j < knowledgeList.size(); ++j) {
                CompoundTag tag2 = knowledgeList.getCompound(j);
                String key = tag2.getString("key");
                int amount = tag2.getInt("amount");
                this.knowledge.put(key, amount);
            }
            this.addAutoUnlockResearch();
        }

        private void addAutoUnlockResearch() {
            for (final ResearchCategory cat : ResearchCategories.researchCategories.values()) {
                for (final ResearchEntry ri : cat.research.values()) {
                    if (ri.hasMeta(ResearchEntry.EnumResearchMeta.AUTOUNLOCK)) {
                        this.addResearch(ri.getKey());
                    }
                }
            }
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        public static final ResourceLocation NAME = new ResourceLocation("thaumcraft", "knowledge");
        private DefaultImpl knowledge;
        private final LazyOptional<DefaultImpl> optional = LazyOptional.of(this::createPlayerKnowledge);

        public Provider() {
            this.knowledge = new DefaultImpl();
        }

        private DefaultImpl createPlayerKnowledge() {
            if (this.knowledge == null) {
                this.knowledge = new DefaultImpl();
            }

            return this.knowledge;
        }

        @Override
        public @Nonnull <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
            if (capability == ThaumcraftCapabilities.KNOWLEDGE) {
                return optional.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.knowledge.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.knowledge.deserializeNBT(nbt);
        }
    }
}
