package com.reogent.hammer.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class LangConfig {
    private static File file;
    private static FileConfiguration LangConfigFile;

    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Hammer").getDataFolder(), "messages.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                //no
            }
        }
        LangConfigFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return LangConfigFile;
    }

    public static void save() {
        try {
            LangConfigFile.save(file);
        } catch (IOException e) {
            System.out.println("Failed to save config.yml file!");
        }
    }

    public static void reload() {
        LangConfigFile = YamlConfiguration.loadConfiguration(file);
    }
}
