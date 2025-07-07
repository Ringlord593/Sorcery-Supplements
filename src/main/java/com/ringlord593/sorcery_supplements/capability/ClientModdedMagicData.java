package com.ringlord593.sorcery_supplements.capability;

public class ClientModdedMagicData {
    private static final ClientModdedMagicData instance = new ClientModdedMagicData();

    public int[] targetEntityIds;
    public int sourceEntityId;
    public boolean isCaster = false;
    public boolean isMulticast = false;

    public static ClientModdedMagicData getInstance() {
        return instance;
    }

    public void update(int[] targetEntityIds, int sourceEntityId, boolean isCaster, boolean isMulticast) {
        this.isCaster = isCaster;
        this.isMulticast = isMulticast;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds = targetEntityIds;
    }
}
