package com.ringlord593.sorcery_supplements.registry;

import com.ringlord593.sorcery_supplements.SorcerySupplements;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, SorcerySupplements.MODID);

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

    public static DeferredHolder<SoundEvent, SoundEvent>TETHER = registerSoundEvent("spell.tether.cast");
    public static DeferredHolder<SoundEvent, SoundEvent> PHEONIX_BEGIN_CAST = registerSoundEvent("spell.pheonix.begin_cast");
    public static DeferredHolder<SoundEvent, SoundEvent> PHEONIX_FINISH_CAST = registerSoundEvent("spell.pheonix.finish_cast");

    public static DeferredHolder<SoundEvent, SoundEvent> PHEONIX_ASCEND = registerSoundEvent("entity.pheonix.ascend");
    public static DeferredHolder<SoundEvent, SoundEvent> PHEONIX_STRIKE = registerSoundEvent("entity.pheonix.strike");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(SorcerySupplements.MODID, name)));
    }

}
