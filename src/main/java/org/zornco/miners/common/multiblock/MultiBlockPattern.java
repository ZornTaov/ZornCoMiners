package org.zornco.miners.common.multiblock;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class MultiBlockPattern {
    private final Predicate<MultiBlockInWorld>[][][] pattern;
    private final int depth;
    private final int height;
    private final int width;

    public MultiBlockPattern(Predicate<MultiBlockInWorld>[][][] pPattern) {
        this.pattern = pPattern;
        this.depth = pPattern.length;
        if (this.depth > 0) {
            this.height = pPattern[0].length;
            if (this.height > 0) {
                this.width = pPattern[0][0].length;
            } else {
                this.width = 0;
            }
        } else {
            this.height = 0;
            this.width = 0;
        }

    }

    public int getDepth() {
        return this.depth;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    @VisibleForTesting
    public Predicate<MultiBlockInWorld>[][][] getPattern() {
        return this.pattern;
    }

    @Nullable
    @VisibleForTesting
    public MultiBlockPattern.MultiBlockPatternMatch matches(LevelReader pLevel, BlockPos pPos, Direction pFinger, Direction pThumb) {
        LoadingCache<BlockPos, MultiBlockInWorld> loadingcache = createLevelCache(pLevel, false);
        return this.matches(pPos, pFinger, pThumb, loadingcache);
    }

    /**
     * Checks that the given pattern & rotation is at the block coordinates.
     */
    @Nullable
    private MultiBlockPattern.MultiBlockPatternMatch matches(BlockPos pPos, Direction pFinger, Direction pThumb, LoadingCache<BlockPos, MultiBlockInWorld> pCache) {
        for(int i = 0; i < this.width; ++i) {
            for(int j = 0; j < this.height; ++j) {
                for(int k = 0; k < this.depth; ++k) {
                    if (!this.pattern[k][j][i].test(pCache.getUnchecked(pPos.offset(translateAndRotate(pFinger, pThumb, i, j, k))))) {
                        return null;
                    }
                }
            }
        }

        return new MultiBlockPatternMatch(pPos, pFinger, pThumb, pCache, this.width, this.height, this.depth);
    }

    /**
     * Calculates whether the given world position matches the pattern. Warning, fairly heavy function.
     * @return a BlockPatternMatch if found, null otherwise.
     */
    @Nullable
    public MultiBlockPattern.MultiBlockPatternMatch find(LevelReader pLevel, BlockPos pPos) {
        LoadingCache<BlockPos, MultiBlockInWorld> loadingcache = createLevelCache(pLevel, false);
        int i = Math.max(Math.max(this.width, this.height), this.depth);

        for(BlockPos blockpos : BlockPos.betweenClosed(pPos, pPos.offset(i - 1, i - 1, i - 1))) {
            for(Direction direction : Direction.values()) {
                for(Direction direction1 : Direction.values()) {
                    if (direction1 != direction && direction1 != direction.getOpposite()) {
                        MultiBlockPattern.MultiBlockPatternMatch blockpattern$blockpatternmatch = this.matches(blockpos, direction, direction1, loadingcache);
                        if (blockpattern$blockpatternmatch != null) {
                            return blockpattern$blockpatternmatch;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static LoadingCache<BlockPos, MultiBlockInWorld> createLevelCache(LevelReader pLevel, boolean pForceLoad) {
        return CacheBuilder.newBuilder().build(new MultiBlockPattern.MultiBlockCacheLoader(pLevel, pForceLoad));
    }

    /**
     * Offsets the position of pos in the direction of finger and thumb facing by offset amounts, follows the right-hand
     * rule for cross products (finger, thumb, palm)
     *
     * @return a new BlockPos offset in the facing directions
     */
    protected static BlockPos translateAndRotate(Direction pFinger, Direction pThumb, int pPalmOffset, int pThumbOffset, int pFingerOffset) {
        if (pFinger != pThumb && pFinger != pThumb.getOpposite()) {
            Vec3i fingerSteps = new Vec3i(pFinger.getStepX(), pFinger.getStepY(), pFinger.getStepZ());
            Vec3i thumbSteps = new Vec3i(pThumb.getStepX(), pThumb.getStepY(), pThumb.getStepZ());
            Vec3i palmSteps = fingerSteps.cross(thumbSteps);
            return new BlockPos(thumbSteps.getX() * -pThumbOffset + palmSteps.getX() * pPalmOffset + fingerSteps.getX() * pFingerOffset, thumbSteps.getY() * -pThumbOffset + palmSteps.getY() * pPalmOffset + fingerSteps.getY() * pFingerOffset, thumbSteps.getZ() * -pThumbOffset + palmSteps.getZ() * pPalmOffset + fingerSteps.getZ() * pFingerOffset);
        } else {
            throw new IllegalArgumentException("Invalid forwards & up combination");
        }
    }
    static class MultiBlockCacheLoader extends CacheLoader<BlockPos, MultiBlockInWorld> {
        private final LevelReader level;
        private final boolean loadChunks;

        public MultiBlockCacheLoader(LevelReader pLevel, boolean pLoadChunks) {
            this.level = pLevel;
            this.loadChunks = pLoadChunks;
        }

        public MultiBlockInWorld load(BlockPos pPos, MultiBlockInWorldType state) {
            return load(pPos).setType(state);
        }
        public @NotNull MultiBlockInWorld load(@NotNull BlockPos pPos) {
            return new MultiBlockInWorld(this.level, pPos, this.loadChunks);
        }
    }
    public static class MultiBlockPatternMatch {
        private final List<MultiBlockInWorld> masters = new ArrayList<>();
        private final List<MultiBlockInWorld> slaves = new ArrayList<>();
        private final BlockPos frontTopLeft;
        private final Direction forwards;
        private final Direction up;
        private final LoadingCache<BlockPos, MultiBlockInWorld> cache;
        private final int width;
        private final int height;
        private final int depth;

        public MultiBlockPatternMatch(BlockPos pFrontTopLeft, Direction pForwards, Direction pUp, LoadingCache<BlockPos, MultiBlockInWorld> pCache, int pWidth, int pHeight, int pDepth) {
            this.frontTopLeft = pFrontTopLeft;
            this.forwards = pForwards;
            this.up = pUp;
            this.cache = pCache;
            this.width = pWidth;
            this.height = pHeight;
            this.depth = pDepth;
            for(int i = 0; i < this.getWidth(); ++i) {
                for(int j = 0; j < this.getHeight(); ++j) {
                    for (int k = 0; k < this.getDepth(); ++k) {
                        BlockPos pos = new BlockPos(i, j, k);
                        MultiBlockInWorld blockInWorld = getBlock(pos).setOffset(pos);
                        if(blockInWorld.type == MultiBlockInWorldType.SLAVE && !slaves.contains(pos))
                            slaves.add(blockInWorld);
                        if(blockInWorld.type == MultiBlockInWorldType.MASTER && !masters.contains(pos))
                            masters.add(blockInWorld);
                    }
                }
            }
        }

        public BlockPos getFrontTopLeft() {
            return this.frontTopLeft;
        }

        public Direction getForwards() {
            return this.forwards;
        }

        public Direction getUp() {
            return this.up;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public int getDepth() {
            return this.depth;
        }

        public MultiBlockInWorld getBlock(BlockPos offset) {
            return getBlock(offset.getX(), offset.getY(), offset.getZ());
        }
        public MultiBlockInWorld getBlock(int pPalmOffset, int pThumbOffset, int pFingerOffset) {
            return this.cache.getUnchecked(this.frontTopLeft.offset(MultiBlockPattern.translateAndRotate(this.getForwards(), this.getUp(), pPalmOffset, pThumbOffset, pFingerOffset)));
        }

        public List<MultiBlockInWorld> getTypes(MultiBlockInWorldType type)
        {
            return switch (type){
                case NOT_INCLUDED -> List.of();
                case SLAVE -> slaves;
                case MASTER -> masters;
            };
        }

        public String toString() {
            return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forwards).add("frontTopLeft", this.frontTopLeft).toString();
        }
    }
}
