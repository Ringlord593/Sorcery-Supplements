package com.ringlord593.sorcery_supplements.network;

import com.ringlord593.sorcery_supplements.SorcerySupplements;
import com.ringlord593.sorcery_supplements.capability.ClientModdedMagicData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncTetherStatePacket implements CustomPacketPayload {

    private int[] targetEntityIds;
    private int sourceEntityId;
    private boolean isCaster = false;
    private boolean isMulticast = false;
    public static final CustomPacketPayload.Type<SyncTetherStatePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SorcerySupplements.MODID, "sync_tether"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncTetherStatePacket> STREAM_CODEC = CustomPacketPayload.codec(SyncTetherStatePacket::write, SyncTetherStatePacket::new);

    public SyncTetherStatePacket(int[] targetEntityIds, int sourceEntityId, boolean isCaster, boolean isMulticast) {
        this.isCaster = isCaster;
        this.isMulticast = isMulticast;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds = targetEntityIds;
    }

    public SyncTetherStatePacket(FriendlyByteBuf buf) {
        this.isCaster = buf.readBoolean();
        this.isMulticast = buf.readBoolean();
        this.sourceEntityId = buf.readInt();
        this.targetEntityIds = buf.readVarIntArray();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(isCaster);
        buf.writeBoolean(isMulticast);
        buf.writeInt(sourceEntityId);
        buf.writeVarIntArray(targetEntityIds);
    }

    public static void handle(SyncTetherStatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientModdedMagicData.getInstance().update(packet.targetEntityIds, packet.sourceEntityId, packet.isCaster, packet.isMulticast);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
