package org.zornco.miners.common.core;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.zornco.miners.ZornCoMiners;
import org.zornco.miners.common.block.DrillBlock;
import org.zornco.miners.common.block.MinerBlock;
import org.zornco.miners.common.item.TestPadItem;
import org.zornco.miners.common.tile.MinerTile;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = ZornCoMiners.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registration {
    // ================================================================================================================
    //    Registries
    // ================================================================================================================
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ZornCoMiners.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ZornCoMiners.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ZornCoMiners.MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ZornCoMiners.MOD_ID);

    // ================================================================================================================
    //   PROPERTIES
    // ================================================================================================================
    private static final BlockBehaviour.Properties baseProperty = Block.Properties
        .of(Material.METAL)
        .strength(3.0f, 128.0f);

    // ================================================================================================================
    //    ITEMS
    // ================================================================================================================
    // TODO - remove for final build?
    public static final RegistryObject<TestPadItem> TEST_PAD_ITEM = ITEMS.register("test_pad", TestPadItem::new);
    // ================================================================================================================
    //    BLOCKS
    // ================================================================================================================
    public static final RegistryObject<MinerBlock> MINER_BLOCK =
        BLOCKS.register("miner_block", () -> new MinerBlock(baseProperty));
    public static final RegistryObject<DrillBlock> DRILL_BLOCK =
        BLOCKS.register("drill", () -> new DrillBlock(baseProperty));
    // ================================================================================================================
    //    ITEM BLOCKS
    // ================================================================================================================
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> MINER_ITEM =
        ITEMS.register("miner_block", () ->
            new BlockItem(MINER_BLOCK.get(),
                new Item.Properties().tab(Registration.ITEM_GROUP))
        );    @SuppressWarnings("unused")
    public static final RegistryObject<Item> DRILL_ITEM =
        ITEMS.register("drill", () ->
            new BlockItem(DRILL_BLOCK.get(),
                new Item.Properties().tab(Registration.ITEM_GROUP))
        );
    // ================================================================================================================
    //    TILE ENTITIES
    // ================================================================================================================
    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<MinerTile>> MINER_TILE =
        TILES.register("energy_controller", () ->
            BlockEntityType.Builder.of(MinerTile::new, MINER_BLOCK.get()
            ).build(null));

    public static void init(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        TILES.register(modEventBus);
        ENTITIES.register(modEventBus);
    }
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(ZornCoMiners.MOD_ID) {
        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Registration.TEST_PAD_ITEM.get());
        }
    };
}
