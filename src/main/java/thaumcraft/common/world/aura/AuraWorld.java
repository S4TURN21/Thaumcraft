package thaumcraft.common.world.aura;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import thaumcraft.common.lib.utils.PosXY;

import java.util.concurrent.ConcurrentHashMap;

public class AuraWorld {
    ResourceKey<Level> dim;
    ConcurrentHashMap<PosXY, AuraChunk> auraChunks;

    public AuraWorld(ResourceKey<Level> dim) {
        this.auraChunks = new ConcurrentHashMap<>();
        this.dim = dim;
    }

    public ConcurrentHashMap<PosXY, AuraChunk> getAuraChunks() {
        return auraChunks;
    }

    public AuraChunk getAuraChunkAt(int x, int y) {
        return getAuraChunkAt(new PosXY(x, y));
    }

    public AuraChunk getAuraChunkAt(PosXY loc) {
        AuraChunk ac = auraChunks.get(loc);
        return ac;
    }
}
