package com.reogent.hammer.commands;

import com.reogent.hammer.ConfigGetter;
import com.reogent.hammer.Utils.ItemUtils;
import com.reogent.hammer.Utils.LangGetter;
import com.reogent.hammer.Utils.Utils;
import com.reogent.hammer.commands.Items.Back;
import com.reogent.hammer.commands.Items.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.HashMap;
import java.util.Map;

public class HammerCommand implements CommandExecutor {
    private static final LangGetter lg = new LangGetter();
    private static final ItemUtils iu = new ItemUtils();


    public Gui menu = Gui.normal()
            .setStructure(
                    "#########",
                    "#X......#",
                    "#########")
            .addIngredient('#', new SimpleItem(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)))
            .addIngredient('X', new Config())
            .build();

    public Gui config = Gui.normal()
            .setStructure(
                    "#########",
                    "#Y......#",
                    "X########")
            .addIngredient('#', new SimpleItem(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)))
            .addIngredient('X', new Back())
            .addIngredient('Y', new AbstractItem() {

                @Override
                public ItemProvider getItemProvider() {
                    boolean eHammer = ConfigGetter.config.getConfig().getBoolean("enable_hammer");
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
                    boolean eHammer = ConfigGetter.config.getConfig().getBoolean("enable_hammer");
                    if (clickType.isLeftClick() && eHammer) {
                        ConfigGetter.config.getConfig().set("enable_hammer", false);
                    } else {
                        ConfigGetter.config.getConfig().set("enable_hammer", true);
                    }
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    ConfigGetter.config.saveConfig();
                    ConfigGetter.config.reloadConfig();
                    notifyWindows();
                }
            })
            .build();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду могут использовать только игроки!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            Utils.sendMessage(player, lg.prefix);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "sendmessage":
                if (args.length < 2) {
                    Utils.sendMessage(player, lg.error_message);
                    return true;
                }
                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    messageBuilder.append(args[i]).append(" ");
                }
                String message = messageBuilder.toString().trim();
                Utils.broadcastMessage(message);
                break;
            case "give":
                if (args.length < 3) {
                    Utils.sendMessage(player, lg.error_message);
                    return true;
                }
                String targetPlayerName = args[1];
                Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

                if (targetPlayer == null){
                    Utils.sendMessage(player, lg.error_message);
                    return true;
                }
                String giveSubCommand = args[2].toLowerCase();
                switch (giveSubCommand) {
                    case "hammer":
                        Map<Attribute, Double> attributes = new HashMap<>();
                        attributes.put(Attribute.GENERIC_ATTACK_DAMAGE, 10.0);
                        attributes.put(Attribute.GENERIC_ATTACK_SPEED, -3.0);

                        iu.giveItem(player.getName(), lg.hammer_name, lg.hammer_lore, Material.DIAMOND_SWORD, nbt -> {
                            nbt.setBoolean("isHammer", true);
                            nbt.setInteger("HideFlags", 2);
                            nbt.setInteger("CustomModelData", 100);
                        }, attributes, "mainhand");
                        break;

                    case "jumpitem":
                        iu.giveItem(player.getName(), lg.breeze_name, lg.breeze_lore, Material.FEATHER, nbt -> {
                            nbt.setBoolean("isJumpItem", true);
                            nbt.setInteger("CustomModelData", 101);
                        }, null, null);
                        break;
                    default: Utils.sendMessage(player, lg.missing_arg);
                }
                break;
            case "adminmenu":
                createWindow(player, "<g:#FF0000;#1800FF>Hammer Админ-панель", menu);
                break;
            case "test":
                break;
            default: Utils.sendMessage(player, lg.missing_arg);
        }

        return true;
    }

    public void createWindow(Player player, String title, Gui ui) {
        Window window = Window.single()
                .setViewer(player)
                .setTitle(ItemUtils.hex(title))
                .setGui(ui)
                .build();
        window.open();
    }
}
