package com.angxd.efficiency.utils;

import com.angxd.efficiency.platform.PlatformHelper;
import com.angxd.rinthify.data.misc.Version;
import net.minecraft.SharedConstants;

import java.util.List;

public class ClientUtils {
    public static boolean isMouseWithin(int x, int y, int width, int height, int mouseX, int mouseY)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static Version getValidVersion(List<Version> versionList) {
        return versionList.stream().filter((version -> version.game_versions.contains(SharedConstants.getCurrentVersion().getName()) && version.loaders.contains(PlatformHelper.getLoader()))).toList().get(0);
    }
}
