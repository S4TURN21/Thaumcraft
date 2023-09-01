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

    void sync(ServerPlayer player);

    public enum EnumResearchStatus {
        UNKNOWN,
        COMPLETE,
        IN_PROGRESS;
    }
}
