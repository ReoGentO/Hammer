package com.reogent.hammer.commands.Items;

import com.reogent.hammer.Utils.ItemUtils;
import com.reogent.hammer.commands.HammerCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class Config extends AbstractItem {

    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(Material.DIAMOND).setDisplayName(ItemUtils.hex("<g:#DDFF00;#00FF98>Настройки"));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType.isLeftClick()) {
            new HammerCommand().createWindow(player, "<g:#DDFF00;#00FF98>Настройки", new HammerCommand().config);
        }
        notifyWindows();
    }
}
