package org.zornco.miners.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
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
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.zornco.miners.Configuration;
import org.zornco.miners.Registration;
import org.zornco.miners.block.MinerBlock;
import org.zornco.miners.capability.EnergyCap;
import org.zornco.miners.recipe.FakeInventory;
import org.zornco.miners.recipe.MinerRecipe;
import org.zornco.miners.recipe.RecipeRegistration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MinerTile extends BlockEntity {

    private static BlockPattern pattern;
    EnergyCap energyStorage;
    LazyOptional<EnergyCap> energy;
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
        if (cap == ForgeCapabilities.ENERGY)
            return energy.cast();

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energy.invalidate();
    }

    public static void tickCommon(Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull MinerTile tile) {
        if(level == null) return;
        tile.ticksRunning++;

        int validationSpeed = 20; // validate every second
        final BlockState blockUnder;
        // only do validation once every so often
        if (tile.ticksRunning % validationSpeed == 0) {
            // do multiblock validation
            boolean isValid = false;
            // TODO multiblock/recipe check
            // TODO cache recipe?
            // TODO allow for any number of ores, this changes yield per operation maybe?

            BlockPattern pattern = getPattern();
            BlockPattern.BlockPatternMatch patternMatch = pattern.find(level, tile.getBlockPos());
            if (patternMatch != null)
            {
                BlockPos ftl = patternMatch.getFrontTopLeft();
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        //speed caps?
                    }
                }
                blockUnder = level.getBlockState(ftl.offset(2, -1, 2));
                level.setBlockAndUpdate(pos, state.setValue(MinerBlock.PROP_IS_VALID, MinerBlock.ValidStatus.VALID));
                isValid = true;
            }
            else {
                level.setBlockAndUpdate(pos, state.setValue(MinerBlock.PROP_IS_VALID, MinerBlock.ValidStatus.INVALID));
                // to make intellisense shut up
                blockUnder = null;
            }

            if (!isValid) return;
        }
        else blockUnder = level.getBlockState(pos.below());
//        TagKey<Block> tagKey = ;
//        boolean blockUnderIsValid = blockUnder.is(tagKey);
        // TODO change with tier?
        int speed = 10; // 2op/1s
        // only do operation once every so often
        if (tile.ticksRunning % speed != 0) return;

        // only run if structure is valid
        if (level.getBlockState(pos).getValue(MinerBlock.PROP_IS_VALID) != MinerBlock.ValidStatus.VALID) {
            return;
        }
        // only do operation if power is supplied and config is enabled
        if (Configuration.useEnergy() &&
            tile.energyStorage.extractEnergy(Configuration.energyUsedPerTick(), true) != Configuration.energyUsedPerTick()) return;

        // TODO change to match recipe
        //boolean blockUnderIsValid = blockUnder.is(Tags.Blocks.ORES);
        BlockEntity tileAbove = level.getBlockEntity(pos.above());
        if(tileAbove == null) return;
        LazyOptional<IItemHandler> cap = tileAbove.getCapability(ForgeCapabilities.ITEM_HANDLER);
        cap.ifPresent( storage ->
        {
            // TODO choose to get item from drops, from block itself, or from recipe

            // get item from drop maybe?
//            ItemStack pickaxe = Items.DIAMOND_PICKAXE.getDefaultInstance();
//            pickaxe.enchant(Enchantments.SILK_TOUCH, 1);
//            LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel) level))
//                .withRandom(level.random)
//                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
//                .withParameter(LootContextParams.TOOL, pickaxe)
//                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, tile);
//            List<ItemStack> drops = blockUnder.getDrops(lootcontext$builder);
//            if(drops.stream().findFirst().isEmpty()) return;

            // OR get item from block
            // TODO have count affected by yield?

            ItemStack stack = null;
            for (MinerRecipe minerBlocks : level.getRecipeManager().getRecipesFor(RecipeRegistration.MINER_RECIPE.get(), FakeInventory.INSTANCE, level))
            {
                if (blockUnder.getBlock().equals(minerBlocks.getResource()) || blockUnder.is(minerBlocks.getResourceTag())) {
                    stack = minerBlocks.codecOutputs().get(0);
                    break;
                }
            }
            if (stack == null) return;
            //ItemStack stack = new ItemStack(blockUnder.getBlock().asItem());
            for (int i = 0; i < storage.getSlots(); i++) {
                if (storage.isItemValid(i, stack) && storage.insertItem(i, stack, true).isEmpty()) {
                    storage.insertItem(i, stack, false);
                    break;
                }
            }
            if (Configuration.useEnergy())
                tile.energyStorage.extractEnergy(Configuration.energyUsedPerTick(), false);
        });
        // TODO if config is true, remove random/furthest oreblock after X uses
    }

    @NotNull
    private static BlockPattern getPattern() {
        if (pattern == null) {
            pattern = BlockPatternBuilder
                .start()
                .aisle("#   #", "     ", "  m  ", "     ", "#   #")
                .aisle("#####", "#ooo#", "#ooo#", "#ooo#", "#####")
                .aisle("#   #", " ooo ", " ooo ", " ooo ", "#   #")
                .aisle("#   #", " ooo ", " ooo ", " ooo ", "#   #")
                .where('#', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.IRON_BARS)))
                .where('o', BlockInWorld.hasState((blockState) -> blockState.is(Tags.Blocks.ORES)))
                .where('m', BlockInWorld.hasState(BlockPredicate.forBlock(Registration.MINER_BLOCK.get())))
                .where(' ', BlockInWorld.hasState(BlockStatePredicate.ANY))
                .build();
        }
        return pattern;
    }
}
