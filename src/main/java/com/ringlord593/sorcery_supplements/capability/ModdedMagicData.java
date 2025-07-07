package com.ringlord593.sorcery_supplements.capability;

import com.ringlord593.sorcery_supplements.registry.ModDataAttachments;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class ModdedMagicData {

    private boolean isMob = false;

    public ModdedMagicData(boolean isMob) {
        this.isMob = isMob;
    }

    public ModdedMagicData() {
        this(false);
    }

    public ModdedMagicData(ServerPlayer serverPlayer) {
        this(false);
        this.serverPlayer = serverPlayer;
    }

    public void setServerPlayer(ServerPlayer serverPlayer) {
        if (this.serverPlayer == null && serverPlayer != null) {
            this.serverPlayer = serverPlayer;
        }
    }
    private ServerPlayer serverPlayer = null;

    private final TetherSpellData tetherSpellData = new TetherSpellData();

    public TetherSpellData getTetherSpellData() {
        return this.tetherSpellData;
    }

    public static ModdedMagicData getPlayerMagicData(LivingEntity livingEntity) {
        return livingEntity.getData(ModDataAttachments.MODDED_MAGIC_DATA);
    }

    public void saveNBTData(CompoundTag compound, HolderLookup.Provider provider) {
        getTetherSpellData().saveNBTData(compound, provider);
    }

    public void loadNBTData(CompoundTag compound, HolderLookup.Provider provider) {
        getTetherSpellData().loadNBTData(compound, provider);
    }

    @Override
    public String toString() {
        return String.format("TargetIDs:%s, CasterID:%s, IsCastor:%b, IsMulticast:%b",
                getTetherSpellData().getTargetEntityUUIDs(),
                getTetherSpellData().getSourceEntityUUID(),
                getTetherSpellData().getIsCaster(),
                getTetherSpellData().getIsMulticast());
    }
}
