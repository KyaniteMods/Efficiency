package com.angxd.efficiency.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class PlatformHelper {
    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static String getLoader() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static String getModsFolder() {
        throw new AssertionError();
    }
}
