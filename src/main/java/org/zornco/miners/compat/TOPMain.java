package org.zornco.miners.compat;

import mcjty.theoneprobe.api.ITheOneProbe;

import java.util.function.Function;

public class TOPMain implements Function<Object, Void> {
    static ITheOneProbe PROBE;

    @Override
    public Void apply(Object o) {
        PROBE = (ITheOneProbe) o;
//        PROBE.registerProvider(new EnergyControllerProvider());
//        PROBE.registerProvider(new EnergyNodeProvider());

        return null;
    }
}