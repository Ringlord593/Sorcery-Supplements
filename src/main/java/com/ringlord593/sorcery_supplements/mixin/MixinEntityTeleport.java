package com.ringlord593.sorcery_supplements.mixin;

import com.ringlord593.sorcery_supplements.util.TetherSpellHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.ringlord593.sorcery_supplements.SorcerySupplements;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public class MixinEntityTeleport {
/*
    @Inject(method = {
            "Lnet/minecraft/world/entity/Entity;teleportTo(DDD)V"}, remap = true, at = @At(value = "HEAD"), cancellable = true)
    private void teleportTetheredEntites(double pX, double pY, double pZ, CallbackInfo ci) {
        SorcerySupplements.LOGGER.debug(((Entity) (Object) this).getName().getString() + " called teleport_to!");
        TetherSpellHandler.handleTether((Entity) (Object) this, pX, pY, pZ);
    }

    @Inject(method = {
            "Lnet/minecraft/world/entity/Entity;changeDimension(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraftforge/common/util/ITeleporter;)Lnet/minecraft/world/entity/Entity;"}, remap = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;removeAfterChangingDimensions()V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void TetheredEntitesChangeDimension(ServerLevel pDestination, ITeleporter teleporter,
                                                CallbackInfoReturnable<Entity> cir, PortalInfo info, Entity transportedEntity) {
        SorcerySupplements.LOGGER.debug(((Entity) (Object) this).getName().getString() + " called change_dimension!");
        TetherSpellHandler.handleTether((Entity) (Object) this, transportedEntity.getX(), transportedEntity.getY(), transportedEntity.getZ());
    }

    @Inject(method = {
            "Lnet/minecraft/world/entity/Entity;dismountTo(DDD)V"}, remap = true, at = @At(value = "HEAD"), cancellable = true)
    private void fixDismountTo(double pX, double pY, double pZ, CallbackInfo ci) {
        SorcerySupplements.LOGGER.debug(((Entity) (Object) this).getName().getString() + " called dismount_to!");
        if (((Entity) (Object) this).level() instanceof ServerLevel) {
            ((Entity) (Object) this).moveTo(pX, pY, pZ, ((Entity) (Object) this).getYRot(),
                    ((Entity) (Object) this).getXRot());
            ((Entity) (Object) this).teleportPassengers();
        }
    }

    @Inject(method = {
            "Lnet/minecraft/world/entity/Entity;teleportRelative(DDD)V"}, remap = true, at = @At(value = "HEAD"), cancellable = true)
    private void fixTeleportRelative(double pX, double pY, double pZ, CallbackInfo ci) {
        SorcerySupplements.LOGGER.debug(((Entity) (Object) this).getName().getString() + " called teleport_relative!");
        if (((Entity) (Object) this).level() instanceof ServerLevel) {
            ((Entity) (Object) this).moveTo(pX, pY, pZ, ((Entity) (Object) this).getYRot(),
                    ((Entity) (Object) this).getXRot());
            ((Entity) (Object) this).teleportPassengers();
        }
    }

*/
}
