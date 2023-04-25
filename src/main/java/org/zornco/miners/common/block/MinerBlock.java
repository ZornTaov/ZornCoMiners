package org.zornco.miners.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zornco.miners.common.core.BuildType;
import org.zornco.miners.common.core.Registration;
import org.zornco.miners.common.tile.DummyTile;
import org.zornco.miners.common.tile.MinerTile;

public class MinerBlock extends DummyBlock {
    public static final String SCREEN_MINER = "screen.miner";
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

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos,
                                          @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (!pLevel.isClientSide() && !pState.getValue(MB_SLAVE))
        {
            if (pLevel.getBlockEntity(pPos) instanceof MinerTile tile)
            {
                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public @NotNull Component getDisplayName() {
                        return Component.translatable(SCREEN_MINER);
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
                        return new MinerContainer(windowId, pPos, playerInventory, playerEntity);
                    }
                };
                NetworkHooks.openScreen((ServerPlayer) pPlayer, containerProvider, tile.getBlockPos());
                return InteractionResult.SUCCESS;
            } else {
                throw new IllegalStateException("Our named container provider is missing!");
            }
        }


        return super.use(pState,pLevel,pPos,pPlayer,pHand,pHit);
    }
}
