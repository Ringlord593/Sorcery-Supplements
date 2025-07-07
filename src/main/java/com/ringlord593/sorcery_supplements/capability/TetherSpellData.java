package com.ringlord593.sorcery_supplements.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TetherSpellData {

    private List<UUID> targetEntityUUIDs = new ArrayList<UUID>();
    private UUID sourceEntityUUID = UUID.randomUUID();
    private boolean isCaster = false;
    private boolean isMulticast = false;

    public UUID getSourceEntityUUID() { return sourceEntityUUID; }

    public List<UUID> getTargetEntityUUIDs() { return targetEntityUUIDs; }

    public boolean getIsCaster() {
        return isCaster;
    }

    public boolean getIsMulticast() {
        return isMulticast;
    }

    public void setSourceEntityUUID(UUID source) {
        this.sourceEntityUUID = source;
    }

    public void setTargetEntityUUIDs(List<UUID> targets) {
        this.targetEntityUUIDs = targets;
    }

    public void setIsCaster(boolean isCaster) {
        this.isCaster = isCaster;
    }

    public void setIsMulticast(boolean isMulticast) {
        this.isMulticast = isMulticast;
    }

    public List<LivingEntity> getTargetEntities(ServerLevel level) {
        List<LivingEntity> targets = new ArrayList<LivingEntity>();
        for (UUID uuid : targetEntityUUIDs) {
            if (level.getEntity(uuid) instanceof LivingEntity living) {
                targets.add(living);
            }
        }
        return targets;
    }

    public void saveNBTData(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putInt("number_of_targets", targetEntityUUIDs.size());
        for (int i = 0; i < targetEntityUUIDs.size(); i++) {
            compoundTag.putByteArray("tether_target_" + i, targetEntityUUIDs.get(i).toString().getBytes());
        }
        compoundTag.putUUID("sourceEntityUUID", sourceEntityUUID);
        compoundTag.putBoolean("isCaster", isCaster);
        compoundTag.putBoolean("isMulticast", isMulticast);
    }

    public void loadNBTData(CompoundTag compoundTag, HolderLookup.Provider provider) {
        int number_of_targets = compoundTag.getInt("number_of_targets");
        for (int i = 0; i < number_of_targets; i++) {
            targetEntityUUIDs.add(UUID.fromString(new String(compoundTag.getByteArray("tether_target_" + i))));
        }
        sourceEntityUUID = compoundTag.getUUID("sourceEntityUUID");
        isCaster = compoundTag.getBoolean("isCaster");
        isMulticast = compoundTag.getBoolean("isMulticast");
    }

}
