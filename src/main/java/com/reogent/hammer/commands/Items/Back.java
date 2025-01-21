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

public class Back extends AbstractItem {

    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName(ItemUtils.hex("&cНазад"));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType.isLeftClick()) {
            new HammerCommand().createWindow(player, "<g:#FF0000;#1800FF>Hammer Админ-панель", new HammerCommand().menu);
        }
        notifyWindows(); // this will update the ItemStack that is displayed to the player
    }
}
