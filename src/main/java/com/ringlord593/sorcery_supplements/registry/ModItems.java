package com.ringlord593.sorcery_supplements.registry;

import com.ringlord593.sorcery_supplements.SorcerySupplements;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, SorcerySupplements.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    //public static final DeferredHolder<Item, Item> WIMPY_SPELL_BOOK = ITEMS.register("wimpy_spell_book", () -> new SpellBook(0, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

}
