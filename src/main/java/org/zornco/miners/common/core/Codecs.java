package org.zornco.miners.common.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class Codecs {
    public static final Codec<ItemStack> FRIENDLY_ITEMSTACK = RecordCodecBuilder.create(i -> i.group(
        Registry.ITEM.byNameCodec().fieldOf("id").forGetter(ItemStack::getItem),
        Codec.INT.optionalFieldOf("Count", 1).forGetter(ItemStack::getCount),
        CompoundTag.CODEC.optionalFieldOf("tag")
            .forGetter(is -> Optional.ofNullable(is.getTag()))
    ).apply(i, (id, count, tag) -> {
        final var is = new ItemStack(id, count);
        tag.ifPresent(is::setTag);
        return is;
    }));
    public static final Codec<List<BlockPos>> BLOCK_POS_LIST_CODEC = Codec.list(BlockPos.CODEC);
    public static final Codec<Direction> DIRECTION_CODEC = Direction.CODEC;//StringRepresentable.fromEnum(Direction::values, Direction::byName);
    public static final Codec<List<Direction>> DIRECTION_LIST_CODEC = Codec.list(DIRECTION_CODEC);
}
