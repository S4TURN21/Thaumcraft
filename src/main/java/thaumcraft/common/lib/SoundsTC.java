package thaumcraft.common.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegisterEvent;

public class SoundsTC {
    public static void registerSoundTypes() {
    }

    public static void registerSounds(RegisterEvent.RegisterHelper<SoundEvent> event) {

    }

    private static SoundEvent getRegisteredSoundEvent(RegisterEvent.RegisterHelper<SoundEvent> event, String id) {
        SoundEvent soundevent = new SoundEvent(new ResourceLocation(id));
        event.register(new ResourceLocation(id), soundevent);
        return soundevent;
    }
}
