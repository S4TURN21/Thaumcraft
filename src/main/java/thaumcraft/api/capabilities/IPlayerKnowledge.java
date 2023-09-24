package thaumcraft.api.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.Set;

public interface IPlayerKnowledge extends INBTSerializable<CompoundTag> {
    void clear();

    EnumResearchStatus getResearchStatus(String res);

    boolean isResearchComplete(String res);

    boolean isResearchKnown(String res);

    int getResearchStage(String res);

    boolean addResearch(@Nonnull String res);

    @Nonnull
    Set<String> getResearchList();

    boolean setResearchFlag(@Nonnull String res, @Nonnull EnumResearchFlag flag);

    boolean clearResearchFlag(@Nonnull String res, @Nonnull EnumResearchFlag flag);

    boolean hasResearchFlag(@Nonnull String res, @Nonnull EnumResearchFlag flag);

    void sync(ServerPlayer player);

    public enum EnumResearchStatus {
        UNKNOWN,
        COMPLETE,
        IN_PROGRESS;
    }

    public enum EnumResearchFlag {
        PAGE,
        RESEARCH,
        POPUP;
    }
}
