package com.reogent.hammer.commands;

import com.reogent.hammer.Hammer;
import com.reogent.hammer.Utils.ItemUtils;
import com.reogent.hammer.Utils.LangGetter;
import com.reogent.hammer.Utils.Utils;
import com.reogent.hammer.commands.Items.Back;
import com.reogent.hammer.commands.Items.Config;
import com.reogent.hammer.commands.Items.HammerToggle;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.HashMap;
import java.util.Map;

public class HammerCommand implements CommandExecutor {
    private static final LangGetter lg = new LangGetter();
    private static final ItemUtils iu = new ItemUtils();
    private static final Hammer inst = Hammer.getInstance();
    private static final MiniMessage mm = MiniMessage.miniMessage();

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
            .addIngredient('Y', new HammerToggle())
            .build();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            Player player = (Player) sender;
            Utils.sendMessage(player, lg.missing_arg);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "give":
                if (args.length < 3) {
                    Utils.sendMessage(sender, mm.deserialize(lg.error_message));
                    return true;
                }
                String targetPlayerName = args[1];
                Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

                if (targetPlayer == null){
                    Utils.sendMessage(sender, mm.deserialize(lg.error_message));
                    return true;
                }
                String giveSubCommand = args[2].toLowerCase();
                switch (giveSubCommand) {
                    case "hammer":
                        Map<Attribute, Double> attributes = new HashMap<>();
                        attributes.put(Attribute.GENERIC_ATTACK_DAMAGE, 10.0);
                        attributes.put(Attribute.GENERIC_ATTACK_SPEED, -3.0);

                        iu.giveItem(targetPlayer.getName(), lg.hammer_name, lg.hammer_lore, Material.DIAMOND_SWORD, nbt -> {
                            nbt.setBoolean("isHammer", true);
                            nbt.setInteger("HideFlags", 2);
                            nbt.setInteger("CustomModelData", 100);
                        }, attributes, "mainhand");
                        break;

                    case "jumpitem":
                        iu.giveItem(targetPlayer.getName(), lg.breeze_name, lg.breeze_lore, Material.FEATHER, nbt -> {
                            nbt.setBoolean("isJumpItem", true);
                            nbt.setInteger("CustomModelData", 101);
                        }, null, null);
                        break;
                    default: Utils.sendMessage((Player) sender, lg.missing_arg);
                }
                break;
            case "adminmenu":
                Player player = (Player) sender;
                createWindow(player, "<g:#FF0000;#1800FF>Hammer Админ-панель", menu);
                break;
            case "test":
                
                break;
            default: Utils.sendMessage((Player) sender, lg.missing_arg);
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
