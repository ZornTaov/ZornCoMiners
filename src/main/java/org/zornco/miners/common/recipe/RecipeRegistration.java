package org.zornco.miners.common.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.tags.ITagManager;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.zornco.miners.ZornCoMiners;

import java.util.HashMap;
@Mod.EventBusSubscriber(modid = ZornCoMiners.MOD_ID)
public class RecipeRegistration {
    public static final HashMap<BlockState, MinerRecipe> CACHED_MINER_RECIPE = new HashMap<>();

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

    @SubscribeEvent
    public void recipeReload(AddReloadListenerEvent event) {
        CACHED_MINER_RECIPE.clear();
        for (MinerRecipe recipe : asdgf;lijahsdpgoiuayhwd[pgioyua].getRecipesFor(RecipeRegistration.MINER_RECIPE.get(),
                FakeInventory.INSTANCE, ServerLifecycleHooks.getCurrentServer().overworld())) {
            if (recipe.getResource() != Blocks.AIR) {
                RecipeRegistration.loadBlocks(recipe, recipe.getResource());
            } else if(!recipe.getResourceTag().location().getPath().equals("air")) {
                RecipeRegistration.loadBlockTag(recipe, recipe.getResourceTag());
            }
        }
    }

    public static MinerRecipe getRecipe(BlockState block) {
        if (CACHED_MINER_RECIPE.containsKey(block))
            return CACHED_MINER_RECIPE.get(block);

        return null;
    }

    public static void loadBlockTag(MinerRecipe recipe, TagKey<Block> blockTagKey) {
        ITagManager<Block> tags = ForgeRegistries.BLOCKS.tags();
        if(tags != null)
            tags.getTag(blockTagKey).stream().forEach(blocks -> {
                loadBlocks(recipe, blocks);
            });
    }

    public static void loadBlocks(MinerRecipe recipe, Block block) {
        block.getStateDefinition().getPossibleStates().forEach(bs ->
            CACHED_MINER_RECIPE.put(bs, recipe)
        );
    }
}
