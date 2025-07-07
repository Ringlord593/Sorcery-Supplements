package com.ringlord593.sorcery_supplements.entities.spells.holy_flare;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ringlord593.sorcery_supplements.SorcerySupplements;
import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class FlareEntityRenderer extends EntityRenderer<FlareEntity> {
    public FlareEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    private static final ResourceLocation CENTER_TEXTURE = SorcerySupplements.id("textures/entity/flare/flare.png");
    private static final ResourceLocation BEAM_TEXTURE = SorcerySupplements.id("textures/entity/flare/beam.png");

    @Override
    public void render(FlareEntity entity, float pEntityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() / 2, 0);

        float entityScale = entity.getBbWidth() * 0.1f;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        poseStack.scale(.5f * entityScale, .5f * entityScale, .5f * entityScale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(90f));
        poseStack.translate(5, 0, 0);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(CENTER_TEXTURE));

        consumer.addVertex(poseMatrix, 0, -8, -8).setColor(255, 255, 255, 255).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, 0, 8, -8).setColor(255, 255, 255, 255).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, 0, 8, 8).setColor(255, 255, 255, 255).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, 0, -8, 8).setColor(255, 255, 255, 255).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        poseStack.popPose();
        poseStack.pushPose();

        poseStack.translate(0, entity.getBoundingBox().getYsize() / 2, 0);
        float animationProgress = (entity.tickCount + partialTicks) / 200.0F;
        //float fadeProgress = Math.min(animationProgress > 0.8F ? (animationProgress - 0.8F) / 0.2F : 0.0F, 1.0F);
        float fadeProgress = .5f;
        RandomSource randomSource = RandomSource.create(432L);
//        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lightning());
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.energySwirl(BEAM_TEXTURE, 0, 0));
        //poseStack.translate(0.0D, -1.0D, -2.0D);


        for (int i = 0; i < 18; ++i) {
            poseStack.mulPose(Axis.XP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(randomSource.nextFloat() * 360.0F + animationProgress * 90.0F));
            float size1 = (randomSource.nextFloat() * 10.0F + 5.0F + fadeProgress * 5.0F) * entityScale * .4f;
            //float size2 = randomSource.nextFloat() * 2.0F + 1.0F + fadeProgress * 2.0F;
            Matrix4f matrix = poseStack.last().pose();
            Matrix3f normalMatrix2 = poseStack.last().normal();

            int alpha = (int) (255.0F * (1.0F - fadeProgress));
            drawTriangle(vertexConsumer, matrix, normalMatrix2, size1);
        }

        poseStack.popPose();

        super.render(entity, pEntityYaw, partialTicks, poseStack, bufferSource, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(FlareEntity pEntity) {
        return IcicleRenderer.TEXTURE;
    }

    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);


    private static void drawTriangle(VertexConsumer consumer, Matrix4f poseMatrix, Matrix3f normalMatrix, float size) {
        consumer.addVertex(poseMatrix, 0, 0, 0).setColor(255, 0, 255, 255).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, 0, 3 * size, -1 * size).setColor(0, 0, 0, 0).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, 0, 3 * size, 1 * size).setColor(0, 0, 0, 0).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, 0, 0, 0).setColor(255, 0, 255, 255).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f);
    }

}