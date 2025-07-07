package com.ringlord593.sorcery_supplements.events;

import com.ringlord593.sorcery_supplements.SorcerySupplements;
import com.ringlord593.sorcery_supplements.capability.ModdedMagicData;
import com.ringlord593.sorcery_supplements.network.SyncTetherStatePacket;
import com.ringlord593.sorcery_supplements.registry.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@EventBusSubscriber(modid = SorcerySupplements.MODID)
public class CapabilityAttachEvent {

    @SubscribeEvent
    public static void loggin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            var data = ModdedMagicData.getPlayerMagicData(player).getTetherSpellData();
            if (event.getEntity().hasEffect(ModEffects.TETHERED)) {
                ServerLevel level = (ServerLevel) player.level();
                List<LivingEntity> entities = data.getTargetEntities(level);
                int[] target_ids = new int[entities.size()];
                for (int i = 0; i < entities.size(); i++) {
                    target_ids[i] = entities.get(i).getId();
                }
                int source = data.getIsCaster() ? 0 : level.getEntity(data.getSourceEntityUUID()).getId();
                PacketDistributor.sendToPlayer(player, new SyncTetherStatePacket(target_ids, source, data.getIsCaster(), data.getIsMulticast()));
            }
        }
    }
}
