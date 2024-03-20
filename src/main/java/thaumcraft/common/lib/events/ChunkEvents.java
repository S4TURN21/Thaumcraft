package thaumcraft.common.lib.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;

@Mod.EventBusSubscriber
public class ChunkEvents {
    @SubscribeEvent
    public static void chunkSave(ChunkDataEvent.Save event) {
        if (event.getLevel() instanceof Level level && event.getChunk() instanceof LevelChunk chunk && chunk.getStatus() == ChunkStatus.FULL) {
            ResourceKey<Level> dim = level.dimension();
            ChunkPos loc = chunk.getPos();
            CompoundTag nbt = new CompoundTag();
            event.getData().put("Thaumcraft", nbt);
            AuraChunk ac = AuraHandler.getAuraChunk(dim, loc.x, loc.z);
            if (ac != null) {
                nbt.putShort("base", ac.getBase());
                nbt.putFloat("flux", ac.getFlux());
                nbt.putFloat("vis", ac.getVis());
                if (event.getChunk().isUnsaved()) {
                    AuraHandler.removeAuraChunk(dim, loc.x, loc.z);
                }
            }
        }
    }

    @SubscribeEvent
    public static void chunkLoad(ChunkDataEvent.Load event) {
        if (event.getLevel() instanceof Level level && event.getChunk() instanceof LevelChunk chunk && event.getStatus() == ChunkStatus.ChunkType.LEVELCHUNK && chunk.getStatus() == ChunkStatus.FULL) {
            ResourceKey<Level> dim = level.dimension();
            ChunkPos loc = event.getChunk().getPos();

            if (event.getData().getCompound("Thaumcraft").contains("base")) {
                CompoundTag nbt = event.getData().getCompound("Thaumcraft");
                short base = nbt.getShort("base");
                float flux = nbt.getFloat("flux");
                float vis = nbt.getFloat("vis");
                AuraHandler.addAuraChunk(dim, chunk, base, vis, flux);
            } else {
                AuraHandler.generateAura(chunk, level.random);
            }
        }
    }
}
