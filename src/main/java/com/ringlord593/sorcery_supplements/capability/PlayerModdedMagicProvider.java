package com.ringlord593.sorcery_supplements.capability;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class PlayerModdedMagicProvider implements IAttachmentSerializer<CompoundTag, ModdedMagicData> {

    @Override
    public ModdedMagicData read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
        var moddedMagicData = holder instanceof ServerPlayer serverPlayer ? new ModdedMagicData(serverPlayer) : new ModdedMagicData(true);
        moddedMagicData.loadNBTData(tag, provider);
        return moddedMagicData;
    }

    @Override
    public @Nullable CompoundTag write(ModdedMagicData attachment, HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        attachment.saveNBTData(tag, provider);
        return tag;
    }
}