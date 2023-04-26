package org.zornco.miners.common.recipe;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
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
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber(modid = ZornCoMiners.MOD_ID)
public class RecipeRegistration {

    public static final HashMap<Block, MinerRecipe> CACHED_MINER_RECIPE = new HashMap<>();
    private static AtomicBoolean LOADED_CACHE = new AtomicBoolean(false);

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
    public static void recipeReload(AddReloadListenerEvent event) {
        CACHED_MINER_RECIPE.clear();
        LOADED_CACHE.set(false);
    }

    // TODO: Make a better way of handling the loading of recipes?

    public static void loadRecipes(Level level) {
        if (!LOADED_CACHE.get()) {
            level.getRecipeManager().getAllRecipesFor(MINER_RECIPE.get()).forEach(recipe -> {
                if (recipe.getResource() != Blocks.AIR) {
                    RecipeRegistration.loadBlock(recipe, recipe.getResource());
                } else if (!recipe.getResourceTag().location().getPath().equals("air")) {
                    RecipeRegistration.loadBlockTag(recipe, recipe.getResourceTag());
                }
            });

            LOADED_CACHE.set(true);
        }
    }

    // Always call loadRecipe() first!
    public static MinerRecipe getRecipe(BlockState blockState) {
        return CACHED_MINER_RECIPE.getOrDefault(blockState.getBlock(), MinerRecipe.EMPTY);
    }

    public static boolean hasRecipe(BlockState blockState) {
        return CACHED_MINER_RECIPE.containsKey(blockState.getBlock());
    }

    public static void loadBlockTag(MinerRecipe recipe, TagKey<Block> blockTagKey) {
        ITagManager<Block> tags = ForgeRegistries.BLOCKS.tags();
        if(tags != null)
            tags.getTag(blockTagKey).stream().forEach(b -> loadBlock(recipe, b));

    }

    public static void loadBlock(MinerRecipe recipe, Block block) {
        if (!CACHED_MINER_RECIPE.containsKey(block))
            CACHED_MINER_RECIPE.put(block, recipe);
    }
}
