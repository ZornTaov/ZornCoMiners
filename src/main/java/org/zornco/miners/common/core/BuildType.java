package org.zornco.miners.common.core;

import net.minecraft.util.StringRepresentable;

public enum BuildType implements StringRepresentable {
    MULTIBLOCK,
    ORE;

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}
