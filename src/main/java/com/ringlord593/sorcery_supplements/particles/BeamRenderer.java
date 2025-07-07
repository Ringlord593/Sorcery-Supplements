package com.ringlord593.sorcery_supplements.particles;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.ringlord593.sorcery_supplements.SorcerySupplements;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class BeamRenderer {
    private static final ResourceLocation LASER_TEXTURE = SorcerySupplements.id("textures/particle/glowing_beam.png");

    public static final RenderType LASER_BEAM = RenderType.create("laser_beam",
            DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 256,
            false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(LASER_TEXTURE, false, false))
                    .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(false)
    );

    public static void renderBeam(PoseStack poseStack, MultiBufferSource bufferSource,
                                  Vec3 start, Vec3 end, float alpha) {

        Vec3 dir = end.subtract(start);
        double length = dir.length();
        Vec3 norm = dir.normalize();

        // Calculate perpendicular vectors for quad orientation
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 side = norm.cross(up).normalize().scale(0.1); // Beam thickness

        // Animate shimmer with sine wave
        float shimmer = (float) (Math.sin(System.currentTimeMillis() / 100.0) * 0.5 + 0.5);

        VertexConsumer buffer = bufferSource.getBuffer(LASER_BEAM);
        Matrix4f mat = poseStack.last().pose();

        Vec3 p1 = start.add(side);
        Vec3 p2 = start.subtract(side);
        Vec3 p3 = end.subtract(side);
        Vec3 p4 = end.add(side);

        buffer.addVertex(mat, (float) p1.x, (float) p1.y, (float) p1.z)
                .setColor(255, 0, 255, (int) (255 * alpha))
                .setUv(0, 0);
        buffer.addVertex(mat, (float) p2.x, (float) p2.y, (float) p2.z)
                .setColor(255, 0, 255, (int) (255 * alpha))
                .setUv(1, 0);
        buffer.addVertex(mat, (float) p3.x, (float) p3.y, (float) p3.z)
                .setColor(255, 0, 255, (int) (255 * alpha))
                .setUv(1, 1);
        buffer.addVertex(mat, (float) p4.x, (float) p4.y, (float) p4.z)
                .setColor(255, 0, 255, (int) (255 * alpha))
                .setUv(0, 1);
    }
}