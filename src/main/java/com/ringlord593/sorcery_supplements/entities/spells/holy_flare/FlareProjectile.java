package com.ringlord593.sorcery_supplements.entities.spells.holy_flare;

import java.util.Optional;

import net.minecraft.core.Holder;
import org.joml.Math;
import org.joml.Vector3f;

import com.ringlord593.sorcery_supplements.registry.ModEntities;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.SparkParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class FlareProjectile extends AbstractMagicProjectile {

    int spellLevel = 0;
    float spellPower = 0;

    public FlareProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FlareProjectile(Level levelIn, LivingEntity shooter, int spellLevel, float spellPower) {
        super(ModEntities.FLARE_PROJECTILE.get(), levelIn);
        this.spellLevel = spellLevel;
        this.spellPower = spellPower;
        setOwner(shooter);
        this.setNoGravity(false);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        Level level = level();
        if (!level.isClientSide) {
            spawnFlare();
        }
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Level level = level();
        if (!level.isClientSide) {
            spawnFlare();
            entityHitResult.getEntity().setRemainingFireTicks(60);
            entityHitResult.getEntity().hurt(DamageSources.get(level(), DamageTypes.ON_FIRE), getDamage());
        }
    }

    @Override
    public void trailParticles() {
        for (int i = 0; i < 1; i++) {
            double speed = .05;
            double dx = Utils.random.nextDouble() * 2 * speed - speed;
            double dy = Utils.random.nextDouble() * 2 * speed - speed;
            double dz = Utils.random.nextDouble() * 2 * speed - speed;
            level().addParticle(ParticleTypes.ELECTRIC_SPARK, this.getX() + dx, this.getY() + dy, this.getZ() + dz, dx,
                    dy + 1, dz);
        }
    }

    @Override
    public float getSpeed() {
        return 1.4f * Math.clamp(1f, 2f, (spellPower * 0.1f));
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.of(SoundRegistry.ICE_IMPACT);
    }

    public void spawnFlare() {
        float radius = getRadius(spellLevel);
        Level level = level();
        Vec3 center = position();
        this.playSound(SoundRegistry.BLACK_HOLE_CAST.get(), getRadius(spellLevel) / 2f, 3);

        FlareEntity flare = new FlareEntity(level, (LivingEntity) getOwner(), spellLevel, spellPower);
        flare.setRadius(radius);
        flare.setDamage(this.getDamage() * 0.25f);
        flare.moveTo(center);
        level.addFreshEntity(flare);
        flare.spawnLightBlocks(center);
        MagicManager.spawnParticles(level, new BlastwaveParticleOptions(new Vector3f(1, 1f, 0.9f), radius),
                center.x, center.y, center.z, 1, 0, 0, 0, 0, true);
        MagicManager.spawnParticles(level, ParticleTypes.FLASH, center.x, center.y, center.z, 1, 0, 0, 0, 0, true);
        MagicManager.spawnParticles(level, new SparkParticleOptions(new Vector3f(1, 1f, 0.9f)), center.x, center.y,
                center.z, 150, .1, .1, .1, .4, true);
        discard();
    }

    private float getRadius(int spellLevel) {
        return (2 * spellLevel + 4) + (1 * .125f * spellPower);
    }

    @Override
    public boolean isNoGravity() {
        return false;
    }

    public void tick() {
        super.tick();
        Level level = level();
        if (!level.isClientSide) {
            if (tickCount > 12 * Math.clamp(1f, 2f, (1 + (spellPower * 0.1f)))) {
                this.discard();
                spawnFlare();
            }
        }
    }

    @Override
    public void impactParticles(double x, double y, double z) {
        // TODO Auto-generated method stub

    }
}
