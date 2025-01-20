package com.reogent.hammer;

import com.reogent.hammer.Utils.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Particle;

public class ParticleTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Получаем предмет в основной руке
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item != null && item.getType() == Material.DIAMOND_SWORD) {
                ParticleUtils.spawnParticleCircle(player, Particle.FLAME, 1.0, 50, "horizontal");
            }
        }
    }
}