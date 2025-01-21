package com.reogent.hammer;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigGetter {
    private static final Hammer inst = Hammer.getInstance();
    public static final ConfigManager config = inst.getMainConfig();

    public static FileConfiguration getConfig() {
        return config.getConfig();
    }
}
