package com.coenmooney.decertium.world;

import com.coenmooney.decertium.DecertiumMod;
import com.coenmooney.decertium.block.DecertiumPortalBlock;
import com.coenmooney.decertium.entity.Decertus;
import com.coenmooney.decertium.init.DecertiumBlocks;
import com.coenmooney.decertium.init.DecertiumEntities;
import com.coenmooney.decertium.init.DecertiumItems;
import com.coenmooney.decertium.world.dimension.DarkWorldTeleporter;
import com.coenmooney.decertium.world.dimension.DecertiumDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;

import java.util.Optional;

public class DarkWorldHandler {
    private static final String DATA_TAG = "Decertium";
    private static final String RETURN_POS = "ReturnPos";
    private static final String RETURN_DIM = "ReturnDim";
    private static final String DARK_DEATH = "DarkDeath";

    public void handlePortalIgnition(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (player == null || player.getLevel().isClientSide) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (!stack.is(DecertiumItems.DARK_WORLD_KEY.get())) {
            return;
        }
        Level level = event.getLevel();
        BlockPos target = event.getPos().relative(event.getFace());
        Direction.Axis axis = event.getFace().getAxis().isHorizontal() ? event.getFace().getAxis() : Direction.Axis.X;
        Optional<DecertiumPortalBlock.PortalShape> shape = DecertiumPortalBlock.findPortalShape(level, target, axis);
        if (shape.isEmpty()) {
            shape = DecertiumPortalBlock.findPortalShape(level, target.relative(Direction.UP), axis);
        }
        if (shape.isPresent()) {
            shape.get().createPortalBlocks();
            level.playSound(null, target, com.coenmooney.decertium.init.DecertiumSounds.DARK_PORTAL_OPEN.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.getAbilities().instabuild) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    public void handlePortalTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }
        BlockPos pos = player.blockPosition();
        BlockState state = player.getLevel().getBlockState(pos);
        if (!state.is(DecertiumBlocks.DARK_WORLD_PORTAL.get())) {
            return;
        }
        if (player.isPassenger() || player.isVehicle() || !player.canChangeDimensions()) {
            return;
        }
        if (player.getLevel().dimension() != DecertiumDimensions.DARK_WORLD) {
            tryEnterDarkWorld(player, pos);
        } else {
            returnToOverworld(player);
        }
    }

    private void tryEnterDarkWorld(ServerPlayer player, BlockPos portalPos) {
        if (!hasFullDecertiumSet(player) || !hasDecertiumSword(player)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("The portal rejects you. Wear a full Decertium set and carry its sword."), true);
            player.setPortalCooldown();
            return;
        }
        ServerLevel target = player.server.getLevel(DecertiumDimensions.DARK_WORLD);
        if (target == null) {
            DecertiumMod.LOGGER.error("Dark World dimension is missing!");
            return;
        }
        sacrificeGear(player);
        storeReturnLocation(player, portalPos, Level.OVERWORLD);
        player.changeDimension(target, new DarkWorldTeleporter(portalPos));
        player.setPortalCooldown();
    }

    private void returnToOverworld(ServerPlayer player) {
        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return;
        }
        BlockPos returnPos = getStoredReturnPos(player).orElse(overworld.getSharedSpawnPos());
        player.changeDimension(overworld, new DarkWorldTeleporter(returnPos));
        player.setPortalCooldown();
    }

    public void handleDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (player.getLevel().dimension() != DecertiumDimensions.DARK_WORLD) {
            return;
        }
        removeDecertiumItems(player);
        markDarkDeath(player);
        reduceMaxHealth(player);
    }

    public void handleClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }
        CompoundTag original = event.getOriginal().getPersistentData().getCompound(DATA_TAG);
        if (!original.isEmpty()) {
            event.getEntity().getPersistentData().put(DATA_TAG, original.copy());
        }
    }

    public void handleRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        CompoundTag data = player.getPersistentData().getCompound(DATA_TAG);
        if (!data.getBoolean(DARK_DEATH)) {
            return;
        }
        data.remove(DARK_DEATH);
        player.getPersistentData().put(DATA_TAG, data);
        ResourceLocation dimId = ResourceLocation.tryParse(data.getString(RETURN_DIM));
        BlockPos returnPos = data.contains(RETURN_POS) ? BlockPos.of(data.getLong(RETURN_POS)) : null;
        if (dimId == null || returnPos == null) {
            return;
        }
        ResourceKey<Level> dimension = ResourceKey.create(net.minecraft.core.Registry.DIMENSION_REGISTRY, dimId);
        ServerLevel level = player.server.getLevel(dimension);
        if (level != null) {
            player.teleportTo(level, returnPos.getX() + 0.5D, returnPos.getY() + 1, returnPos.getZ() + 0.5D, player.getYRot(), player.getXRot());
        }
    }

    private boolean hasFullDecertiumSet(ServerPlayer player) {
        return player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).is(DecertiumItems.DECERTIUM_HELMET.get())
                && player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).is(DecertiumItems.DECERTIUM_CHESTPLATE.get())
                && player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS).is(DecertiumItems.DECERTIUM_LEGGINGS.get())
                && player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET).is(DecertiumItems.DECERTIUM_BOOTS.get());
    }

    private boolean hasDecertiumSword(ServerPlayer player) {
        return player.getMainHandItem().is(DecertiumItems.DECERTIUM_SWORD.get()) ||
                player.getInventory().items.stream().anyMatch(stack -> stack.is(DecertiumItems.DECERTIUM_SWORD.get()));
    }

    private void sacrificeGear(ServerPlayer player) {
        removeDecertiumItems(player);
    }

    private void removeDecertiumItems(ServerPlayer player) {
        for (net.minecraft.world.entity.EquipmentSlot slot : new net.minecraft.world.entity.EquipmentSlot[]{
                net.minecraft.world.entity.EquipmentSlot.HEAD,
                net.minecraft.world.entity.EquipmentSlot.CHEST,
                net.minecraft.world.entity.EquipmentSlot.LEGS,
                net.minecraft.world.entity.EquipmentSlot.FEET}) {
            ItemStack equipped = player.getItemBySlot(slot);
            if (equipped.is(DecertiumItems.DECERTIUM_HELMET.get())
                    || equipped.is(DecertiumItems.DECERTIUM_CHESTPLATE.get())
                    || equipped.is(DecertiumItems.DECERTIUM_LEGGINGS.get())
                    || equipped.is(DecertiumItems.DECERTIUM_BOOTS.get())) {
                player.setItemSlot(slot, ItemStack.EMPTY);
            }
        }
        player.getInventory().items.removeIf(stack -> stack.is(DecertiumItems.DECERTIUM_SWORD.get()));
        if (player.getOffhandItem().is(DecertiumItems.DECERTIUM_SWORD.get())) {
            player.getInventory().offhand.set(0, ItemStack.EMPTY);
        }
        player.getInventory().setChanged();
    }

    private void reduceMaxHealth(ServerPlayer player) {
        var attr = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
        if (attr != null) {
            double newBase = Math.max(2.0D, attr.getBaseValue() - 2.0D);
            attr.setBaseValue(newBase);
            if (player.getHealth() > newBase) {
                player.setHealth((float) newBase);
            }
        }
    }

    private void storeReturnLocation(ServerPlayer player, BlockPos pos, ResourceKey<Level> dim) {
        CompoundTag data = player.getPersistentData().getCompound(DATA_TAG);
        data.putLong(RETURN_POS, pos.asLong());
        data.putString(RETURN_DIM, dim.location().toString());
        player.getPersistentData().put(DATA_TAG, data);
    }

    private Optional<BlockPos> getStoredReturnPos(ServerPlayer player) {
        CompoundTag data = player.getPersistentData().getCompound(DATA_TAG);
        if (!data.contains(RETURN_POS)) {
            return Optional.empty();
        }
        return Optional.of(BlockPos.of(data.getLong(RETURN_POS)));
    }

    private void markDarkDeath(ServerPlayer player) {
        CompoundTag data = player.getPersistentData().getCompound(DATA_TAG);
        data.putBoolean(DARK_DEATH, true);
        player.getPersistentData().put(DATA_TAG, data);
    }

    public void handleSummon(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide) {
            return;
        }
        BlockState placed = event.getPlacedBlock();
        if (!(placed.is(Blocks.WITHER_SKELETON_SKULL) || placed.is(Blocks.WITHER_SKELETON_WALL_SKULL))) {
            return;
        }
        if (level.dimension() != DecertiumDimensions.DARK_WORLD) {
            return;
        }
        if (trySpawnDecertus(level, event.getPos())) {
            event.setCanceled(true);
        }
    }

    private boolean trySpawnDecertus(Level level, BlockPos skullPos) {
        for (Direction dir : new Direction[]{Direction.NORTH, Direction.EAST}) {
            BlockPos base = skullPos.below();
            BlockPos left = base.relative(dir);
            BlockPos right = base.relative(dir.getOpposite());
            if (isDarkSand(level.getBlockState(base)) && isDarkSand(level.getBlockState(left)) && isDarkSand(level.getBlockState(right))) {
                if (isSkull(level.getBlockState(skullPos)) && isSkull(level.getBlockState(skullPos.relative(dir))) && isSkull(level.getBlockState(skullPos.relative(dir.getOpposite())))) {
                    level.setBlock(skullPos, Blocks.AIR.defaultBlockState(), 3);
                    level.setBlock(skullPos.relative(dir), Blocks.AIR.defaultBlockState(), 3);
                    level.setBlock(skullPos.relative(dir.getOpposite()), Blocks.AIR.defaultBlockState(), 3);
                    level.setBlock(base, Blocks.AIR.defaultBlockState(), 3);
                    level.setBlock(left, Blocks.AIR.defaultBlockState(), 3);
                    level.setBlock(right, Blocks.AIR.defaultBlockState(), 3);
                    Decertus decertus = DecertiumEntities.DECERTUS.get().create(level);
                    if (decertus != null) {
                        decertus.moveTo(base.getX() + 0.5D, base.getY() + 1, base.getZ() + 0.5D, 0.0F, 0.0F);
                        level.addFreshEntity(decertus);
                        level.playSound(null, base, com.coenmooney.decertium.init.DecertiumSounds.DECERTUS_ROAR.get(), SoundSource.HOSTILE, 1.5F, 0.6F);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isDarkSand(BlockState state) {
        return state.is(DecertiumBlocks.DARK_SAND.get());
    }

    private boolean isSkull(BlockState state) {
        return state.is(Blocks.WITHER_SKELETON_SKULL) || state.is(Blocks.WITHER_SKELETON_WALL_SKULL);
    }
}
