package com.ringlord593.sorcery_supplements.network;

import com.ringlord593.sorcery_supplements.SorcerySupplements;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = SorcerySupplements.MODID)
public class PayloadHandler {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar payloadRegistrar = event.registrar(SorcerySupplements.MODID).versioned("1.0.0").optional();

        payloadRegistrar.playToClient(SyncTetherStatePacket.TYPE, SyncTetherStatePacket.STREAM_CODEC, SyncTetherStatePacket::handle);

    }
}
