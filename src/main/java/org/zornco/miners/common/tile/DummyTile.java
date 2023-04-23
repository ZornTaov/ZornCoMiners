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
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zornco.miners.common.core.Registration;

import javax.annotation.Nonnull;

public class DummyTile extends BlockEntity {
    private static final String BLOCKSTATE_NBT = "originalstate";
    @NotNull
    private BlockState originalBlockState = Blocks.IRON_BLOCK.defaultBlockState();

    public DummyTile(BlockPos pPos, BlockState pBlockState) {
        super(Registration.DUMMY_TILE.get(), pPos, pBlockState);
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
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        this.originalBlockState = NbtUtils.readBlockState(tag.getCompound(BLOCKSTATE_NBT));
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(BLOCKSTATE_NBT, NbtUtils.writeBlockState(this.originalBlockState));
    }
}
