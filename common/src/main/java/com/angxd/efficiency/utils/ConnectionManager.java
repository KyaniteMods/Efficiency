package com.angxd.efficiency.utils;

import com.angxd.rinthify.ModrinthApi;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.net.InetAddress;

public class ConnectionManager {
    public ModrinthApi API;

    public ConnectionManager() {
        if(!ConnectionManager.isConnected()) return;

        this.API = ModrinthApi.builder().build();
    }
    public static boolean isConnected() {
        try {
            return InetAddress.getByName("www.google.com").isReachable(8000);
        } catch (IOException e) {
            return false;
        }
    }
}
