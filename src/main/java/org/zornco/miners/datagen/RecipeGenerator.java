package org.zornco.miners.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import org.zornco.miners.Registration;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> consumer) {

        // ================================================================================================================
        //    BLOCKS
        // ================================================================================================================
        ShapedRecipeBuilder.shaped(Registration.MINER_ITEM.get(), 1)
                .pattern("SSS")
                .pattern("HGH")
                .pattern("SCS")
                .define('G', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('C', Items.DIAMOND_PICKAXE)
                .define('H', Items.IRON_BARS)
                .define('S', Tags.Items.STONE)
                .unlockedBy("has_diamond_pickaxe", has(Items.DIAMOND_PICKAXE))
                .save(consumer);

        // ================================================================================================================
        //    ITEMS
        // ================================================================================================================
//        ShapedRecipeBuilder.shaped(Registration.ENERGY_LINKER_ITEM.get(), 1)
//                .pattern(" R ")
//                .pattern("SGS")
//                .pattern("SBS")
//                .define('R', Items.REDSTONE_TORCH)
//                .define('G', Tags.Items.GLASS_PANES)
//                .define('S', Tags.Items.INGOTS_IRON)
//                .define('B', Items.STONE_BUTTON)
//                .unlockedBy("has_redstone_torch", has(Blocks.REDSTONE_TORCH))
//                .save(consumer);
    }
}
