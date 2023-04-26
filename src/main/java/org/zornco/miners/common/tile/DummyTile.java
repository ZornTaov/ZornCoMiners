package org.zornco.miners.common.tile;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zornco.miners.ZornCoMiners;
import org.zornco.miners.common.multiblock.pattern.MultiBlockInWorld;
import org.zornco.miners.common.multiblock.pattern.MultiBlockInWorldType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.zornco.miners.common.core.Codecs.BLOCK_POS_LIST_CODEC;

public abstract class DummyTile extends BlockEntity {
    public static final String BLOCKSTATE_NBT = "originalstate";
    public static final String CONTROLLER_NBT = "controllerpos";
    public static final String MB_DATA_NBT = "mbdata";
    private static final String SLAVES_MB = "slaves";

    private final List<BlockPos> SLAVES = new ArrayList<>();

    @NotNull
    private BlockState originalBlockState = Blocks.IRON_BLOCK.defaultBlockState();
    private MultiBlockInWorldType type = MultiBlockInWorldType.NOT_INCLUDED;
    private boolean formed = false;
    private BlockPos controller = null;

    public DummyTile(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }


    public void markBlockForRenderUpdate() {
        if (getLevel() == null)
            return;

        BlockState state = getLevel().getBlockState(getBlockPos());
        getLevel().sendBlockUpdated(getBlockPos(), state, state, Block.UPDATE_CLIENTS);
    }

    public void deconstruct() {
        if (this.type == MultiBlockInWorldType.MASTER) {
            SLAVES.forEach(pos -> {
                if (level.getBlockEntity(pos) instanceof DummyTile tile)
                    level.setBlock(pos, tile.getOriginalBlockState(), Block.UPDATE_ALL);
            });
        }
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

    public void setController(BlockPos controller) {
        this.controller = controller;
    }

    public BlockPos getController() {
        return controller;
    }

    public DummyTile getMaster() {
        if (getMultiBlockType() == MultiBlockInWorldType.MASTER)
            return this;
        if (getController() != null && level.getBlockEntity(getController()) instanceof DummyTile tile)
            return tile;

        return null;
    }
    public List<BlockPos> getSlaves()
    {
        return SLAVES;
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
        load(tag);
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        CompoundTag MBData = tag.getCompound(MB_DATA_NBT);

        this.originalBlockState = NbtUtils.readBlockState(MBData.getCompound(BLOCKSTATE_NBT));
        this.formed = MBData.getBoolean("formed");
        this.type = MBData.getBoolean("isMaster") ? MultiBlockInWorldType.MASTER : MultiBlockInWorldType.SLAVE;
        if (MBData.contains(CONTROLLER_NBT))
            this.controller = NbtUtils.readBlockPos(MBData.getCompound(CONTROLLER_NBT));
        ListTag list = MBData.getList(SLAVES_MB, CompoundTag.TAG_LIST);
        BLOCK_POS_LIST_CODEC.decode(NbtOps.INSTANCE, list).resultOrPartial(ZornCoMiners.LOGGER::error)
            .ifPresent(listTagPair -> SLAVES.addAll(listTagPair.getFirst()));
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag MBData = new CompoundTag();
        MBData.put(BLOCKSTATE_NBT, NbtUtils.writeBlockState(this.originalBlockState));
        MBData.putBoolean("formed", getFormed());
        MBData.putBoolean("isMaster", getMultiBlockType() == MultiBlockInWorldType.MASTER);
        if (controller != null)
            MBData.put(CONTROLLER_NBT, NbtUtils.writeBlockPos(controller));
        tag.put(MB_DATA_NBT, MBData);
        BLOCK_POS_LIST_CODEC.encodeStart(NbtOps.INSTANCE, SLAVES).resultOrPartial(ZornCoMiners.LOGGER::error)
            .ifPresent(tag1 -> tag.put(SLAVES_MB, tag1));
//        ListTag list = new ListTag();
//        getSlaves().forEach(pos -> list.add(NbtUtils.writeBlockPos(pos)));
//        if (!list.isEmpty())
//            tag.put(SLAVES_MB, list);
    }
}
