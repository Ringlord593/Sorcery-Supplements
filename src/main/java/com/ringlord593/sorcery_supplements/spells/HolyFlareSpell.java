package com.ringlord593.sorcery_supplements.spells;

import com.ringlord593.sorcery_supplements.SorcerySupplements;
import com.ringlord593.sorcery_supplements.entities.spells.holy_flare.FlareProjectile;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class HolyFlareSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(SorcerySupplements.MODID, "holy_flare");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui." + SorcerySupplements.MODID + ".range", Utils.stringTruncation(getRadius(spellLevel, caster), 1)),
                Component.translatable("ui." + SorcerySupplements.MODID + ".impact_damage", Utils.stringTruncation(getDamage(spellLevel, caster), 1)),
                Component.translatable("ui." + SorcerySupplements.MODID + ".duration", Utils.stringTruncation(getTime(spellLevel, caster), 1)));
    }

    public HolyFlareSpell() {
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 6;
        this.spellPowerPerLevel = 1;
        this.castTime = 18;
        this.baseManaCost = 40;
    }

    private final DefaultConfig defaultConfig = new DefaultConfig().setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.HOLY_RESOURCE).setMaxLevel(10).setCooldownSeconds(75).build();

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
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource,
                       MagicData playerMagicData) {
        FlareProjectile flare = new FlareProjectile(level, entity, spellLevel, getSpellPower(spellLevel, entity));
        flare.setPos(entity.position().add(0, entity.getEyeHeight() - flare.getBoundingBox().getYsize() * .5f, 0));
        flare.shoot(entity.getLookAngle());
        flare.setDamage(getDamage(spellLevel, entity));
        flare.setNoGravity(true);
        level.addFreshEntity(flare);

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
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
        return SpellAnimations.ANIMATION_LONG_CAST;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.ANIMATION_LONG_CAST_FINISH;
    }

    @Override
    public int getSpellCooldown() {
        return super.getSpellCooldown();
    }

}
