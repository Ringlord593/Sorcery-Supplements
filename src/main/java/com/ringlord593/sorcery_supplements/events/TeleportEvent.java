package com.ringlord593.sorcery_supplements.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ringlord593.sorcery_supplements.SorcerySupplements;
import com.ringlord593.sorcery_supplements.capability.ClientModdedMagicData;
import com.ringlord593.sorcery_supplements.entities.spells.holy_flare.FlareEntity;
import com.ringlord593.sorcery_supplements.particles.BeamRenderer;
import com.ringlord593.sorcery_supplements.registry.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import org.joml.Matrix4f;

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
    public static void test(RenderPlayerEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (event.getEntity().equals(minecraft.player) && !minecraft.options.getCameraType().isFirstPerson()) {
            renderbeam(event.getPoseStack());

        }
    }

    @SubscribeEvent
    public static void test2(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.options.getCameraType().isFirstPerson()) {
                event.getPoseStack().pushPose();
                event.getPoseStack().translate(0, -1.8, 0);
                renderbeam(event.getPoseStack());
                event.getPoseStack().popPose();
            }
        }
    }

    public static void renderbeam(PoseStack posestack) {

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player.hasEffect(ModEffects.TETHERED)) {
            ClientModdedMagicData data = ClientModdedMagicData.getInstance();
            if (data.isCaster) {
                for (int i = 0; i < data.targetEntityIds.length; i++) {
                    if (minecraft.level.getEntity(data.targetEntityIds[i]) instanceof LivingEntity entity) {
                     //   renderBeamWithTexture(entity, minecraft.player, minecraft.getTimer().getGameTimeDeltaPartialTick(false), posestack, minecraft.renderBuffers().bufferSource());
                        BeamRenderer.renderBeam(posestack, minecraft.renderBuffers().bufferSource(), minecraft.player.getRopeHoldPosition(minecraft.getTimer().getGameTimeDeltaPartialTick(false)), entity.position(), 1);
                    }
                }
            } else {
                if (minecraft.level.getEntity(data.sourceEntityId) instanceof LivingEntity entity) {
                   // renderBeamWithTexture(entity, minecraft.player, minecraft.getTimer().getGameTimeDeltaPartialTick(false), posestack,minecraft.renderBuffers().bufferSource());
                    BeamRenderer.renderBeam(posestack, minecraft.renderBuffers().bufferSource(), minecraft.player.getRopeHoldPosition(minecraft.getTimer().getGameTimeDeltaPartialTick(false)), entity.position(), 1);
                }
            }

        }
    }
    private static final ResourceLocation BEAM_TEXTURE = SorcerySupplements.id("textures/entity/flare/beam.png");
    public static void renderBeamWithTexture(LivingEntity pLeashHolder, LivingEntity pEntityLiving, float pPartialTicks,
                                             PoseStack pPoseStack, MultiBufferSource pBuffer) {
        pPoseStack.pushPose();
        Vec3 vec3 = pLeashHolder.getPosition(pPartialTicks).add(0.0D, (double) pLeashHolder.getEyeHeight() * 0.6D,
                0.0D);
        double d0 = (double) (Mth.lerp(pPartialTicks, pEntityLiving.yBodyRotO, pEntityLiving.yBodyRot)
                * ((float) Math.PI / 180F)) + (Math.PI / 2D);
        Vec3 vec31 = Vec3.ZERO.add(0, pEntityLiving.getEyeHeight() * 0.6D, 0);
        double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
        double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
        double d3 = Mth.lerp((double) pPartialTicks, pEntityLiving.xo, pEntityLiving.getX()) + d1;
        double d4 = Mth.lerp((double) pPartialTicks, pEntityLiving.yo, pEntityLiving.getY()) + vec31.y;
        double d5 = Mth.lerp((double) pPartialTicks, pEntityLiving.zo, pEntityLiving.getZ()) + d2;
        pPoseStack.translate(d1, vec31.y, d2);
        float f = (float) (vec3.x - d3);
        float f1 = (float) (vec3.y - d4);
        float f2 = (float) (vec3.z - d5);
        float f3 = 0.07F;
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.energySwirl(BEAM_TEXTURE, 0, 0));
        Matrix4f matrix4f = pPoseStack.last().pose();
        float f4 = Mth.invSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
        float f5 = f2 * f4;
        float f6 = f * f4;
        int i = 15;

        for (int i1 = 0; i1 <= 24; ++i1) {
            addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, i, i, i, f3, f3, f5, f6, i1, false);
        }

        for (int j1 = 24; j1 >= 0; --j1) {
            addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, i, i, i, f3, f3, f5, f6, j1, true);
        }

        pPoseStack.popPose();
    }

    private static void addVertexPair(VertexConsumer pConsumer, Matrix4f pMatrix, float p_174310_, float p_174311_,
                                      float p_174312_, int pEntityBlockLightLevel, int pLeashHolderBlockLightLevel, int pEntitySkyLightLevel,
                                      int pLeashHolderSkyLightLevel, float p_174317_, float p_174318_, float p_174319_, float p_174320_,
                                      int pIndex, boolean p_174322_) {
        float f = (float) pIndex / 24.0F;
        int i = (int) Mth.lerp(f, (float) pEntityBlockLightLevel, (float) pLeashHolderBlockLightLevel);
        int j = (int) Mth.lerp(f, (float) pEntitySkyLightLevel, (float) pLeashHolderSkyLightLevel);
        int k = LightTexture.pack(i, j);
        float f2 = 0.68F;
        float f3 = 0F;
        float f4 = 1F;
        float f5 = p_174310_ * f;
        float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
        float f7 = p_174312_ * f;
        pConsumer.addVertex(pMatrix, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).setColor(f2, f3, f4, 0.3F).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        pConsumer.addVertex(pMatrix, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).setColor(f2, f3, f4, 0.3F).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f);

    }
}
