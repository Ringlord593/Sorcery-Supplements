package com.ringlord593.sorcery_supplements.util;

import com.ringlord593.sorcery_supplements.capability.ModdedMagicData;
import com.ringlord593.sorcery_supplements.capability.TetherSpellData;
import com.ringlord593.sorcery_supplements.network.SyncTetherStatePacket;
import com.ringlord593.sorcery_supplements.registry.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class TetherSpellHandler {

    public static void handleTether(Entity entity, double pX, double pY, double pZ) {
        Level level = entity.level();
        if (level instanceof ServerLevel server) {
            if (entity instanceof LivingEntity living) {
                if (living.hasEffect(ModEffects.TETHERED)) {
                    var data = ModdedMagicData.getPlayerMagicData(living).getTetherSpellData();
                    if (data.getIsCaster()) {
                        int[] target_ids = teleportTargets(server, data.getTargetEntities(server), pX, pY, pZ,
                               data);
                        if (living instanceof ServerPlayer player) {
                            PacketDistributor.sendToPlayer(player, new SyncTetherStatePacket(target_ids, 0, data.getIsCaster(), data.getIsMulticast()));
                        }
                    } else if (!data.getIsMulticast()
                            && server.getEntity(data.getSourceEntityUUID()) instanceof LivingEntity source) {
                        teleportSource(server, source, living, pX, pY, pZ);
                    }
                }
            }
        }
    }

    private static int[] teleportTargets(ServerLevel level, List<LivingEntity> targets, double pX, double pY, double pZ,
                                         TetherSpellData data) {
        int[] target_ids = new int[targets.size()];
        for (int i = 0; i < targets.size(); i++) {
            LivingEntity livingentity = targets.get(i);
            livingentity.unRide();
            Entity tpentity = livingentity.getType().create(level);
            if (tpentity != null) {
                tpentity.restoreFrom(livingentity);
                tpentity.setPos(pX, pY, pZ);
                tpentity.setYHeadRot(tpentity.getYRot());
                tpentity.setPortalCooldown();

                livingentity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                level.addDuringTeleport(tpentity);
                target_ids[i] = tpentity.getId();
            }
        }
        return target_ids;
    }

    private static void teleportSource(ServerLevel level, LivingEntity source, LivingEntity target, double pX,
                                       double pY, double pZ) {
        int radius = 10; // Maximum search radius
        BlockPos pos = BlockPos.containing(pX, pY, pZ);

        for (int layer = 1; layer <= radius; layer++) {
            // Iterate through the spiral pattern
            for (int xOffset = -layer; xOffset <= layer; xOffset++) {
                for (int zOffset = -layer; zOffset <= layer; zOffset++) {
                    if (Math.abs(xOffset) != layer && Math.abs(zOffset) != layer) {
                        continue; // Skip inner blocks, focus on the current layer
                    }

                    BlockPos testPos = pos.offset(xOffset, 0, zOffset);
                    // Check each block vertically within this position
                    for (int yOffset = -4; yOffset <= 4; yOffset++) {
                        BlockPos verticalPos = testPos.offset(0, yOffset, 0);
                        if (level.getBlockState(verticalPos.below()).isSolid()
                                && level.getBlockState(verticalPos).isAir()
                                && level.getBlockState(verticalPos.above()).isAir()) {

                            if (source instanceof ServerPlayer player) {
                                player.connection.teleport(verticalPos.getX(), verticalPos.getY(), verticalPos.getZ(),
                                        lookAtYaw(pos, verticalPos), lookAtPitch(pos, verticalPos, source, target));
                            } else {
                                source.moveTo(verticalPos, lookAtYaw(pos, verticalPos),
                                        lookAtPitch(pos, verticalPos, source, target));
                            }
                            return; // Exit as soon as a safe block is found
                        }
                    }
                }
            }
        }
        source.setPos(pX, pY, pZ);
        source.setYRot(target.getYRot());
        source.setXRot(target.getXRot());
    }

    private static float lookAtYaw(BlockPos targetPos, BlockPos sourcePos) {
        double deltaX = targetPos.getX() - sourcePos.getX();
        double deltaZ = targetPos.getZ() - sourcePos.getZ();
        return (float) (Math.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90;
    }

    private static float lookAtPitch(BlockPos targetPos, BlockPos sourcePos, LivingEntity source, LivingEntity target) {
        double deltaX = targetPos.getX() - sourcePos.getX();
        double deltaY = (targetPos.getY() + target.getEyeHeight() * 0.75)
                - (sourcePos.getY() + (source.getEyeHeight()));
        double deltaZ = targetPos.getZ() - sourcePos.getZ();
        return (float) -(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)) * (180 / Math.PI));
    }
}
