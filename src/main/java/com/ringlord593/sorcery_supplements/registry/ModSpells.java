package com.ringlord593.sorcery_supplements.registry;

import com.ringlord593.sorcery_supplements.SorcerySupplements;
import com.ringlord593.sorcery_supplements.spells.HolyFlareSpell;
import com.ringlord593.sorcery_supplements.spells.RagingPhoenixSpell;
import com.ringlord593.sorcery_supplements.spells.TetherSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSpells {
    private static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, SorcerySupplements.MODID);

    private static Supplier<AbstractSpell> registerSpell(AbstractSpell spell) {
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }

    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }

    //BLOOD

    //ENDER
    public static final Supplier<AbstractSpell> TETHER_SPELL = registerSpell(new TetherSpell());

    //EVOCATION

    //FIRE
    public static final Supplier<AbstractSpell> PHOENIX_SPELL = registerSpell(new RagingPhoenixSpell());

    //HOLY
    public static final Supplier<AbstractSpell> HOLY_FLARE_SPELL = registerSpell(new HolyFlareSpell());

    //ICE

    //LIGHTNING

    //NATURE

    //VOID

}