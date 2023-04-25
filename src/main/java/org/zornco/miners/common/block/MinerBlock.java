package org.zornco.miners.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zornco.miners.common.core.BuildType;
import org.zornco.miners.common.core.Registration;
import org.zornco.miners.common.tile.DummyTile;
import org.zornco.miners.common.tile.MinerTile;

public class MinerBlock extends DummyBlock {
    public static final BooleanProperty VALID = BooleanProperty.create("valid");
    public static final EnumProperty<BuildType> TYPE = EnumProperty.create("type", BuildType.class);

    public MinerBlock(Properties p_49224_) {
        super(p_49224_);
        this.registerDefaultState(this.stateDefinition.any().setValue(VALID, false).setValue(TYPE, BuildType.MULTIBLOCK));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VALID, TYPE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MinerTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, Registration.MINER_TILE.get(), MinerTile::tickCommon);
    }

    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        if (state.getValue(DummyBlock.MB_SLAVE))
            return RenderShape.INVISIBLE;

        return RenderShape.MODEL;
    }
}
