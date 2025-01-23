package com.reogent.hammer.Listeners;

import com.reogent.hammer.ConfigGetter;
import com.reogent.hammer.Hammer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

import java.io.File;

public class PlayerLiseners implements Listener {
    private static final Hammer inst = Hammer.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!ConfigGetter.config.contains("players." + player.getName())) {
            ConfigGetter.config.set("players." + player.getName() + ".current_effect", "none");

            YamlConfiguration.loadConfiguration(new File(inst.getDataFolder(), "config.yml"));
        }
    }
}
