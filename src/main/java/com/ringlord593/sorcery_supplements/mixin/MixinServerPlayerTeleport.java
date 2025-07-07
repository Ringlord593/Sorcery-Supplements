package com.ringlord593.sorcery_supplements.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayer.class)
public class MixinServerPlayerTeleport {
/*
    @Inject(method = {
            "Lnet/minecraft/server/level/ServerPlayer;teleportTo(DDD)V"}, remap = true, at = @At(value = "HEAD"), cancellable = true)
    private void teleportTetheredEntites(double pX, double pY, double pZ, CallbackInfo ci) {
        SorcerySupplements.LOGGER.debug(((ServerPlayer) (Object) this).getName().getString() + " called teleport_to!");
        TetherSpellHandler.handleTether((Entity) (Object) this, pX, pY, pZ);
    }
*/
}
