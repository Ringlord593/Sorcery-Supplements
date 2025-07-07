package com.ringlord593.sorcery_supplements.entities.spells.holy_flare;

import java.util.ArrayList;
import java.util.List;

import com.ringlord593.sorcery_supplements.registry.ModEntities;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

public class FlareEntity extends Projectile implements AntiMagicSusceptible {
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(FlareEntity.class,
            EntityDataSerializers.FLOAT);

    int spellLevel = 0;
    float spellPower = 0;

    public FlareEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FlareEntity(Level pLevel, LivingEntity owner, int spellLevel, float spellPower) {
        this(ModEntities.FLARE_ENTITY.get(), pLevel);
        setOwner(owner);
        this.spellLevel = spellLevel;
        this.spellPower = spellPower;
    }

    List<Entity> trackingEntities = new ArrayList<>();
    List<Entity> collidingEntities = new ArrayList<>();
    List<BlockPos> trackingBlocks = new ArrayList<>();

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        discard();
    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    private float damage;

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.scalable((this.getRadius() * 0.1F) * 2.0F, (this.getRadius() * 0.1F) * 2.0F);
    }


    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (DATA_RADIUS.equals(pKey)) {
            this.refreshDimensions();
            if (getRadius() < .1f)
                this.discard();
        }
        super.onSyncedDataUpdated(pKey);
    }

    public void setRadius(float pRadius) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Math.min(pRadius, 48));
        }
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("Radius", this.getRadius());
        pCompound.putInt("Age", this.tickCount);
        pCompound.putFloat("Damage", this.getDamage());
        List<Integer> blockX = new ArrayList<>();
        List<Integer> blockY = new ArrayList<>();
        List<Integer> blockZ = new ArrayList<>();
        for (BlockPos blockPos : trackingBlocks) {
            blockX.add(blockPos.getX());
            blockY.add(blockPos.getY());
            blockZ.add(blockPos.getZ());
        }
        pCompound.putIntArray("BlockX", blockX);
        pCompound.putIntArray("BlockY", blockY);
        pCompound.putIntArray("BlockZ", blockZ);
        super.addAdditionalSaveData(pCompound);
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.tickCount = pCompound.getInt("Age");
        this.damage = pCompound.getFloat("Damage");
        if (damage == 0)
            damage = 1;
        if (pCompound.getInt("Radius") > 0)
            this.setRadius(pCompound.getFloat("Radius"));
        int[] blockX = pCompound.getIntArray("BlockX");
        int[] blockY = pCompound.getIntArray("BlockY");
        int[] blockZ = pCompound.getIntArray("BlockZ");
        for (int i = 0; i < blockX.length; i++) {
            trackingBlocks.add(new BlockPos(new Vec3i(blockX[i], blockY[i], blockZ[i])));
        }

        super.readAdditionalSaveData(pCompound);

    }

    @Override
    public void tick() {
        super.tick();
        Level level = level();
        int update = Math.max((int) (getRadius() / 2), 3);
        // prevent lag from gigantic flares
        if (tickCount % update == 0) {
            updateTrackingEntities();
        }

        boolean hitTick = this.tickCount % 10 == 0;
        for (Entity entity : trackingEntities) {
            if (entity instanceof LivingEntity livingEntity) {
                if (entity.getType().is(EntityTypeTags.UNDEAD)) {
                    if (entity != getOwner() && !DamageSources.isFriendlyFireBetween(getOwner(), entity)) {
                        if (hitTick && canHitEntity(entity)) {
                            entity.setRemainingFireTicks(60);
                        }
                    }
                }
            }
        }
        for (Entity entity : collidingEntities) {
            if (entity instanceof LivingEntity livingEntity) {
                if (entity != getOwner() && !DamageSources.isFriendlyFireBetween(getOwner(), entity)) {
                    if (hitTick && canHitEntity(entity)) {
                        entity.setRemainingFireTicks(60);
                        livingEntity.hurt(DamageSources.get(level, DamageTypes.ON_FIRE), damage);
                    }
                }

            }
        }
        if (!level.isClientSide) {
            if (tickCount > 20 * (spellPower + 1) * 3) {
                this.discard();
                MagicManager.spawnParticles(level, ParticleTypes.FLASH, getX(), getY() + getRadius(), getZ(), 1, 0.5,
                        0.5, 0.5, 0, true);
            }
        }
    }

    private void updateTrackingEntities() {
        trackingEntities = level().getEntities(this, this.getBoundingBox().inflate(getRadius() + 2));
        collidingEntities = level().getEntities(this, this.getBoundingBox().inflate(1));
    }

    public void removeLightBlocks() {
        Level level = level();
        if (!level.isClientSide) {
            if (!trackingBlocks.isEmpty()) {
                for (BlockPos pos : trackingBlocks) {
                    BlockState block = level().getBlockState(pos);
                    if (block == Blocks.LIGHT.defaultBlockState()) {
                        level().setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
                trackingBlocks.clear();
            }
        }

    }

    public void spawnLightBlocks(Vec3 position) {
        Level level = level();
        if (!level.isClientSide) {
            removeLightBlocks();
            trackingBlocks = getBlocksInRadius(level(), position, (int) getRadius());
            for (BlockPos pos : trackingBlocks) {
                BlockState block = level().getBlockState(pos);
                if (block.isAir()) {
                    level().setBlock(pos, Blocks.LIGHT.defaultBlockState(), 2);
                }
            }
        }
    }

    public List<BlockPos> getBlocksInRadius(Level world, Vec3 center, int radius) {
        List<BlockPos> blocks = new ArrayList<>();

        int cx = (int) center.x;
        int cy = (int) center.y;
        int cz = (int) center.z;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radius * radius) {
                        if ((x + y + z) % 5 == 0) {
                            BlockPos pos = new BlockPos(cx + x, cy + y, cz + z);
                            blocks.add(pos);
                        }
                    }
                }
            }
        }
        return blocks;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_RADIUS, 5F);
    }

    @Override
    public void remove(RemovalReason pReason) {
        removeLightBlocks();
        super.remove(pReason);
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }
}
