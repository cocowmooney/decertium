package com.coenmooney.decertium;

import com.coenmooney.decertium.init.DecertiumItems;
import com.coenmooney.decertium.world.DarkWorldHandler;
import com.coenmooney.decertium.world.FlightHandler;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DecertiumServerEvents {
    private final FlightHandler flightHandler = new FlightHandler();
    private final DarkWorldHandler darkWorldHandler = new DarkWorldHandler();

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        flightHandler.handleFlight(event);
        darkWorldHandler.handlePortalTick(event);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        darkWorldHandler.handleRespawn(event);
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        darkWorldHandler.handleClone(event);
        flightHandler.handleClone(event);
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        darkWorldHandler.handleDeath(event);
    }

    @SubscribeEvent
    public void onDecertusDefeat(LivingDeathEvent event) {
        flightHandler.handleDecertusDefeat(event);
    }

    @SubscribeEvent
    public void onLivingHurt(net.minecraftforge.event.entity.living.LivingHurtEvent event) {
        flightHandler.handleDamageMitigation(event);
    }

    @SubscribeEvent
    public void onPortalInteract(PlayerInteractEvent.RightClickBlock event) {
        darkWorldHandler.handlePortalIgnition(event);
    }

    @SubscribeEvent
    public void onBlockPlaced(net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent event) {
        darkWorldHandler.handleSummon(event);
    }
}
