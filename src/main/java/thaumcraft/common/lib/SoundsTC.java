package thaumcraft.common.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.registries.RegisterEvent;

public class SoundsTC {
    public static SoundEvent crystal;
    public static SoundType CRYSTAL;

    public static void registerSoundTypes() {
        SoundsTC.CRYSTAL = new SoundType(0.5f, 1.0f, SoundsTC.crystal, SoundsTC.crystal, SoundsTC.crystal, SoundsTC.crystal, SoundsTC.crystal);
    }

    public static void registerSounds(RegisterEvent.RegisterHelper<SoundEvent> event) {
        SoundsTC.crystal = getRegisteredSoundEvent(event, "thaumcraft:crystal");
    }

    private static SoundEvent getRegisteredSoundEvent(RegisterEvent.RegisterHelper<SoundEvent> event, String id) {
        SoundEvent soundevent = new SoundEvent(new ResourceLocation(id));
        event.register(new ResourceLocation(id), soundevent);
        return soundevent;
    }
}
