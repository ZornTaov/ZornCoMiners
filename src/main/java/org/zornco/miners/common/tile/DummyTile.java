package org.zornco.miners.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zornco.miners.common.multiblock.pattern.MultiBlockInWorld;
import org.zornco.miners.common.multiblock.pattern.MultiBlockInWorldType;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class DummyTile extends BlockEntity {
    private static final String BLOCKSTATE_NBT = "originalstate";
    @NotNull
    private BlockState originalBlockState = Blocks.IRON_BLOCK.defaultBlockState();
    private MultiBlockInWorldType type = MultiBlockInWorldType.NOT_INCLUDED;
    private boolean formed = false;

    public DummyTile(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }


    public void markBlockForRenderUpdate() {
        if (getLevel() == null)
            return;

        BlockState state = getLevel().getBlockState(getBlockPos());
        getLevel().sendBlockUpdated(getBlockPos(), state, state, Block.UPDATE_CLIENTS);
    }

    public BlockState getOriginalBlockState() {
        return originalBlockState;
    }

    public void setOriginalBlockState(BlockState state) {
        if (state == null) {
            this.originalBlockState = Blocks.AIR.defaultBlockState();
            markBlockForRenderUpdate();
            return;
        }

        this.originalBlockState = state;
        markBlockForRenderUpdate();
    }

    public void setMultiBlockType(MultiBlockInWorldType type) {
        this.type = type;
    }

    public MultiBlockInWorldType getMultiBlockType() {
        return type;
    }

    public void setFormed(boolean value) {
        this.formed = value;
    }

    public boolean getFormed() {
        return formed;
    }


    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.originalBlockState = NbtUtils.readBlockState(tag.getCompound(BLOCKSTATE_NBT));
        this.formed = tag.getBoolean("formed");
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        this.originalBlockState = NbtUtils.readBlockState(tag.getCompound(BLOCKSTATE_NBT));
        this.formed = tag.getBoolean("formed");
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(BLOCKSTATE_NBT, NbtUtils.writeBlockState(this.originalBlockState));
        tag.putBoolean("formed", getFormed());
    }
}
