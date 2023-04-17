package org.zornco.miners.recipe;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class FakeInventory extends RecipeWrapper {
    public static final FakeInventory INSTANCE = new FakeInventory();

    public FakeInventory() {
        super(new ItemStackHandler(0));
    }
}
