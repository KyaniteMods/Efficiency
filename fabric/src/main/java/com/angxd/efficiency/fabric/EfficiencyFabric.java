package com.angxd.efficiency.fabric;

import com.angxd.efficiency.Efficiency;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class EfficiencyFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Efficiency.init();
    }
}