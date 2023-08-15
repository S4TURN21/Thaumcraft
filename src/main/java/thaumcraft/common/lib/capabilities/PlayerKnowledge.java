package thaumcraft.common.lib.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncKnowledge;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PlayerKnowledge {
    private static class DefaultImpl implements IPlayerKnowledge {
        private final HashSet<String> research;

        private DefaultImpl() {
            this.research = new HashSet<>();
        }

        @Override
        public void clear() {
            this.research.clear();
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
//            return (ss.length <= 1 || this.getResearchStage(ss[0]) >= MathHelper.getInt(ss[1], 0)) && this.research.contains(ss[0]);
            return this.research.contains(ss[0]);
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
        public void sync(@NotNull ServerPlayer player) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketSyncKnowledge());
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag rootTag = new CompoundTag();
            ListTag researchList = new ListTag();
            for (String resKey : this.research) {
                CompoundTag tag = new CompoundTag();
                tag.putString("key", resKey);
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
