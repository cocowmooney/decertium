package com.coenmooney.decertium.init;

import com.coenmooney.decertium.DecertiumMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DecertiumSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, DecertiumMod.MOD_ID);

    public static final RegistryObject<SoundEvent> DARK_PORTAL_OPEN = register("block.dark_world_portal.open");
    public static final RegistryObject<SoundEvent> DECERTUS_ROAR = register("entity.decertus.roar");

    private static RegistryObject<SoundEvent> register(String name) {
        ResourceLocation id = new ResourceLocation(DecertiumMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> new SoundEvent(id));
    }
}
