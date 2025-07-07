package com.ringlord593.sorcery_supplements;

import com.mojang.logging.LogUtils;
import com.ringlord593.sorcery_supplements.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(SorcerySupplements.MODID)
public class SorcerySupplements {
    public static final String MODID = "sorcery_supplements";
    public static final Logger LOGGER = LogUtils.getLogger();


    public SorcerySupplements(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        ModSpells.register(modEventBus);
        ModEffects.register(modEventBus);
        ModParticles.register(modEventBus);
        ModDataAttachments.register(modEventBus);


        //ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfigs.SPEC,"irons_spellbooks-client.toml");
        //modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfigs.SPEC, String.format("%s-client.toml", IronsSpellbooks.MODID));
        // modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC, String.format("%s-server.toml", IronsSpellbooks.MODID));

    }

    public static ResourceLocation id(@NotNull String path) {
        return new ResourceLocation(SorcerySupplements.MODID, path);
    }

}
