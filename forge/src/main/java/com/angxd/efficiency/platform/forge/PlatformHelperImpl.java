package com.angxd.efficiency.platform.forge;

import net.minecraftforge.fml.ModList;

public class PlatformHelperImpl {
    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
    public static String getLoader() { return "forge"; }
}