package com.angxd.efficiency;

import com.angxd.efficiency.utils.ConnectionManager;
import com.angxd.rinthify.ModrinthApi;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Efficiency {
    public static List<String> REQUIRES_RESTART = new ArrayList<>();
    public static final String MOD_ID = "efficiency";
    public static final ConnectionManager CONNECTION_MANAGER = new ConnectionManager();
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {

    }
}