package org.zornco.miners.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.zornco.miners.common.capability.EnergyCap;
import org.zornco.miners.common.core.Registration;
import org.zornco.miners.common.tile.MinerTile;

@SuppressWarnings("SameParameterValue")
public class MinerContainer extends AbstractContainerMenu {
    private final MinerTile blockEntity;
    private final Player playerEntity;
    private final IItemHandler playerInventory;
    private final int lastIndex;

    public MinerContainer(int pContainerId, BlockPos pos, Inventory inv, Player player) {
        super(Registration.MINER_CONTAINER.get(), pContainerId);
        blockEntity = (MinerTile) player.getCommandSenderWorld().getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inv);

        if (blockEntity != null) {
            blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
                addSlot(new SlotItemHandler(h, 0, 44, 17));
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 2; j++) {
                        addSlot(new SlotItemHandler(h, 1+i+j*3, 108+i*18, 26+j*18));
                    }
                }
            });
        }
        layoutPlayerInventorySlots(8, 84);
        lastIndex = this.slots.size();
        trackPower();
    }
    // Setup syncing of power from server to client so that the GUI can show the amount of power in the block
    private void trackPower() {
        // Unfortunatelly on a dedicated server ints are actually truncated to short so we need
        // to split our integer here (split our 32 bit integer into two 16 bit integers)
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy() & 0xffff;
            }

            @Override
            public void set(int value) {
                if (blockEntity == null) return;
                blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(h -> {
                    int energyStored = h.getEnergyStored() & 0xffff0000;
                    ((EnergyCap)h).setEnergy(energyStored + (value & 0xffff));
                });
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (getEnergy() >> 16) & 0xffff;
            }

            @Override
            public void set(int value) {
                if (blockEntity == null) return;
                blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(h -> {
                    int energyStored = h.getEnergyStored() & 0x0000ffff;
                    ((EnergyCap)h).setEnergy(energyStored | (value << 16));
                });
            }
        });
    }
    public boolean getStatus()
    {
        return blockEntity.getFormed();
    }
    public int getEnergy() {
        return blockEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            // from miner slots to player inv
            if (index < 7) {
                if (!this.moveItemStackTo(stack, 7, lastIndex, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            } else {
                //player inv to drill slot
                if (stack.getItem() == Registration.DRILL_BLOCK.get().asItem()) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                //player inv to player hotbar
                } else if (index < lastIndex-9) {
                    if (!this.moveItemStackTo(stack, lastIndex-9, lastIndex, false)) {
                        return ItemStack.EMPTY;
                    }
                //player hotbar to player inv
                } else if (index < lastIndex && !this.moveItemStackTo(stack, 7, lastIndex-9, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, stack);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        Level level = blockEntity.getLevel();
        if (level == null) return false;
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), playerEntity, Registration.MINER_BLOCK.get());
    }


    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    @SuppressWarnings("UnusedReturnValue")
    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }
    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
}
