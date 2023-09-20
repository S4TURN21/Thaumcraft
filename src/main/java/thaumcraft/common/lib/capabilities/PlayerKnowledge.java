package thaumcraft.common.lib.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncKnowledge;

import java.util.*;

public class PlayerKnowledge {
    private static class DefaultImpl implements IPlayerKnowledge {
        private final HashSet<String> research;
        private final Map<String, Integer> stages;
        private final Map<String, HashSet<EnumResearchFlag>> flags;

        private DefaultImpl() {
            this.research = new HashSet<>();
            this.stages = new HashMap<>();
            this.flags = new HashMap<>();
        }

        @Override
        public void clear() {
            this.research.clear();
            this.flags.clear();
        }

        @Override
        public EnumResearchStatus getResearchStatus(String res) {
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
        public boolean isResearchComplete(final String res) {
            return this.getResearchStatus(res) == EnumResearchStatus.COMPLETE;
        }

        @Override
        public int getResearchStage(String res) {
            if (res == null || !this.research.contains(res)) {
                return -1;
            }
            Integer stage = this.stages.get(res);
            return (stage == null) ? 0 : stage;
        }

        @Override
        public boolean addResearch(@NotNull String res) {
            if (!this.isResearchKnown(res)) {
                this.research.add(res);
                return true;
            }
            return false;
        }

        @Override
        public @NotNull Set<String> getResearchList() {
            return Collections.unmodifiableSet(this.research);
        }

        @Override
        public boolean setResearchFlag(@NotNull String res, @NotNull EnumResearchFlag flag) {
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
        public boolean clearResearchFlag(@NotNull String res, @NotNull EnumResearchFlag flag) {
            HashSet<EnumResearchFlag> list = this.flags.get(res);
            if (list != null) {
                final boolean b = list.remove(flag);
                if (list.isEmpty()) {
                    this.flags.remove(this.research);
                }
                return b;
            }
            return false;
        }

        @Override
        public boolean hasResearchFlag(@NotNull String res, @NotNull EnumResearchFlag flag) {
            return this.flags.get(res) != null && this.flags.get(res).contains(flag);
        }

        @Override
        public void sync(@NotNull ServerPlayer player) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketSyncKnowledge(player));
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag rootTag = new CompoundTag();
            ListTag researchList = new ListTag();
            for (String resKey : this.research) {
                CompoundTag tag = new CompoundTag();
                tag.putString("key", resKey);
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
            return rootTag;
        }

        @Override
        public void deserializeNBT(CompoundTag rootTag) {
            if (rootTag == null) {
                return;
            }
            ListTag researchList = rootTag.getList("research", Tag.TAG_COMPOUND);
            for (int i = 0; i < researchList.size(); ++i) {
                CompoundTag tag = researchList.getCompound(i);
                String know = tag.getString("key");
                if (know != null && !this.isResearchKnown(know)) {
                    this.research.add(know);
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
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
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
