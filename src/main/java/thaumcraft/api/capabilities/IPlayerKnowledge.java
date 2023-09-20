package thaumcraft.api.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface IPlayerKnowledge extends INBTSerializable<CompoundTag> {
    void clear();

    EnumResearchStatus getResearchStatus(String res);

    boolean isResearchComplete(String res);

    boolean isResearchKnown(String res);

    int getResearchStage(String res);

    boolean addResearch(@NotNull String res);

    @NotNull
    Set<String> getResearchList();

    boolean setResearchFlag(@NotNull String res, @NotNull EnumResearchFlag flag);

    boolean clearResearchFlag(@NotNull String res, @NotNull EnumResearchFlag flag);

    boolean hasResearchFlag(@NotNull String res, @NotNull EnumResearchFlag flag);

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
