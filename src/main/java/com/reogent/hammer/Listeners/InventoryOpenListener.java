package com.reogent.hammer.Listeners;


import com.reogent.hammer.Hammer;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.events.RyseInventoryOpenEvent;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author Rysefoxx | Rysefoxx#7880
 * @since 12/3/2022
 */
@RequiredArgsConstructor
public class InventoryOpenListener implements Listener {

    private final Hammer plugin;

    @EventHandler
    public void onInventoryOpen(@NotNull RyseInventoryOpenEvent event) {
        Player player = event.getPlayer();
        RyseInventory inventory = event.getInventory();

        Optional<InventoryContents> optional = this.plugin.getManager().getContents(player.getUniqueId());

        if (optional.isEmpty()) {
            plugin.getLogger().warning("The player " + player.getName() + " has no contents.");
            return;
        }

        InventoryContents contents = optional.get();
        inventory.updateTitle(player, "Test Inventory - " + contents.pagination().page());
    }
}