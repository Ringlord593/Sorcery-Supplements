package com.ringlord593.sorcery_supplements.spells;

import com.ringlord593.sorcery_supplements.SorcerySupplements;
import com.ringlord593.sorcery_supplements.capability.ModdedMagicData;
import com.ringlord593.sorcery_supplements.network.SyncTetherStatePacket;
import com.ringlord593.sorcery_supplements.registry.ModEffects;
import com.ringlord593.sorcery_supplements.registry.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MultiTargetEntityCastData;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AutoSpellConfig
public class TetherSpell extends AbstractSpell {

    private final ResourceLocation spellId = new ResourceLocation(SorcerySupplements.MODID, "tether");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui." + SorcerySupplements.MODID + ".duration",
                Utils.stringTruncation((double) getDuration(spellLevel, caster) / 20, 1)));
    }

    public TetherSpell() {
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 6;
        this.spellPowerPerLevel = 2;
        this.castTime = 12;
        this.baseManaCost = 30;
    }

    private final DefaultConfig defaultConfig = new DefaultConfig().setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE).setMaxLevel(5).setCooldownSeconds(15).build();

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
       if(entity != null) {
           return entity.isCrouching() ? spellLevel : 0;
       }
       else return 0;
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, .6f);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource,
                       MagicData playerMagicData) {
        if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetEntityCastData) {
            if (entity.isCrouching()) {
                var recasts = playerMagicData.getPlayerRecasts();
                if (!recasts.hasRecastForSpell(getSpellId())) {
                    recasts.addRecast(
                            new RecastInstance(getSpellId(), spellLevel, getRecastCount(spellLevel, entity), 80,
                                    castSource,
                                    new MultiTargetEntityCastData(targetEntityCastData.getTarget((ServerLevel) level))),
                            playerMagicData);
                } else {
                    var instance = recasts.getRecastInstance(this.getSpellId());
                    if (instance != null && instance.getCastData() instanceof MultiTargetEntityCastData targetingData) {
                        targetingData.addTarget(targetEntityCastData.getTargetUUID());
                    }
                }
            } else {
                if (!level.isClientSide()) {

                    var recasts = playerMagicData.getPlayerRecasts();
                    if (recasts.hasRecastForSpell(getSpellId())) {
                        var instance = recasts.getRecastInstance(this.getSpellId());
                        recasts.removeRecast(instance, RecastResult.USER_CANCEL);
                    }
                    List<UUID> targets = new ArrayList<>();
                    targets.add(targetEntityCastData.getTargetUUID());

                    LivingEntity target = targetEntityCastData.getTarget((ServerLevel) level);


                    finishCast(entity, targets, spellLevel, false);
                }
            }

        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);

    }

    @Override
    public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult,
                                 ICastDataSerializable castDataSerializable) {
        if (recastResult != RecastResult.USER_CANCEL) {
            super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
            var level = serverPlayer.level();
            Vec3 origin = serverPlayer.getEyePosition().add(serverPlayer.getForward().normalize().scale(.2f));
            level.playSound(null, origin.x, origin.y, origin.z, ModSounds.TETHER.get(), SoundSource.PLAYERS, 4.0f,
                    1.0f);
            if (castDataSerializable instanceof MultiTargetEntityCastData targetingData) {
                finishCast(serverPlayer, targetingData.getTargets(), recastInstance.getSpellLevel(), true);
            }
        }
    }

    public void finishCast(LivingEntity source, List<UUID> targets, int spelllevel, boolean multicast) {
        var data = ModdedMagicData.getPlayerMagicData(source);
        data.getTetherSpellData().setIsCaster(true);
        data.getTetherSpellData().setIsMulticast(multicast);
        data.getTetherSpellData().setSourceEntityUUID(source.getUUID());
        data.getTetherSpellData().setTargetEntityUUIDs(targets);
        if (source instanceof ServerPlayer player) {
            int[] target_ids = new int[targets.size()];
            List<LivingEntity> entities = data.getTetherSpellData().getTargetEntities((ServerLevel) source.level());
            for (int i = 0; i < entities.size(); i++) {
                target_ids[i] = entities.get(i).getId();
            }
            PacketDistributor.sendToPlayer(player, new SyncTetherStatePacket(target_ids, 0, data.getTetherSpellData().getIsCaster(), multicast));
        }
        source.addEffect(new MobEffectInstance(ModEffects.TETHERED, getDuration(spelllevel, source), 1));

        targets.forEach(uuid -> {
            var target = (LivingEntity) ((ServerLevel) source.level()).getEntity(uuid);
            if (target != null) {
                var targetdata = ModdedMagicData.getPlayerMagicData(source);
                targetdata.getTetherSpellData().setIsCaster(false);
                targetdata.getTetherSpellData().setIsMulticast(multicast);
                targetdata.getTetherSpellData().setSourceEntityUUID(source.getUUID());
                if (target instanceof ServerPlayer player) {
                    PacketDistributor.sendToPlayer(player, new SyncTetherStatePacket(null, source.getId(), data.getTetherSpellData().getIsCaster(), multicast));
                }
                target.addEffect(new MobEffectInstance(ModEffects.TETHERED, getDuration(spelllevel, source), 1));
            }
        });
    }


    public int getDuration(int spellLevel, LivingEntity entity) {
        return (int) (100 * getSpellPower(spellLevel, entity));
    }


    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.ANIMATION_LONG_CAST;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.ANIMATION_LONG_CAST_FINISH;
    }

    @Override
    public ICastDataSerializable getEmptyCastData() {
        return new MultiTargetEntityCastData();
    }
}
