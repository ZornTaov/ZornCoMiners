package org.zornco.miners.common.core;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class Utils {

    public static void sendMessage(Player player, String text) {

        ((ServerPlayer)player).sendSystemMessage(Component.literal(text), false);
    }

    public static void sendSystemMessage(Player player, String text) {
        sendSystemMessage(player, Component.literal(text));
    }

    public static void sendSystemMessage(Player player, Component text) {
        player.displayClientMessage(text, true);
    }

    public static void SendSystemMessage(@Nonnull UseOnContext context, Component s) {
        if (!context.getLevel().isClientSide) {
            sendSystemMessage(Objects.requireNonNull(context.getPlayer()), s);
        }
    }

    public static String getCoordinatesAsString(Vec3i vec) {
        return "" + vec.getX() + ", " + vec.getY() + ", " + vec.getZ();
    }

    @Nonnull
    public static Direction getFacingFromBlockPos(@Nonnull BlockPos pos, @Nonnull BlockPos neighbor) {
        return Direction.getNearest(
            (float) (pos.getX() - neighbor.getX()),
            (float) (pos.getY() - neighbor.getY()),
            (float) (pos.getZ() - neighbor.getZ()));
    }

    public static BlockPos getOffsetFromPos(BlockPos to, BlockPos from)
    {
        return !from.equals(to) ? to.subtract(from) : BlockPos.ZERO;
    }

    public static BlockPos getPosFromOffset(BlockPos to, BlockPos from)
    {
        return !from.equals(to) ? to.offset(from) : BlockPos.ZERO;
    }
}
