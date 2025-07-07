package com.ringlord593.sorcery_supplements.setup;

import com.ringlord593.sorcery_supplements.SorcerySupplements;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = SorcerySupplements.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CommonSetup {
    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {

    }


}