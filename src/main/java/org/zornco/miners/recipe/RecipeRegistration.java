package org.zornco.miners.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.zornco.miners.ZornCoMiners;

public class RecipeRegistration {
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

}
