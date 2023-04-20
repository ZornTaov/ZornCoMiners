package org.zornco.miners.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.zornco.miners.common.config.Configuration;
import org.zornco.miners.common.core.MinerTierRegistration;
import org.zornco.miners.common.core.Registration;
import org.zornco.miners.common.block.MinerBlock;
import org.zornco.miners.common.capability.EnergyCap;
import org.zornco.miners.common.recipe.FakeInventory;
import org.zornco.miners.common.recipe.MinerRecipe;
import org.zornco.miners.common.recipe.RecipeRegistration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class MinerTile extends BlockEntity {

    private static final HashMap<Block, BlockPattern> patterns = new HashMap<>();
    private static final HashMap<TagKey<Block>, BlockPattern> tagPatterns = new HashMap<>();
    private List<ItemStack> lastOutputs = null;
    private BlockPattern pattern = null; // the current pattern we are using.

    EnergyCap energyStorage;
    ItemStackHandler storage = new ItemStackHandler(1);

    LazyOptional<EnergyCap> energy;
    LazyOptional<IItemHandler> item = LazyOptional.of(() -> storage);
    public int ticksRunning = 0;

    public MinerTile(BlockPos p_155229_, BlockState p_155230_) {
        super(Registration.MINER_TILE.get(), p_155229_, p_155230_);
        energyStorage = new EnergyCap(10000);
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
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ticks", this.ticksRunning);
        tag.put("energy", energyStorage.serializeNBT());
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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
    }

    public static void tickCommon(Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull MinerTile tile) {
        if(level == null) return;
        if(level.isClientSide()) return;
        tile.ticksRunning++;
        int validationSpeed = 20; // validate every second
        // only do validation once every so often

        if (tile.ticksRunning % validationSpeed == 0) {
            if (tile.pattern == null) {

                // do multiblock validation
                // TODO allow for any number of ores, this changes yield per operation maybe?
                for (MinerRecipe recipe : level.getRecipeManager().getRecipesFor(RecipeRegistration.MINER_RECIPE.get(), FakeInventory.INSTANCE, level)) {
                    // get/cache pattern
                    try {
                        if (recipe.getResource() != Blocks.AIR) {
                            tile.pattern = getPattern(recipe.getResource(), recipe.codecTier(), BlockStatePredicate.forBlock(recipe.getResource()));
                        } else if (!recipe.getResourceTag().location().getPath().equals("air")) {
                            tile.pattern = getPattern(recipe.getResourceTag(), recipe.codecTier(), (blockState) -> blockState.is(recipe.getResourceTag()));
                        }
                    } catch (Exception e) {
                        // should never happen?
                        return;
                    }

                    BlockPattern.BlockPatternMatch match = tile.pattern.find(level, tile.getBlockPos());

                    if (match != null) {
                        level.setBlockAndUpdate(pos, state.setValue(MinerBlock.VALID, true));
                        tile.lastOutputs = recipe.codecOutputs();
                        break;
                    } else {
                        level.setBlockAndUpdate(pos, state.setValue(MinerBlock.VALID, false));
                        tile.lastOutputs = null;
                    }
                }
            } else {
                if (tile.pattern.find(level, pos) == null) {
                    tile.pattern = null;
                    tile.lastOutputs = null;
                    level.setBlockAndUpdate(pos, state.setValue(MinerBlock.VALID, false));
                }
            }
        }

        // get out early if recipe isn't set
        if (tile.pattern == null || tile.lastOutputs == null)
            return;

        // TODO change with tier?
        int speed = 10; // 2op/1s
        // only do operation once every so often
        if (tile.ticksRunning % speed != 0) return;

        // only run if structure is valid
        if (!level.getBlockState(pos).getValue(MinerBlock.VALID)) {
            return;
        }

        // only do operation if power is supplied and config is enabled
        if (Configuration.useEnergy() &&
            tile.energyStorage.extractEnergy(Configuration.energyUsedPerTick(), true) != Configuration.energyUsedPerTick()) return;

        BlockEntity tileAbove = level.getBlockEntity(pos.above());
        if(tileAbove == null) return;
        LazyOptional<IItemHandler> cap = tileAbove.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN);
        AtomicBoolean pushed = new AtomicBoolean(false);

        cap.ifPresent(storage -> {
            for (ItemStack output : tile.lastOutputs) {
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

        if (!pushed.get()) {
            for (ItemStack output : tile.lastOutputs) {
                ItemStack stack = output.copy();
                if (stack.isEmpty())
                    return; // bad recipe!
                //ItemStack stack = new ItemStack(blockUnder.getBlock().asItem());
                // TODO incorporate percentage outputs?
                for (int i = 0; i < tile.storage.getSlots(); i++) {
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

    @NotNull
    private static BlockPattern getPattern(Block key, int tier, Predicate<BlockState> predicate) throws Exception {
        if (!patterns.containsKey(key)) {
            patterns.put(key, getBuild(predicate, tier));
        }
        BlockPattern pattern = patterns.get(key);
        if (pattern == null)
            throw new Exception("This should never happen");
        return pattern;
    }

    @NotNull
    private static BlockPattern getPattern(TagKey<Block> key, int tier, Predicate<BlockState> predicate) throws Exception {
        if (!tagPatterns.containsKey(key)) {
            tagPatterns.put(key, getBuild(predicate, tier));
        }
        BlockPattern pattern = tagPatterns.get(key);
        if (pattern == null)
            throw new Exception("This should never happen");
        return pattern;
    }

    @NotNull
    private static BlockPattern getBuild(Predicate<BlockState> predicate, int tier) {
        return BlockPatternBuilder
            .start()
            .aisle("     ", "     ", "  m  ", "     ", "     ")
            .aisle("  #  ", "  #  ", "##d##", "  #  ", "  #  ")
            .aisle("  #  ", " ooo ", "#ooo#", " ooo ", "  #  ")
            .aisle("     ", " ooo ", " ooo ", " ooo ", "     ")
            .aisle("     ", " ooo ", " ooo ", " ooo ", "     ")
            .where('o', BlockInWorld.hasState(predicate))
            .where('#', BlockInWorld.hasState(blockState -> MinerTierRegistration.isValidForTier(blockState.getBlock(), tier)))
            .where('m', BlockInWorld.hasState(BlockPredicate.forBlock(Registration.MINER_BLOCK.get())))
            .where('d', BlockInWorld.hasState(BlockPredicate.forBlock(Registration.DRILL_BLOCK.get())))
            .build();
    }
}
