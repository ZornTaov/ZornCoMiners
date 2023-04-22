package org.zornco.miners;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.zornco.miners.common.compat.TheOneProbeCompat;
import org.zornco.miners.common.config.Configuration;
import org.zornco.miners.common.core.Registration;
import org.zornco.miners.common.recipe.FakeInventory;
import org.zornco.miners.common.recipe.MinerRecipe;
import org.zornco.miners.common.recipe.RecipeRegistration;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ZornCoMiners.MOD_ID)
public class ZornCoMiners
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "zorncominers";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    private boolean load = false;

    public ZornCoMiners()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::enqueueIMC);

        Registration.init(modEventBus);
        RecipeRegistration.init(modEventBus);

        // Configuration
        ModLoadingContext mlCtx = ModLoadingContext.get();
        mlCtx.registerConfig(ModConfig.Type.SERVER, Configuration.CONFIG);
        // Mango

        DispenserBlock.registerBehavior(Items.DIAMOND_AXE, (source, itemStack) -> {
            Level level = source.getLevel();
            BlockPos pos = source.getPos();
            Direction facing = source.getBlockState().getValue(FACING);
            BlockPos frontBlock = pos.relative(facing, 1);
            BlockState frontState = level.getBlockState(frontBlock);

            if (!frontState.isAir() && frontState.hasProperty(CropBlock.AGE) && frontState.getValue(CropBlock.AGE) == CropBlock.MAX_AGE) {
                level.destroyBlock(frontBlock, true);
                level.addDestroyBlockEffect(frontBlock, frontState);
                itemStack.setDamageValue(itemStack.getDamageValue() - 1);
            }

            return itemStack;
        });

        DispenserBlock.registerBehavior(Items.WHEAT_SEEDS, (source, itemStack) -> { // place
            Level level = source.getLevel();
            BlockPos pos = source.getPos();
            Direction facing = source.getBlockState().getValue(FACING);
            BlockPos frontBlock = pos.relative(facing, 1);
            BlockState frontState = level.getBlockState(frontBlock);

            if (frontState.isAir()) {
                level.setBlock(frontBlock, Blocks.WHEAT.defaultBlockState(), Block.UPDATE_ALL);
                itemStack.shrink(1);
            }

            return itemStack;
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }

    public void enqueueIMC(final InterModEnqueueEvent event) {
        LOGGER.trace("Sending IMC setup to TOP and other mods.");
        if (ModList.get().isLoaded("theoneprobe"))
            TheOneProbeCompat.sendIMC();
    }

}
