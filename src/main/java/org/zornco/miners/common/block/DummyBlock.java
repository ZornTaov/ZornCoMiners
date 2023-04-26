package org.zornco.miners.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.zornco.miners.common.multiblock.pattern.MultiBlockInWorldType;
import org.zornco.miners.common.tile.DummyTile;

public abstract class DummyBlock extends BaseEntityBlock {
    public static final BooleanProperty MB_SLAVE = BooleanProperty.create("mbslave");

    public DummyBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(MB_SLAVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(MB_SLAVE);
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (pLevel.getBlockEntity(pPos) instanceof DummyTile tile) {
            if (tile.getMaster() != null) {
                tile.getMaster().deconstruct();
            } else {
                // TODO: Log this later!
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.getBlockEntity(pPos) instanceof DummyTile tile) {
            if (tile.getOriginalBlockState() != null)
                if (tile.getOriginalBlockState().getBlock().asItem() != null)
                    return new ItemStack(tile.getOriginalBlockState().getBlock().asItem());
        }

        return ItemStack.EMPTY;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity entity = pLevel.getBlockEntity(pPos);
        boolean state = entity != null;

        if (!pLevel.isClientSide && entity instanceof DummyTile tile)
            tile.markBlockForRenderUpdate();

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }
}
