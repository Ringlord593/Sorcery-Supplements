package com.ringlord593.sorcery_supplements.entities.spells.pheonix;

import com.ringlord593.sorcery_supplements.SorcerySupplements;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class PhoenixModel extends GeoModel<PhoenixEntity> {
    @Override
    public ResourceLocation getModelResource(PhoenixEntity animatable) {
        return new ResourceLocation(SorcerySupplements.MODID, "geo/phoenix.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PhoenixEntity animatable) {
        return new ResourceLocation(SorcerySupplements.MODID, "textures/entity/phoenix.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PhoenixEntity animatable) {
        return new ResourceLocation(SorcerySupplements.MODID, "animations/phoenix.animation.json");
    }

    @Override
    public void setCustomAnimations(PhoenixEntity animatable, long instanceId, AnimationState<PhoenixEntity> animationState) {

    }


}