package com.coenmooney.decertium;

import com.coenmooney.decertium.init.DecertiumBlocks;
import com.coenmooney.decertium.init.DecertiumEntities;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class DecertiumModClient {
    public static void initClient() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(DecertiumModClient::onClientSetup);
        modBus.addListener(DecertiumModClient::registerRenderers);
    }

    private static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(DecertiumBlocks::setRenderLayers);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        DecertiumEntities.registerRenderers(event);
    }
}
