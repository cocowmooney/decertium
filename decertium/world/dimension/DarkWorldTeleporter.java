package com.coenmooney.decertium.world.dimension;

import com.coenmooney.decertium.init.DecertiumBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class DarkWorldTeleporter implements ITeleporter {
    private final BlockPos fromPortal;

    public DarkWorldTeleporter(BlockPos fromPortal) {
        this.fromPortal = fromPortal;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destinationWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity movedEntity = repositionEntity.apply(false);
        BlockPos targetPos = locateOrBuildPortal(destinationWorld, movedEntity.blockPosition());
        movedEntity.teleportTo(targetPos.getX() + 0.5D, targetPos.getY() + 1, targetPos.getZ() + 0.5D);
        movedEntity.setPortalCooldown();
        return movedEntity;
    }

    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        BlockPos targetPos = locateOrBuildPortal(destWorld, fromPortal == null ? entity.blockPosition() : fromPortal);
        Vec3 center = new Vec3(targetPos.getX() + 0.5D, targetPos.getY() + 1, targetPos.getZ() + 0.5D);
        return new PortalInfo(center, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
    }

    private BlockPos locateOrBuildPortal(ServerLevel level, BlockPos hint) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int radius = 12;
        BlockPos best = null;
        for (BlockPos pos : BlockPos.betweenClosed(hint.offset(-radius, -radius, -radius), hint.offset(radius, radius, radius))) {
            BlockState state = level.getBlockState(pos);
            if (state.is(DecertiumBlocks.DARK_WORLD_PORTAL.get())) {
                best = pos;
                break;
            }
        }
        if (best != null) {
            return best;
        }
        BlockPos ground = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, hint);
        buildPortalFrame(level, ground);
        return ground;
    }

    private void buildPortalFrame(ServerLevel level, BlockPos base) {
        BlockState frame = DecertiumBlocks.DECERTIUM_BLOCK.get().defaultBlockState();
        BlockState portal = DecertiumBlocks.DARK_WORLD_PORTAL.get().defaultBlockState();
        // Build a simple 4x5 frame oriented on the X axis.
        for (int y = 0; y < 5; y++) {
            for (int x = -1; x <= 2; x++) {
                BlockPos framePos = base.offset(x, y, 0);
                boolean isFrame = y == 0 || y == 4 || x == -1 || x == 2;
                level.setBlock(framePos, isFrame ? frame : portal, 18);
            }
        }
    }
}
