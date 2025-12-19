package com.coenmooney.decertium;

import com.coenmooney.decertium.init.DecertiumBlocks;
import com.coenmooney.decertium.init.DecertiumCreativeTab;
import com.coenmooney.decertium.init.DecertiumEntities;
import com.coenmooney.decertium.init.DecertiumItems;
import com.coenmooney.decertium.init.DecertiumSounds;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DecertiumMod.MOD_ID)
public class DecertiumMod {
    public static final String MOD_ID = "decertium";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DecertiumMod() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        DecertiumBlocks.BLOCKS.register(modBus);
        DecertiumItems.ITEMS.register(modBus);
        DecertiumEntities.ENTITY_TYPES.register(modBus);
        DecertiumSounds.SOUND_EVENTS.register(modBus);
        modBus.addListener(DecertiumEntities::addEntityAttributes);
        modBus.addListener(DecertiumEntities::setupSpawns);

        MinecraftForge.EVENT_BUS.register(new DecertiumServerEvents());
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> DecertiumModClient::initClient);
    }
}
