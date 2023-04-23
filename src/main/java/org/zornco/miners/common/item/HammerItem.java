package org.zornco.miners.common.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.zornco.miners.common.multiblock.MultiblockManager;

public class HammerItem extends Item {
    public HammerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide)
            return InteractionResult.sidedSuccess(pContext.getLevel().isClientSide);

        MultiblockManager.handle(pContext.getLevel(), pContext.getClickedPos());
        return super.useOn(pContext);
    }
}
