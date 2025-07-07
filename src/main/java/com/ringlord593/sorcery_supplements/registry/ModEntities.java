package com.ringlord593.sorcery_supplements.registry;

import com.ringlord593.sorcery_supplements.SorcerySupplements;
import com.ringlord593.sorcery_supplements.entities.spells.holy_flare.FlareEntity;
import com.ringlord593.sorcery_supplements.entities.spells.holy_flare.FlareProjectile;
import com.ringlord593.sorcery_supplements.entities.spells.pheonix.PhoenixEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, SorcerySupplements.MODID);

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    public static final DeferredHolder<EntityType<?>, EntityType<FlareProjectile>> FLARE_PROJECTILE =
            ENTITIES.register("flare_projectile", () -> EntityType.Builder.<FlareProjectile>of(FlareProjectile::new, MobCategory.MISC)
                    .sized(.5f, .5f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(SorcerySupplements.MODID, "flare_projectile").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<FlareEntity>> FLARE_ENTITY =
            ENTITIES.register("flare_entity", () -> EntityType.Builder.<FlareEntity>of(FlareEntity::new, MobCategory.MISC)
                    .sized(2f, 2f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(SorcerySupplements.MODID, "flare_entity").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<PhoenixEntity>> PHOENIX_ENTITY =
            ENTITIES.register("phoenix_entity", () -> EntityType.Builder.<PhoenixEntity>of(PhoenixEntity::new, MobCategory.MISC)
                    .sized(1.5f, 1.5f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(SorcerySupplements.MODID, "phoenix_entity").toString()));
}
