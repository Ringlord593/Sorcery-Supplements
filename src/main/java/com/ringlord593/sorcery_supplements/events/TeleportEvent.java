package com.ringlord593.sorcery_supplements.events;

import com.ringlord593.sorcery_supplements.SorcerySupplements;
import com.ringlord593.sorcery_supplements.entities.spells.holy_flare.FlareEntity;
import com.ringlord593.sorcery_supplements.particles.BeamRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

@EventBusSubscriber(modid = SorcerySupplements.MODID)
public class TeleportEvent {

    @SubscribeEvent
    public static void onEntityTeleport(EntityTeleportEvent event) {
        Level level = event.getEntity().level();
        if (!level.isClientSide) {
            if (event.getEntity() instanceof FlareEntity flare) {
                flare.spawnLightBlocks(event.getTarget());
            }
        }

    }

    @SubscribeEvent
    public static void RenderBeamThirdPerson(RenderPlayerEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (event.getEntity().equals(minecraft.player) && !minecraft.options.getCameraType().isFirstPerson()) {
            BeamRenderer.renderBeam(event.getPoseStack());

        }
    }

    @SubscribeEvent
    public static void RenderBeamFirstPerson(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.options.getCameraType().isFirstPerson()) {
                event.getPoseStack().pushPose();
                event.getPoseStack().translate(0, -1.8, 0);
                BeamRenderer.renderBeam(event.getPoseStack());
                event.getPoseStack().popPose();
            }
        }
    }



}
