package org.zornco.miners.common.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.zornco.miners.ZornCoMiners;

import java.util.HashMap;

public class RecipeRegistration {
    public static final HashMap<Block, MinerRecipe> CACHED_MINER_RECIPE = new HashMap<>();

    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ZornCoMiners.MOD_ID);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ZornCoMiners.MOD_ID);

    // ================================================================================================================

    public static final RegistryObject<RecipeSerializer<MinerRecipe>> MINER_SERIALIZER = RECIPES.register("miner", MinerRecipeSerializer::new);

    public static final ResourceLocation MINER_RECIPE_TYPE_ID = new ResourceLocation(ZornCoMiners.MOD_ID, "miner_recipe");

    public static final RegistryObject<RecipeType<MinerRecipe>> MINER_RECIPE = RECIPE_TYPES.register(MINER_RECIPE_TYPE_ID.getPath(),
        () -> RecipeType.simple(MINER_RECIPE_TYPE_ID));

    // ================================================================================================================

    public static void init(IEventBus eventBus) {
        RECIPES.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }

    public static MinerRecipe getRecipe(Block block) {
        if (CACHED_MINER_RECIPE.containsKey(block))
            return CACHED_MINER_RECIPE.get(block);

        return null;
    }

    public static void loadBlockTag(MinerRecipe recipe, TagKey<Block> blockTagKey) {
        ForgeRegistries.BLOCKS.tags().getTag(blockTagKey).stream().forEach(block -> {
            loadBlock(recipe, block);
        });
    }

    public static void loadBlock(MinerRecipe recipe, Block block) {
        CACHED_MINER_RECIPE.put(block, recipe);
    }
}
