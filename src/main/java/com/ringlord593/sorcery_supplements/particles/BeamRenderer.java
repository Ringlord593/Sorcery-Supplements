package com.ringlord593.sorcery_supplements.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ringlord593.sorcery_supplements.SorcerySupplements;
import com.ringlord593.sorcery_supplements.capability.ClientModdedMagicData;
import com.ringlord593.sorcery_supplements.registry.ModEffects;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BeamRenderer {
    private static final ResourceLocation LASER_TEXTURE = SorcerySupplements.id("textures/particle/glowing_beam.png");

    public static void renderBeam(PoseStack posestack) {

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player.hasEffect(ModEffects.TETHERED)) {
            ClientModdedMagicData data = ClientModdedMagicData.getInstance();
            if (data.isCaster) {
                for (int i = 0; i < data.targetEntityIds.length; i++) {
                    if (minecraft.level.getEntity(data.targetEntityIds[i]) instanceof LivingEntity entity) {
                        BeamRenderer.renderBeam(entity, minecraft.player, minecraft.getTimer().getGameTimeDeltaPartialTick(false), posestack, minecraft.renderBuffers().bufferSource());
                    }
                }
            } else {
                if (minecraft.level.getEntity(data.sourceEntityId) instanceof LivingEntity entity) {
                    BeamRenderer.renderBeam(entity, minecraft.player, minecraft.getTimer().getGameTimeDeltaPartialTick(false), posestack, minecraft.renderBuffers().bufferSource());
                }
            }

        }
    }

    public static void renderBeam(Entity beamStartEntity, Entity beamTargetEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        poseStack.pushPose();

        // Get interpolated start and target positions
        Vec3 startVec = beamStartEntity.getPosition(partialTick).add(0, beamStartEntity.getEyeHeight() * 0.6D, 0);
        double yaw = Math.toRadians(beamTargetEntity.getPreciseBodyRotation(partialTick)) + Math.PI / 2;
        Vec3 eyeOffset = new Vec3(0, beamTargetEntity.getEyeHeight() * 0.6D, 0);

        double offsetX = Math.cos(yaw) * eyeOffset.z + Math.sin(yaw) * eyeOffset.x;
        double offsetZ = Math.sin(yaw) * eyeOffset.z - Math.cos(yaw) * eyeOffset.x;

        double targetX = Mth.lerp(partialTick, beamTargetEntity.xo, beamTargetEntity.getX()) + offsetX;
        double targetY = Mth.lerp(partialTick, beamTargetEntity.yo, beamTargetEntity.getY()) + eyeOffset.y;
        double targetZ = Mth.lerp(partialTick, beamTargetEntity.zo, beamTargetEntity.getZ()) + offsetZ;

        poseStack.translate(offsetX, eyeOffset.y, offsetZ);

        Vec3 direction = new Vec3(startVec.x - targetX, startVec.y - targetY, startVec.z - targetZ);
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.energySwirl(SpellRenderingHelper.SOLID, 0, 0));

        int segments = 24;

        RandomSource rand = RandomSource.create(); // Create once outside the loop
        long seed = Minecraft.getInstance().level.getGameTime(); // Time-based seed // Generate a consistent random offset per segment

        double beamLength = beamStartEntity.distanceTo(beamTargetEntity);

        float waveSpeed = 0.3F;
        float minFreq = 0.1F;
        float maxFreq = 25.0F;
        float minAmp = 0.01F;
        float maxAmp = 0.1F;

        // Clamp the beamLength to avoid division by zero or overly fast ripple
        float waveFrequency = Mth.lerp(Mth.clamp((float) beamLength / 50.0F, 0.0F, 1.0F), minFreq, maxFreq);
        float waveAmplitude = Mth.lerp(Mth.clamp((float) beamLength / 50.0F, 0.0F, 1.0F), minAmp, maxAmp);
        long gameTime = Minecraft.getInstance().level.getGameTime();

        Vec3 prevTo = null;

        for (int i = 0; i <= segments; i++) {
            float progress = (float) i / segments;

            Vec3 current = new Vec3(direction.x * progress, (direction.y > 0.0F) ? direction.y * (0.5F * progress + 0.5F * progress * progress) : direction.y - direction.y * (1.0F - progress) * (1.0F - progress), direction.z * progress);
            rand.setSeed(seed + i * 31L); // Use a prime multiplier for better spread
            float phaseJitter = rand.nextFloat() * 2.0F * Mth.PI; // Random phase shift
            float amplitudeJitter = waveAmplitude + rand.nextFloat() * 0.03F; // Slight amplitude variation
            float wave = progress * waveFrequency * Mth.TWO_PI + gameTime * waveSpeed + phaseJitter;
            // Y-axis wave
            float waveY = Mth.sin(wave) * amplitudeJitter;
            // X-axis wave (shift the beam horizontally for wobble)
            float waveX = Mth.cos(wave) * amplitudeJitter;
            current = current.add(waveX, waveY, 0);

            if (prevTo != null) {
                float uvMin = (float) (i - 1) / segments;
                int r = 195, g = 0, b = 255, a = 200;
                float beamWidth = 0.025F, beamHeight = 0.025F;
                drawQuad(prevTo, current, beamWidth, beamHeight, poseStack.last(), buffer, r, g, b, a, uvMin, progress);

                buffer = bufferSource.getBuffer(RenderType.energySwirl(LASER_TEXTURE, 0, 0));
                drawQuad(prevTo, current, beamWidth * 4, beamHeight * 4, poseStack.last(), buffer, r / 2, 0, b / 2, a / 4, uvMin, progress);
            }

            prevTo = current;
        }

        poseStack.popPose();
    }

    public static void drawQuad(Vec3 from, Vec3 to, float width, float height, PoseStack.Pose pose, VertexConsumer consumer, int r, int g, int b, int a, float uvMin, float uvMax) {
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        float halfWidth = width * .5f;
        float halfHeight = height * .5f;

        consumer.addVertex(poseMatrix, (float) from.x - halfWidth, (float) from.y - halfHeight, (float) from.z).setColor(r, g, b, a).setUv(0f, uvMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0f, 0f, 1f);
        consumer.addVertex(poseMatrix, (float) from.x + halfWidth, (float) from.y + halfHeight, (float) from.z).setColor(r, g, b, a).setUv(1f, uvMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0f, 0f, 1f);
        consumer.addVertex(poseMatrix, (float) to.x + halfWidth, (float) to.y + halfHeight, (float) to.z).setColor(r, g, b, a).setUv(1f, uvMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0f, 0f, 1f);
        consumer.addVertex(poseMatrix, (float) to.x - halfWidth, (float) to.y - halfHeight, (float) to.z).setColor(r, g, b, a).setUv(0f, uvMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0f, 0f, 1f);

    }


}