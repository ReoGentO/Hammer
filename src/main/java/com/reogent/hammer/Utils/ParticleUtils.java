package com.reogent.hammer.Utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleUtils {

    /**
     * Создает частицы в позиции игрока.
     *
     * @param player   Игрок, вокруг которого будут создаваться частицы.
     * @param particle Тип частиц (например, Particle.FLAME, Particle.SMOKE_NORMAL).
     * @param count    Количество частиц.
     * @param offsetX  Смещение по X от позиции игрока (от -1 до 1).
     * @param offsetY  Смещение по Y от позиции игрока (от -1 до 1).
     * @param offsetZ  Смещение по Z от позиции игрока (от -1 до 1).
     * @param speed    Скорость частиц.
     */
    public static void spawnParticlesAroundPlayer(Player player, Particle particle, int count, double offsetX, double offsetY, double offsetZ, double speed) {
        if (player == null || particle == null) {
            return; // Проверяем, что игрок и частица не null
        }

        Location playerLocation = player.getLocation();
        player.getWorld().spawnParticle(particle, playerLocation, count, offsetX, offsetY, offsetZ, speed);
    }

    /**
     * Создает круг из частиц вокруг игрока.
     *
     * @param player   Игрок, вокруг которого будет создан круг.
     * @param particle Тип частиц.
     * @param radius   Радиус круга.
     * @param count    Количество частиц в круге.
     * @param plane    Плоскость круга: 'horizontal' или 'vertical'.
     */
    public static void spawnParticleCircle(Player player, Particle particle, double radius, int count, String plane) {
        if (player == null || particle == null || radius <= 0 || count <= 0 || plane == null) {
            return; // Проверяем, что все параметры корректны
        }

        Location center = player.getLocation();
        double angleStep = 360.0 / count;

        for (int i = 0; i < count; i++) {
            double angle = Math.toRadians(i * angleStep);

            double x, y, z;

            if(plane.equalsIgnoreCase("horizontal")){
                x = center.getX() + radius * Math.cos(angle);
                y = center.getY();
                z = center.getZ() + radius * Math.sin(angle);
            } else if (plane.equalsIgnoreCase("vertical")){
                x = center.getX() + radius * Math.cos(angle);
                y = center.getY() + radius * Math.sin(angle);
                z = center.getZ();
            } else {
                return;
            }

            Location particleLocation = new Location(center.getWorld(), x, y, z);
            player.getWorld().spawnParticle(particle, particleLocation, 1, 0, 0, 0, 0);
        }
    }

    /**
     * Создает частицы в позиции игрока с параметрами по умолчанию (количество 10, смещение 0.1, скорость 0)
     *
     * @param player Игрок, вокруг которого будут создаваться частицы.
     * @param particle Тип частиц (например, Particle.FLAME, Particle.SMOKE_NORMAL).
     */
    public static void spawnParticlesAroundPlayer(Player player, Particle particle) {
        spawnParticlesAroundPlayer(player, particle, 10, 0.1, 0.1, 0.1, 0);
    }
}