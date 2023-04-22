package org.zornco.miners.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.zornco.miners.common.tile.RenderableTile;

public class RenderableBlock extends Block implements EntityBlock {
    public static final BooleanProperty DUMMY = BooleanProperty.create("dummy");

    public RenderableBlock() {
        super(BlockBehaviour.Properties.of(Material.WOOL).noOcclusion().dynamicShape());
        registerDefaultState(getStateDefinition().any().setValue(DUMMY, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(DUMMY);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RenderableTile(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity entity = pLevel.getBlockEntity(pPos);
        boolean state = entity == null ? false : true;

        if (pLevel.isClientSide) {
            pPlayer.sendSystemMessage(Component.literal("Has Client BE: " + state));
            if (state)
                pPlayer.sendSystemMessage(Component.literal("BE Class: " + entity.getClass()));
        } else if (state) {
            pPlayer.sendSystemMessage(Component.literal("Has Server BE: " + state));
            if (entity instanceof RenderableTile RE)
                RE.markBlockForRenderUpdate();
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }
}
