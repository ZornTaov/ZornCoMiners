package org.zornco.miners.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.zornco.miners.Registration;

import javax.annotation.Nonnull;

public class MinerTile extends BlockEntity {

    public int ticksRunning = 0;

    public MinerTile(BlockPos p_155229_, BlockState p_155230_) {
        super(Registration.MINER_TILE.get(), p_155229_, p_155230_);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        this.ticksRunning = tag.getInt("ticks");
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ticks", this.ticksRunning);
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

    public static void tickCommon(Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull MinerTile tile) {
        if(level == null) return;
        // TODO change with tier?
        int speed = 40; // 1op/second
        int validationSpeed = 20; // validate every second
        tile.ticksRunning++;
        // only do validation once every so often
        if (tile.ticksRunning % validationSpeed == 0) {
            // do multiblock validation
            boolean isValid = true;
            // TODO multiblock/recipe check
            // TODO cache recipe?
            // TODO allow for any number of ores, this changes yield per operation maybe?

            if (!isValid) return;
        }

        // only do operation once every so often
        if (tile.ticksRunning % speed != 0) return;
        BlockState blockUnder = level.getBlockState(pos.below());

        // TODO only do operation if power is supplied and config is enabled

        // TODO change to match recipe
        if(blockUnder.is(Tags.Blocks.ORES))
        {
            BlockEntity tileAbove = level.getBlockEntity(pos.above());
            if(tileAbove == null) return;
            LazyOptional<IItemHandler> cap = tileAbove.getCapability(ForgeCapabilities.ITEM_HANDLER);
            cap.ifPresent( storage ->
            {
                // TODO choose to get item from drops, from block itself, or from recipe

                // get item from drop maybe?
//                ItemStack pickaxe = Items.DIAMOND_PICKAXE.getDefaultInstance();
//                pickaxe.enchant(Enchantments.SILK_TOUCH, 1);
//                LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel) level))
//                    .withRandom(level.random)
//                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
//                    .withParameter(LootContextParams.TOOL, pickaxe)
//                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, tile);
//                List<ItemStack> drops = blockUnder.getDrops(lootcontext$builder);
//                if(drops.stream().findFirst().isEmpty()) return;

                // OR get item from block
                // TODO have count affected by yield?
                ItemStack stack = new ItemStack(blockUnder.getBlock().asItem());
                for (int i = 0; i < storage.getSlots(); i++) {
                    if (storage.isItemValid(i, stack) && storage.insertItem(i, stack, true).isEmpty()) {
                        storage.insertItem(i, stack, false);
                        break;
                    }
                }
            });
        }
        // TODO if config is true, remove random/furthest oreblock after X uses
    }

}
