package org.zornco.miners.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.zornco.miners.common.config.Configuration;
import org.zornco.miners.common.core.Registration;
import org.zornco.miners.common.block.MinerBlock;
import org.zornco.miners.common.capability.EnergyCap;
import org.zornco.miners.common.recipe.MinerRecipe;
import org.zornco.miners.common.recipe.RecipeRegistration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.zornco.miners.common.block.MinerBlock.TYPE;

public class MinerTile extends DummyTile {
    private int minTier = -1;

    EnergyCap energyStorage;
    ItemStackHandler storage = new ItemStackHandler(7){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == 0 && stack.getItem() == Registration.DRILL_BLOCK.get().asItem() || slot < 8;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (slot == 0 && stack.getItem() != Registration.DRILL_BLOCK.get().asItem())
                return stack;
            return super.insertItem(slot, stack, simulate);
        }
    };

    LazyOptional<EnergyCap> energy;
    LazyOptional<IItemHandler> item = LazyOptional.of(() -> storage);
    public int ticksRunning = 0;
    private MinerRecipe recipe;

    public MinerTile(BlockPos p_155229_, BlockState p_155230_) {
        super(Registration.MINER_TILE.get(), p_155229_, p_155230_);
        energyStorage = new EnergyCap(10000){
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
        this.energy = LazyOptional.of(() -> this.energyStorage);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        this.ticksRunning = tag.getInt("ticks");
        energyStorage.deserializeNBT(tag.get("energy"));
        if (tag.contains("inventory")) {
            storage.deserializeNBT(tag.getCompound("inventory"));
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ticks", this.ticksRunning);
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("inventory", storage.serializeNBT());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, final @Nullable Direction side) {
        if (Configuration.useEnergy() && cap == ForgeCapabilities.ENERGY)
            return energy.cast();

        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return item.cast();

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energy.invalidate();
        item.invalidate();
    }

    public static void tickCommon(Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull MinerTile tile) {
        if(level == null) return;
        if(level.isClientSide()) return;
        if(!tile.getFormed()) return;
        if(!(tile.getOriginalBlockState().getBlock() == Registration.MINER_BLOCK.get())) return;

        tile.ticksRunning++;

        if (tile.storage.getStackInSlot(0).getItem() != Registration.DRILL_BLOCK.get().asItem()) return;

        // TODO change with tier?
        int speed = 10; // 2op/1s
        // only do operation once every so often
        if (tile.ticksRunning % speed != 0) return;
        BlockPos center = pos.below(3);
        var blockStates = getBlocksIn(
            AABB.ofSize(new Vec3(center.getX(), center.getY(), center.getZ()), 3, 3, 3).move(0.5,0.5,0.5)
        ).map(bp -> new BlockInWorld(level, bp, true)).filter(biw -> !biw.getState().isAir()).toList();
        if(blockStates.isEmpty()) return;

        for (int i = 0; i < 100; i++) {
            BlockInWorld randBlockState = blockStates.get(level.getRandom().nextInt(blockStates.size()));
            tile.recipe = switch (state.getValue(TYPE))
            {
                case MULTIBLOCK -> RecipeRegistration.getRecipe(randBlockState.getState(), level);

                case ORE -> RecipeRegistration.getRecipe(level.getBlockState(pos.below(1)), level);
            };
            if(tile.recipe != null) break;
        }

        // get out early if recipe isn't set
        if (tile.recipe == null)
            return;

        // only do operation if power is supplied and config is enabled
        if (Configuration.useEnergy() &&
            tile.energyStorage.extractEnergy(Configuration.energyUsedPerTick(), true) != Configuration.energyUsedPerTick()) return;

        BlockEntity tileAbove = level.getBlockEntity(pos.above());
        AtomicBoolean pushed = new AtomicBoolean(false);
        if(tileAbove != null) {
            LazyOptional<IItemHandler> cap = tileAbove.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN);

            cap.ifPresent(storage -> {
                for (ItemStack output : tile.recipe.codecOutputs()) {
                    ItemStack stack = output.copy();
                    if (stack.isEmpty())
                        return; // bad recipe!
                    //ItemStack stack = new ItemStack(blockUnder.getBlock().asItem());
                    // TODO incorporate percentage outputs?
                    for (int i = 0; i < storage.getSlots(); i++) {
                        if (storage.isItemValid(i, stack) && storage.insertItem(i, stack, true).isEmpty()) {
                            storage.insertItem(i, stack, false);
                            pushed.set(true);
                            break;
                        }
                    }
                }
            });
        }

        if (!pushed.get()) {
            for (ItemStack output : tile.recipe.codecOutputs()) {
                ItemStack stack = output.copy();
                if (stack.isEmpty())
                    return; // bad recipe!
                //ItemStack stack = new ItemStack(blockUnder.getBlock().asItem());
                // TODO incorporate percentage outputs?
                for (int i = 1; i < tile.storage.getSlots(); i++) {
                    if (tile.storage.isItemValid(i, stack) && tile.storage.insertItem(i, stack, true).isEmpty()) {
                        tile.storage.insertItem(i, stack, false);
                        pushed.set(true);
                        break;
                    }
                }
            }
        }

        if (pushed.get() && Configuration.useEnergy())
            tile.energyStorage.extractEnergy(Configuration.energyUsedPerTick(), false);
        // TODO if config is true, remove random/furthest oreblock after X uses
    }

    @Nonnull
    public static Stream<BlockPos> getBlocksIn(AABB bounds) {
        return BlockPos.betweenClosedStream(bounds.contract(1, 1, 1));
    }
}
