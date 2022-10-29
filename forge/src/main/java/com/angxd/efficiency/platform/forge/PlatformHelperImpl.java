package com.angxd.efficiency.platform.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

public class PlatformHelperImpl {
    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
    public static String getLoader() { return "forge"; }
    public static String getModsFolder() { return FMLPaths.MODSDIR.get().toString(); }
}