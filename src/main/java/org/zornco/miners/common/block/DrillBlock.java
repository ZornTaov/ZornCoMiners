package org.zornco.miners.common.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class DrillBlock extends Block {
    public static final IntegerProperty TIER = IntegerProperty.create("tier", 0, 32);

    public DrillBlock(Properties p_49224_) {
        super(p_49224_.noOcclusion().dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(this.getTierProperty(), 0).setValue(FACING, Direction.DOWN));
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_52669_) {
        return this.defaultBlockState().setValue(FACING, p_52669_.getNearestLookingDirection());
    }

    private IntegerProperty getTierProperty() {
        return TIER;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TIER, FACING);
    }
}
