package com.coenmooney.decertium.block;

import com.coenmooney.decertium.init.DecertiumBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;

import java.util.Optional;

public class DecertiumPortalBlock extends NetherPortalBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public DecertiumPortalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        // Dimension handling is managed in the world handler to enforce gear sacrifice rules.
        if (!entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions()) {
            entity.setPortalCooldown();
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Disable zombie pigman spawning; keep portal quiet.
    }

    public static Optional<PortalShape> findPortalShape(LevelAccessor level, BlockPos pos, Direction.Axis axis) {
        PortalShape shape = new PortalShape(level, pos, axis);
        return shape.isValid() ? Optional.of(shape) : Optional.empty();
    }

    public static class PortalShape {
        private static final int MIN_WIDTH = 2;
        private static final int MAX_WIDTH = 21;
        private static final int MIN_HEIGHT = 3;
        private static final int MAX_HEIGHT = 21;
        private final LevelAccessor level;
        private final Direction.Axis axis;
        private final Direction rightDir;
        private final Direction leftDir;
        private BlockPos bottomLeft;
        private int height;
        private int width;
        private int portalBlocks;

        public PortalShape(LevelAccessor level, BlockPos pos, Direction.Axis axis) {
            this.level = level;
            this.axis = axis;
            this.rightDir = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
            this.leftDir = this.rightDir.getOpposite();
            this.bottomLeft = this.calculateBottomLeft(pos);
            if (this.bottomLeft != null) {
                this.width = this.calculateWidth();
                if (this.width > 0) {
                    this.height = this.calculateHeight();
                }
            }
        }

        private BlockPos calculateBottomLeft(BlockPos pos) {
            BlockPos.MutableBlockPos mutable = pos.mutable();
            int minHeight = Math.max(level.getMinBuildHeight(), pos.getY() - MAX_HEIGHT);
            while (mutable.getY() > minHeight && isEmpty(level.getBlockState(mutable.below()))) {
                mutable.move(Direction.DOWN);
            }
            Direction opposite = this.rightDir.getOpposite();
            int distance = this.getDistanceUntilEdge(mutable, opposite) - 1;
            if (distance < 0) {
                return null;
            }
            return mutable.relative(opposite, distance);
        }

        private int getDistanceUntilEdge(BlockPos pos, Direction dir) {
            int i = 0;
            while (i < MAX_WIDTH) {
                BlockPos checkPos = pos.relative(dir, i);
                BlockState state = level.getBlockState(checkPos);
                if (!isEmpty(state) || !isFrame(level.getBlockState(checkPos.below()))) {
                    break;
                }
                ++i;
            }
            BlockState state = level.getBlockState(pos.relative(dir, i));
            return isFrame(state) ? i : 0;
        }

        private int calculateWidth() {
            int width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
            return width >= MIN_WIDTH && width <= MAX_WIDTH ? width : 0;
        }

        private int calculateHeight() {
            int y;
            label39:
            for (y = 0; y < MAX_HEIGHT; ++y) {
                for (int x = 0; x < this.width; ++x) {
                    BlockPos checkPos = this.bottomLeft.relative(this.rightDir, x).above(y);
                    BlockState state = this.level.getBlockState(checkPos);
                    if (!isEmpty(state)) {
                        break label39;
                    }
                    if (state.is(DecertiumBlocks.DARK_WORLD_PORTAL.get())) {
                        ++this.portalBlocks;
                    }
                    if (x == 0) {
                        if (!isFrame(this.level.getBlockState(checkPos.relative(this.leftDir)))) {
                            break label39;
                        }
                    } else if (x == this.width - 1) {
                        if (!isFrame(this.level.getBlockState(checkPos.relative(this.rightDir)))) {
                            break label39;
                        }
                    }
                }
            }
            for (int x = 0; x < this.width; ++x) {
                if (!isFrame(this.level.getBlockState(this.bottomLeft.relative(this.rightDir, x).above(y)))) {
                    return 0;
                }
            }
            return y;
        }

        private boolean isFrame(BlockState state) {
            return state.is(DecertiumBlocks.DECERTIUM_BLOCK.get());
        }

        private boolean isEmpty(BlockState state) {
            return state.isAir() || state.getMaterial() == Material.FIRE || state.is(DecertiumBlocks.DARK_WORLD_PORTAL.get());
        }

        public boolean isValid() {
            return this.bottomLeft != null && this.width >= MIN_WIDTH && this.width <= MAX_WIDTH && this.height >= MIN_HEIGHT && this.height <= MAX_HEIGHT;
        }

        public boolean isComplete() {
            return this.portalBlocks == this.width * this.height;
        }

        public void createPortalBlocks() {
            BlockState state = DecertiumBlocks.DARK_WORLD_PORTAL.get().defaultBlockState().setValue(AXIS, this.axis);
            BlockPos.betweenClosedStream(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1))
                    .forEach(pos -> this.level.setBlock(pos, state, 18));
        }
    }
}
