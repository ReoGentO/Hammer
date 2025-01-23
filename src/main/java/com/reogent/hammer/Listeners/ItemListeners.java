package com.reogent.hammer.Listeners;

import com.reogent.hammer.ConfigGetter;
import com.reogent.hammer.Hammer;
import com.reogent.hammer.Utils.LangGetter;
import com.reogent.hammer.Utils.ParticleUtils;
import de.tr7zw.nbtapi.NBT;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class ItemListeners implements Listener {
    private final Map<Player, Double> fallCounters = new HashMap<>();
    private final Map<Player, Double> fallDistance = new HashMap<>();

    @EventHandler
    public void PlayerMove(PlayerMoveEvent event) {
        if (ConfigGetter.config.getBoolean("enable_hammer")) {
            Player player = event.getPlayer();
            Location from = event.getFrom();
            Location to = event.getTo();
            if (to==null) return;

            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR) return;
            boolean isHammer = NBT.get(item, nbt -> (boolean) nbt.getBoolean("isHammer"));

            double fallThreshold = -0.5;
            if (isHammer && from.getY() > to.getY() && player.getVelocity().getY() < fallThreshold) {
                double currentFallDistance = fallDistance.getOrDefault(player, 0.0);
                double fallDelta = Math.abs(to.getY() - from.getY());
                fallDistance.put(player, currentFallDistance + fallDelta);
                fallCounters.put(player, fallCounters.getOrDefault(player, 0.0) + 0.2);
                player.setFallDistance(0);
            } else if (fallCounters.containsKey(player)) {
                fallCounters.remove(player);
                fallDistance.remove(player);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (ConfigGetter.config.getBoolean("enable_hammer")) {

            if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof LivingEntity)) return;

            Player player = (Player) event.getDamager();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR) return;

            boolean isHammer = NBT.get(item, nbt -> (boolean) nbt.getBoolean("isHammer"));
            if (fallCounters.containsKey(player) && isHammer) {
                if (!player.isOnGround()) {
                    double fallCount = fallCounters.get(player);
                    double damage = event.getDamage();
                    double multipliedDamage = damage * fallCount;
                    event.setDamage(multipliedDamage);
                    double totalFallDistance = fallDistance.getOrDefault(player, 0.0);

                    if (totalFallDistance > 10.0) {
                        ParticleUtils.spawnParticlesAroundPlayer(player, Particle.CLOUD, 100, 0, 0, 0, 0.3);
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 2);
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1, 1);
                        Vector impulse = new Vector(0, (totalFallDistance / 10) * 1.1, 0);
                        player.setVelocity(impulse);
                        Collection<Entity> nearbyEntities = player.getWorld().getNearbyEntities(player.getLocation(), 5.0, 5.0, 5.0);
                        for (Entity nearbyEntity : nearbyEntities) {
                            if (nearbyEntity instanceof LivingEntity && nearbyEntity != player) {
                                Vector direction = nearbyEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                                Vector impulse2 = direction.multiply(2.0).add(new Vector(0, 1.5, 0));;
                                nearbyEntity.setVelocity(impulse2);
                            }
                        }
                    }
                }
                fallCounters.remove(player);
                fallDistance.remove(player);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) return;
        boolean isJumpItem = NBT.get(item, nbt -> (boolean) nbt.getBoolean("isJumpItem"));

        boolean CREATIVE = (player.getGameMode() == GameMode.CREATIVE);
        if (event.getHand() != null) {
            if (isJumpItem && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getHand().equals(EquipmentSlot.HAND)) {

                int amount = item.getAmount();
                Snowball snowball = player.launchProjectile(Snowball.class);
                snowball.setShooter(player);
                snowball.setMetadata("isImpulseSnowball", new FixedMetadataValue(Hammer.getInstance(), true));

                // Эффекты и уменьшение количества предметов
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1, 1);

                if (!CREATIVE) {
                    item.setAmount(amount - 1);
                }

            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball snowball &&
                snowball.hasMetadata("isImpulseSnowball")) {

            // Место столкновения снежка
            Location impactLocation = snowball.getLocation();

            // Создаем взрыв без разрушений
            impactLocation.getWorld().createExplosion(impactLocation, 0, false, false);

            // Генерация импульса
            double radius = 5.0; // Радиус действия импульса
            for (Entity entity : impactLocation.getWorld().getNearbyEntities(impactLocation, radius, radius, radius)) {
                if (!(entity instanceof Player)) continue;

                Player player = (Player) entity;
//                if (player.equals(snowball.getShooter())) continue; // Пропускаем того, кто бросил снежок

                // Рассчитываем вектор от эпицентра к игроку
                Vector direction = player.getLocation().toVector().subtract(impactLocation.toVector());

                // Рассчитываем расстояние до эпицентра
                double distance = direction.length();

                // Нормализуем вектор (приводим длину к 1)
                direction.normalize();

                // Модифицируем силу импульса
                double force = Math.max(0, (radius - distance) / radius); // Сила уменьшается с расстоянием
                direction.multiply(force * 1.5); // Увеличиваем горизонтальную силу
                direction.setY(force * 2); // Добавляем вертикальную составляющую

                // Применяем импульс к игроку
                player.setVelocity(direction);
            }
            // Визуальные эффекты
            ParticleUtils.spawnParticlesAt(snowball.getLocation(), Particle.CLOUD, 150, 0, 0, 0, 0.1);
            impactLocation.getWorld().playSound(impactLocation, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
            impactLocation.getWorld().playSound(impactLocation, Sound.ENTITY_GENERIC_EXPLODE, 1, 2);
            impactLocation.getWorld().playSound(impactLocation, Sound.ENTITY_GHAST_SHOOT, 1, 1);
        }
    }
}
