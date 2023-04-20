package org.zornco.miners.recipe;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.zornco.miners.Codecs;

import java.util.List;

public class MinerRecipe implements Recipe<FakeInventory> {
    private ResourceLocation id;
    private final ResourceLocation resource;
    private final String resourceTag;
    private final List<ItemStack> output;
    private final int tierMinimum;

    public static final Codec<MinerRecipe> CODEC = RecordCodecBuilder.create(i -> i.group(
//        Codec.STRING.optionalFieldOf("resource", "")
//            .forGetter(MinerRecipe::codecResource),
        ResourceLocation.CODEC.optionalFieldOf("resource", new ResourceLocation("minecraft:air"))
            .forGetter(MinerRecipe::codecResource),
        Codec.STRING.optionalFieldOf("resourceTag", "minecraft:air")
            .forGetter(MinerRecipe::codecResourceTag),
        Codecs.FRIENDLY_ITEMSTACK.listOf().fieldOf("outputs")
            .forGetter(MinerRecipe::codecOutputs),
        Codec.INT.optionalFieldOf("tierMinimum", 1)
            .forGetter(MinerRecipe::codecTier)

        ).apply(i, MinerRecipe::new));



    public MinerRecipe(ResourceLocation resource, String resourceTag, List<ItemStack> output, int tierMinimum) {
        this.resource = resource;
        this.resourceTag = resourceTag;
        this.output = output;
        this.tierMinimum = tierMinimum;
    }
    @Override
    public boolean matches(@NotNull FakeInventory inv, @NotNull Level level) {
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull FakeInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int height, int width) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }
    public void setId(ResourceLocation recipeId) {
        this.id = recipeId;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeRegistration.MINER_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeRegistration.MINER_RECIPE.get();
    }

    public List<ItemStack> codecOutputs() {
        return ImmutableList.copyOf(output);
    }

    public int codecTier() {
        return tierMinimum;
    }

    public ResourceLocation codecResource() {
        return resource;
    }

    private String codecResourceTag() {
        return resourceTag;
    }

    public Block getResource() {
        return Registry.BLOCK.getOptional((resource)).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown item '" + resource + "'");
        });
    }

    public TagKey<Block> getResourceTag() {
        return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(resourceTag));
    }
}
