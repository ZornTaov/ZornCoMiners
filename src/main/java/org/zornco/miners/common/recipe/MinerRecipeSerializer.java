package org.zornco.miners.common.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zornco.miners.ZornCoMiners;

public class MinerRecipeSerializer implements RecipeSerializer<MinerRecipe> {
    @SuppressWarnings("NullableProblems")
    @Override
    public MinerRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
        ZornCoMiners.LOGGER.debug("Beginning deserialization of recipe: {}", recipeId.toString());
        DataResult<MinerRecipe> parseResult = MinerRecipe.CODEC.parse(JsonOps.INSTANCE, json);

        if (parseResult.error().isPresent()) {
            DataResult.PartialResult<MinerRecipe> pr = parseResult.error().get();
            ZornCoMiners.LOGGER.error("Error loading recipe: " + pr.message());
            return null;
        }

        MinerRecipe recipe = parseResult.result()
            .map(r -> {
                r.setId(recipeId);
                return r;
            })
            .orElse(null);
        if (recipe != null)
        {
//            if (recipe.getResource() != Blocks.AIR) {
//                RecipeRegistration.loadBlocks(recipe, recipe.getResource());
//            } else if (!recipe.getResourceTag().location().getPath().equals("air")) {
//                RecipeRegistration.loadBlockTag(recipe, recipe.getResourceTag());
//            }
        }
        return recipe;
    }

    @Nullable
    @Override
    public MinerRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
        ZornCoMiners.LOGGER.debug("Starting recipe read: {}", recipeId);

        if(!buffer.isReadable() || buffer.readableBytes() == 0) {
            ZornCoMiners.LOGGER.error("Recipe not readable from buffer: {}", recipeId);

            return null;
        }

        try {
            final MinerRecipe recipe = buffer.readWithCodec(MinerRecipe.CODEC);
            recipe.setId(recipeId);

            ZornCoMiners.LOGGER.debug("Finished recipe read: {}", recipeId);

            return recipe;
        }

        catch(EncoderException ex) {
            ZornCoMiners.LOGGER.error("Error reading recipe information from network: " + ex.getMessage());
            return null;
        }
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull MinerRecipe recipe) {
        ZornCoMiners.LOGGER.debug("Sending recipe over network: {}", recipe.getId());
        buffer.writeWithCodec(MinerRecipe.CODEC, recipe);
    }
}
