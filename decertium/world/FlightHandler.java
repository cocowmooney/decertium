package com.coenmooney.decertium.world;

import com.coenmooney.decertium.entity.Decertus;
import com.coenmooney.decertium.init.DecertiumItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class FlightHandler {
    private static final String SLAYER_TAG = "DecertusSlayer";

    public void handleFlight(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.getLevel().isClientSide) {
            return;
        }
        Player player = event.player;
        boolean fullSet = hasFullSet(player);
        if (fullSet) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
        } else if (!player.isCreative()) {
            if (player.getAbilities().flying) {
                player.getAbilities().flying = false;
            }
            if (player.getAbilities().mayfly) {
                player.getAbilities().mayfly = false;
                player.onUpdateAbilities();
            }
        }
    }

    public void handleDamageMitigation(LivingHurtEvent event) {
        if (event.getEntity() instanceof EnderDragon && event.getSource().getEntity() instanceof Player player) {
            if (hasFullSet(player) && hasSlayerMark(player) && player.getMainHandItem().is(DecertiumItems.DECERTIUM_SWORD.get())) {
                event.setAmount(120.0F);
            }
        }
        if (event.getEntity() instanceof Player player) {
            if (hasFullSet(player) && hasSlayerMark(player)) {
                event.setCanceled(true);
            }
        }
    }

    public void handleDecertusDefeat(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Decertus)) {
            return;
        }
        if (event.getSource().getEntity() instanceof Player player) {
            player.getPersistentData().putBoolean(SLAYER_TAG, true);
            player.displayClientMessage(Component.literal("You have conquered Decertus. Your Decertium set is now absolute."), true);
        }
    }

    public void handleClone(PlayerEvent.Clone event) {
        if (event.isWasDeath() && event.getOriginal().getPersistentData().getBoolean(SLAYER_TAG)) {
            event.getEntity().getPersistentData().putBoolean(SLAYER_TAG, true);
        }
    }

    private boolean hasFullSet(Player player) {
        return player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).is(DecertiumItems.DECERTIUM_HELMET.get())
                && player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).is(DecertiumItems.DECERTIUM_CHESTPLATE.get())
                && player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS).is(DecertiumItems.DECERTIUM_LEGGINGS.get())
                && player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET).is(DecertiumItems.DECERTIUM_BOOTS.get());
    }

    private boolean hasSlayerMark(Player player) {
        return player.getPersistentData().getBoolean(SLAYER_TAG);
    }
}
