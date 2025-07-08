package com.ringlord593.sorcery_supplements.spells;

import com.ringlord593.sorcery_supplements.SorcerySupplements;
import com.ringlord593.sorcery_supplements.capability.PhoenixCastData;
import com.ringlord593.sorcery_supplements.entities.spells.pheonix.PhoenixEntity;
import com.ringlord593.sorcery_supplements.registry.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class RagingPhoenixSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(SorcerySupplements.MODID, "raging_phoenix");


    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui." + SorcerySupplements.MODID + ".range",
                        Utils.stringTruncation(getRadius(spellLevel, caster), 1)),
                Component.translatable("ui." + SorcerySupplements.MODID + ".impact_damage",
                        Utils.stringTruncation(getDamage(spellLevel, caster), 1)),
                Component.translatable("ui." + SorcerySupplements.MODID + ".duration",
                        Utils.stringTruncation(getTime(spellLevel, caster), 1)));
    }

    public RagingPhoenixSpell() {
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 5;
        this.castTime = 50;
        this.baseManaCost = 40;
    }

    private final DefaultConfig defaultConfig = new DefaultConfig().setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.FIRE_RESOURCE).setMaxLevel(3).setCooldownSeconds(5).build();

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        var recasts = playerMagicData.getPlayerRecasts();
        if (!recasts.hasRecastForSpell(getSpellId())) {
            PhoenixEntity phoenix = new PhoenixEntity(entity, level);
            phoenix.setSpellPower(getSpellPower(spellLevel, entity));
            level.addFreshEntity(phoenix);
            playerMagicData.setAdditionalCastData(new PhoenixCastData(phoenix));
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.PHEONIX_BEGIN_CAST.get(), SoundSource.PLAYERS, 2, 1);
        }

        super.onServerPreCast(level, spellLevel, entity, playerMagicData);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource,
                       MagicData playerMagicData) {

        var recasts = playerMagicData.getPlayerRecasts();
        if (!recasts.hasRecastForSpell(getSpellId())) {
            if (playerMagicData.getAdditionalCastData() instanceof PhoenixCastData data) {
                if (data.getPheonix((ServerLevel) level) instanceof PhoenixEntity phoenix) {
                    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.PHEONIX_FINISH_CAST.get(), SoundSource.PLAYERS, 2, 1);
                    phoenix.setState(PhoenixEntity.State.ASCEND);

                    recasts.addRecast(new RecastInstance(getSpellId(), spellLevel, getRecastCount(spellLevel, entity),
                            250, castSource, data), playerMagicData);
                }
            }
        } else {
            var instance = recasts.getRecastInstance(this.getSpellId());
            if (instance != null && instance.getCastData() instanceof PhoenixCastData data) {
                if (data.getPheonix((ServerLevel) level) instanceof PhoenixEntity phoenix) {
                    Vec3 targetArea = Utils.moveToRelativeGroundLevel(level,
                            Utils.raycastForEntity(level, entity, 60, true).getLocation(), 12);
                    TargetedAreaEntity target = TargetedAreaEntity.createTargetAreaEntity(level, targetArea, 2f * (getSpellPower(spellLevel, entity) / 6),
                            0xFF6A00);
                    phoenix.setTarget(target);
                    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), getSchoolType().getCastSound(), SoundSource.PLAYERS, 2, 1.5f);
                }
            }
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }


    @Override
    public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult,
                                 ICastDataSerializable castDataSerializable) {
        super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
    }

    private float getDamage(int spellLevel, LivingEntity entity) {
        return getSpellPower(spellLevel, entity) * 0.25f;
    }

    private float getRadius(int spellLevel, LivingEntity entity) {
        return (2 * spellLevel + 4) + (1 * .125f * getSpellPower(spellLevel, entity));
    }

    private float getTime(int spellLevel, LivingEntity entity) {
        return (getSpellPower(spellLevel, entity) + 1) * 3;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.ANIMATION_CONTINUOUS_CAST;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.ANIMATION_LONG_CAST_FINISH;
    }

    @Override
    public int getSpellCooldown() {
        return super.getSpellCooldown();
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return 2;
    }

    @Override
    public int getEffectiveCastTime(int spellLevel, LivingEntity entity) {
        MagicData playerMagicData = MagicData.getPlayerMagicData(entity);
        var recasts = playerMagicData.getPlayerRecasts();
        if (recasts.hasRecastForSpell(getSpellId())) {
            return 1;
        }
        return super.getEffectiveCastTime(spellLevel, entity);
    }

    @Override
    public ICastDataSerializable getEmptyCastData() {
        return new PhoenixCastData();
    }
}
