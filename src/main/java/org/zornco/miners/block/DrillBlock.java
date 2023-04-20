package org.zornco.miners.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class DrillBlock extends Block {
    public static final IntegerProperty TIER = IntegerProperty.create("tier", 0, 32);
    public DrillBlock(Properties p_49224_) {
        super(p_49224_.noOcclusion().dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(this.getTierProperty(), 0));

    }

    private IntegerProperty getTierProperty() {
        return TIER;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TIER);
    }
}
