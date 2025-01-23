package com.reogent.hammer.Listeners;

import com.reogent.hammer.Hammer;
import com.reogent.hammer.Utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;


public class MoveListener implements Listener {

    private Location lastLocation = null;
    private final FileConfiguration config = Hammer.getInstance().getConfig();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String effect = config.getString("players." + player.getName() + ".current_effect", "none");
        if (effect.equals("steps")) {
            Location currentLocation = event.getTo();
            if (lastLocation == null) {
                lastLocation = event.getFrom();
                return;
            }

            Vector velocity = player.getVelocity();
            double verticalVelocity = velocity.getY();


            // Проверяем момент приземления
            if (verticalVelocity <= 0 && lastLocation.getY() > currentLocation.getY() && player.isOnGround()) {
                ParticleUtils.spawnParticleCircle(player, Particle.VILLAGER_HAPPY, 0.5, 50, "horizontal");
            }
            lastLocation = currentLocation;
        }
    }
}
