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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zornco.miners.common.core.Registration;
import org.zornco.miners.common.multiblock.IMultiblockFormer;
import org.zornco.miners.common.multiblock.MultiBlockManager;
import org.zornco.miners.common.multiblock.pattern.MultiBlockInWorld;
import org.zornco.miners.common.multiblock.pattern.MultiBlockInWorldType;
import org.zornco.miners.common.multiblock.pattern.MultiBlockPattern;
import org.zornco.miners.common.tile.DummyTile;
import org.zornco.miners.common.tile.MinerTile;

public class MinerBlock extends DummyBlock implements IMultiblockFormer {
    public static final String SCREEN_MINER = "screen.miner";

    public MinerBlock(Properties p_49224_) {
        super(p_49224_);
        this.registerDefaultState(this.stateDefinition.any());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
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
        if (!pState.getValue(MB_SLAVE)) {
            if (!pLevel.isClientSide) {
                if (pLevel.getBlockEntity(pPos) instanceof MinerTile tile) {
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
            } else {
                return InteractionResult.SUCCESS;
            }
        }


        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void form(Level level, BlockPos origin, MultiBlockPattern.MultiBlockPatternMatch pattern) {
        MultiBlockInWorld master = pattern.getMaster();
        if (master == null) return; // Cant continue as there is no 'master'

        pattern.getAllTypes().forEach(multiBlockInWorld -> formDummy(level, multiBlockInWorld, master.getPos()));
        pattern.getAllTypes().forEach(multiBlockInWorld -> finishFormingDummy(level, multiBlockInWorld));

        if(level.getBlockEntity(master.getPos()) instanceof DummyTile tile)
        {
            tile.getSlaves().addAll(pattern.getAllTypes().stream().map(MultiBlockInWorld::getPos).toList());
        }
    }

    public void formDummy(Level level, MultiBlockInWorld multiBlockInWorld, BlockPos master) {
        BlockState original = multiBlockInWorld.getState();
        BlockState dummy = Registration.MINER_BLOCK.get().defaultBlockState().setValue(DummyBlock.MB_SLAVE, multiBlockInWorld.getType() == MultiBlockInWorldType.SLAVE);

        level.setBlock(multiBlockInWorld.getPos(), dummy, Block.UPDATE_ALL);

        if (level.getBlockEntity(multiBlockInWorld.getPos()) instanceof DummyTile tile) {
            tile.setOriginalBlockState(original);
            tile.setController(master);
            tile.setMultiBlockType(multiBlockInWorld.getType());

            tile.setChanged();
        }
    }

    private void finishFormingDummy(Level level, MultiBlockInWorld multiBlockInWorld) {
        if (level.getBlockEntity(multiBlockInWorld.getPos()) instanceof DummyTile tile) {
            tile.setFormed(true);
        }
    }
}
