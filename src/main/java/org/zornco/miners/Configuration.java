package org.zornco.miners;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = ZornCoMiners.MOD_ID, bus = Bus.MOD)
public class Configuration {

    public static ForgeConfigSpec CONFIG;

    private static ForgeConfigSpec.BooleanValue USE_ENERGY;
    private static ForgeConfigSpec.IntValue ENERGY_USED_PER_TICK;

    static {
        generateConfig();
    }

    private static void generateConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        USE_ENERGY = builder
            .comment("Specify if miners will use forge energy")
            .comment("default: false")
            .define("useEnergy", true);

        ENERGY_USED_PER_TICK = builder
            .comment("Specify how much power is used per tick, if enabled.")
            .defineInRange("energyUsedPerTick", 100, 1, Integer.MAX_VALUE);

        CONFIG = builder.build();
    }
    public static Boolean useEnergy() {
        return USE_ENERGY.get();
    }
    public static Integer energyUsedPerTick() {
        return ENERGY_USED_PER_TICK.get();
    }
    @SubscribeEvent
    public static void onCommonReload(ModConfigEvent ev)
    {

    }
}
