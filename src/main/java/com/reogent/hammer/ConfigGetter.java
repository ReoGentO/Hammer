package com.reogent.hammer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigGetter {
    private static final Hammer inst = Hammer.getInstance();
    public static final FileConfiguration config = inst.getConfig();
}
