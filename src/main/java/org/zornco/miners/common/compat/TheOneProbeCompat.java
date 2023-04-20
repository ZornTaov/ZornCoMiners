package org.zornco.miners.common.compat;

import net.minecraftforge.fml.InterModComms;

public class TheOneProbeCompat {

    public static void sendIMC() {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPMain::new);
    }
}
