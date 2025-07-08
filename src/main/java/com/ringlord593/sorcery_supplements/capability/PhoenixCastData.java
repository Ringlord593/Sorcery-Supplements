package com.ringlord593.sorcery_supplements.capability;

import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.UUID;

public class PhoenixCastData implements ICastDataSerializable {

    private UUID pheonix = UUID.randomUUID();

    public PhoenixCastData(UUID pheonix) {
        this.pheonix = pheonix;
    }

    public PhoenixCastData(Entity pheonix) {
        this.pheonix = pheonix.getUUID();
    }

    public PhoenixCastData() {
    }

    @Nullable
    public Entity getPheonix(ServerLevel level) {
        return level.getEntity(pheonix);
    }

    public UUID getPheonixUUID() {
        return pheonix;
    }

    @Override
    public void reset() {

    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {
        buffer.writeUUID(pheonix);

    }

    @Override
    public void readFromBuffer(FriendlyByteBuf buffer) {
        pheonix = buffer.readUUID();

    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("phoenix", pheonix);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        pheonix = compoundTag.getUUID("phoenix");
    }

}