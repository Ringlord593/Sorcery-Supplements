package com.ringlord593.sorcery_supplements.entities.spells.pheonix;

import com.ringlord593.sorcery_supplements.registry.ModEntities;
import com.ringlord593.sorcery_supplements.registry.ModSounds;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PhoenixEntity extends Entity implements AntiMagicSusceptible, GeoEntity {

    protected static final RawAnimation SPAWN_ANIM = RawAnimation.begin().thenPlay("spawn");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Float> SPELL_POWER = SynchedEntityData.defineId(PhoenixEntity.class,
            EntityDataSerializers.FLOAT);

    private PhoenixEntity.State state = State.BIRTH;
    private LivingEntity owner;
    private TargetedAreaEntity target;
    private Vec3 startPos;
    private boolean strike;
    private float damage;
    float speed = 1.6f; // Initial speed
    private double interpTime = 0;

    public PhoenixEntity(LivingEntity owner, Level pLevel) {
        super(ModEntities.PHOENIX_ENTITY.get(), pLevel);
        this.owner = owner;
    }

    public PhoenixEntity(EntityType<? extends Entity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SPELL_POWER, 0.0f);
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        discard();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.state == State.ASCEND) {
            interpTime++;
            ascend(interpTime / 10);
        } else if (this.state == State.IDLE) {
            if (strike) {
                setState(State.STRIKE);
            }
        } else if (this.state == State.STRIKE) {
            double currentDistance = new Vec3(getX(), 0, getZ()).distanceTo(new Vec3(target.getX(), 0, target.getZ()));
            double totalDistance = startPos.distanceTo(target.position());
            double totalTime = totalDistance / speed;
            if (interpTime < totalTime) {
                interpTime++;
                Mth.clamp(interpTime, 0, totalTime);
            }
            strike(interpTime / totalTime);
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void ascend(double t) {
        if (t == 1) {
            setDeltaMovement(0, 0, 0);
            setPos(getX(), startPos.y + 10, getZ());
            setState(State.IDLE);
            this.playSound(ModSounds.PHEONIX_ASCEND.get(), 4, 1);
        } else {
            double yChange = (startPos.y + (10) * t) - getY();
            setDeltaMovement(0, yChange, 0);
            this.setYRot(owner.getYRot());
            this.setPos(owner.getEyePosition().add(owner.getViewVector(1.0F).add(0,
                    -this.getBoundingBox().getYsize() * .7f , 0)));
        }

    }

    private void strike(double t) {
        if (t >= 1) {
            setDeltaMovement(0, 0, 0);
            setPos(target.position());
            level().playSound(null, this.blockPosition(), ModSounds.PHEONIX_STRIKE.get(), SoundSource.PLAYERS, 4, 1);
            this.remove(RemovalReason.KILLED);
        } else {

            double pX = (startPos.x + (target.getX() - startPos.x) * t) - getX();
            double pZ = (startPos.z + (target.getZ() - startPos.z) * t) - getZ();
            double pY = (startPos.y + (target.getY() - startPos.y) * (1 - Math.cos(t * Math.PI)) / 2) - getY();
            setDeltaMovement(pX, pY, pZ);

            double d0 = target.getX() - this.getX();
            double d1 = target.getY() - this.getY();
            double d2 = target.getZ() - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            this.setXRot(Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * (double) (180F / (float) Math.PI)))) + 90);
            this.setYRot(Mth.wrapDegrees((float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F));
            this.setYHeadRot(this.getYRot());
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
        }

    }


    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 5, this::idleAnimController));
    }

    protected <E extends PhoenixEntity> PlayState idleAnimController(final AnimationState<E> event) {
        return event.setAndContinue(SPAWN_ANIM);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }


    public void setSpellPower(float power) {
        this.entityData.set(SPELL_POWER, power);
    }

    public float getSpellPower() {
        return this.entityData.get(SPELL_POWER);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.setSpellPower(pCompound.getFloat("spell_power"));

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("spell_power", this.getSpellPower());

    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    public void setState(PhoenixEntity.State state) {
        this.state = state;
        interpTime = 0;
        if (state == State.ASCEND) {
            if (owner != null) {
                this.startPos = owner.getPosition(1.0f);
            }
        }
        if (state == State.STRIKE) {
            this.startPos = getPosition(1.0f);
        }
    }

    public PhoenixEntity.State getState() {
        return state;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    public void setTarget(TargetedAreaEntity target) {
        this.target = target;
        strike = true;
    }

    public enum State {
        BIRTH, ASCEND, IDLE, STRIKE
    }

}
