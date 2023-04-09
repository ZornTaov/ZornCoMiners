package org.zornco.miners.compat;

import net.minecraftforge.fml.InterModComms;

public class TheOneProbeCompat {

    public static void sendIMC() {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPMain::new);
    }
}
