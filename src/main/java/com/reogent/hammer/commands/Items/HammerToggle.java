package com.reogent.hammer.commands.Items;

import com.reogent.hammer.ConfigGetter;
import com.reogent.hammer.Hammer;
import com.reogent.hammer.Utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.io.File;

public class HammerToggle extends AbstractItem {

    @Override
    public ItemProvider getItemProvider() {
        boolean eHammer = ConfigGetter.config.getBoolean("enable_hammer");
        if (eHammer) {
            return new ItemBuilder(Material.PAPER)
                    .setDisplayName(ItemUtils.hex("&eБулава: &a" + eHammer))
                    .addLoreLines(" ", ItemUtils.hex("&aВкл&7/&4Выкл&7 булаву."));
        }
        return new ItemBuilder(Material.PAPER)
                .addEnchantment(Enchantment.MENDING, 1, true)
                .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                .setDisplayName(ItemUtils.hex("&eБулава: &4" + eHammer))
                .addLoreLines(" ", ItemUtils.hex("&aВкл&7/&4Выкл&7 булаву."));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        boolean eHammer = ConfigGetter.config.getBoolean("enable_hammer");
        if (clickType.isLeftClick() && eHammer) {
            ConfigGetter.config.set("enable_hammer", false);
        } else {
            ConfigGetter.config.set("enable_hammer", true);
        }
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        YamlConfiguration.loadConfiguration(new File(Hammer.getInstance().getDataFolder(), "config.yml"));
        Hammer.getInstance().saveConfig();
        notifyWindows();
    }
}