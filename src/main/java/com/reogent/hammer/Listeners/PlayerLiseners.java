package com.reogent.hammer.Listeners;

import com.reogent.hammer.ConfigGetter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class PlayerLiseners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!ConfigGetter.config.getConfig().contains("players." + player.getName())) {
            ConfigGetter.getConfig().set("players." + player.getName() + ".current_effect", "none");

            ConfigGetter.config.saveConfig();
            ConfigGetter.config.reloadConfig();
        }
    }
}
