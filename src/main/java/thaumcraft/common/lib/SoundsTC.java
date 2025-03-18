package thaumcraft.common.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.registries.RegisterEvent;

public class SoundsTC {
    public static SoundEvent spill;
    public static SoundEvent bubble;
    public static SoundEvent clack;
    public static SoundEvent poof;
    public static SoundEvent page;
    public static SoundEvent learn;
    public static SoundEvent write;
    public static SoundEvent crystal;
    public static SoundEvent scan;
    public static SoundEvent dust;
    public static SoundType CRYSTAL;

    public static void registerSoundTypes() {
        SoundsTC.CRYSTAL = new SoundType(0.5f, 1.0f, SoundsTC.crystal, SoundsTC.crystal, SoundsTC.crystal, SoundsTC.crystal, SoundsTC.crystal);
    }

    public static void registerSounds(RegisterEvent.RegisterHelper<SoundEvent> event) {
        SoundsTC.spill = getRegisteredSoundEvent(event, "thaumcraft:spill");
        SoundsTC.bubble = getRegisteredSoundEvent(event, "thaumcraft:bubble");
        SoundsTC.clack = getRegisteredSoundEvent(event, "thaumcraft:clack");
        SoundsTC.poof = getRegisteredSoundEvent(event, "thaumcraft:poof");
        SoundsTC.page = getRegisteredSoundEvent(event, "thaumcraft:page");
        SoundsTC.learn = getRegisteredSoundEvent(event, "thaumcraft:learn");
        SoundsTC.write = getRegisteredSoundEvent(event, "thaumcraft:write");
        SoundsTC.crystal = getRegisteredSoundEvent(event, "thaumcraft:crystal");
        SoundsTC.scan = getRegisteredSoundEvent(event, "thaumcraft:scan");
        SoundsTC.dust = getRegisteredSoundEvent(event, "thaumcraft:dust");
    }

    private static SoundEvent getRegisteredSoundEvent(RegisterEvent.RegisterHelper<SoundEvent> event, String id) {
        SoundEvent soundevent = new SoundEvent(new ResourceLocation(id));
        event.register(new ResourceLocation(id), soundevent);
        return soundevent;
    }
}
