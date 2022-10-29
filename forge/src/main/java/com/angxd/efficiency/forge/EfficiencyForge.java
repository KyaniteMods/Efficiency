package com.angxd.efficiency.forge;

import com.angxd.efficiency.Efficiency;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Efficiency.MOD_ID)
public class EfficiencyForge {
    public EfficiencyForge() {
        Efficiency.init();
    }
}