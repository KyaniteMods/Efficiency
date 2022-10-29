package com.angxd.efficiency.platform.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class PlatformHelperImpl {
    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
    public static String getLoader() { return "fabric"; }
}
