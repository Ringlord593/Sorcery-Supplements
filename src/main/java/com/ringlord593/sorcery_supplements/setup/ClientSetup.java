package com.ringlord593.sorcery_supplements.setup;

import com.ringlord593.sorcery_supplements.SorcerySupplements;
import com.ringlord593.sorcery_supplements.entities.spells.holy_flare.FlareEntityRenderer;
import com.ringlord593.sorcery_supplements.entities.spells.holy_flare.FlareProjectileRenderer;
import com.ringlord593.sorcery_supplements.entities.spells.pheonix.PhoenixEntityRenderer;
import com.ringlord593.sorcery_supplements.registry.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
@EventBusSubscriber(modid = SorcerySupplements.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
    }

    @SubscribeEvent
    public static void rendererRegister(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FLARE_PROJECTILE.get(), FlareProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.FLARE_ENTITY.get(), FlareEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.PHOENIX_ENTITY.get(), PhoenixEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {


    }
}