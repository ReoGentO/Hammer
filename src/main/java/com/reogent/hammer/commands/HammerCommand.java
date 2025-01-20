package com.reogent.hammer.commands;

import com.reogent.hammer.ConfigGetter;
import com.reogent.hammer.Hammer;
import com.reogent.hammer.Utils.ItemBuilder;
import com.reogent.hammer.Utils.ItemUtils;
import com.reogent.hammer.Utils.LangGetter;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pagination.SlotIterator;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class HammerCommand implements CommandExecutor {
    private static final Hammer inst = Hammer.getInstance();
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final LangGetter lg = new LangGetter();
    private static final ConfigGetter cg = new ConfigGetter();
    private static final ItemUtils iu = new ItemUtils();
    private static final BukkitAudiences audiences = inst.getAudiences();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду могут использовать только игроки!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendMessage(player, lg.prefix);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "sendmessage":
                if (args.length < 2) {
                    sendMessage(player, lg.error_message);
                    return true;
                }
                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    messageBuilder.append(args[i]).append(" ");
                }
                String message = messageBuilder.toString().trim();
                broadcastMessage(message);
                break;
            case "give":
                if (args.length < 3) {
                    sendMessage(player, lg.error_message);
                    return true;
                }
                String targetPlayerName = args[1];
                Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

                if (targetPlayer == null){
                    sendMessage(player, lg.error_message);
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
                    default: sendMessage(player, lg.missing_arg);
                }
                break;
            case "menu":
                RyseInventory.builder()
                        .title("Test Inventory - 1")
                        .rows(6)
                        .disableUpdateTask()
                        .provider(new InventoryProvider() {
                            @Override
                            public void init(Player player, InventoryContents contents) {
                                contents.fillBorders(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

                                Pagination pagination = contents.pagination();
                                pagination.setItemsPerPage(20);
                                pagination.iterator(SlotIterator.builder()
                                        .startPosition(1, 1)
                                        .type(SlotIterator.SlotIteratorType.HORIZONTAL)
                                        .build());

                                contents.set(5, 3, IntelligentItem.of(new ItemBuilder(Material.ARROW)
                                        .amount((pagination.isFirst() ? 1 : pagination.page() - 1))
                                        .displayName(pagination.isFirst()
                                                ? "This is the first page"
                                                : "Page -> " + pagination.newInstance(pagination).previous().page())
                                        .build(), event -> {
                                    if (pagination.isFirst()) {
                                        sendMessage(player, "You are already on the first page.");
                                        return;
                                    }

                                    RyseInventory currentInventory = pagination.inventory();
                                    currentInventory.open(player, pagination.previous().page());
                                }));

                                for (int i = 0; i < 30; i++)
                                    pagination.addItem(new ItemStack(Material.STONE));

                                int page = pagination.newInstance(pagination).next().page();
                                contents.set(5, 5, IntelligentItem.of(new ItemBuilder(Material.ARROW)
                                        .amount((pagination.isLast() ? 1 : page))
                                        .displayName((!pagination.isLast()
                                                ? "Page -> " + page
                                                : "This is the last page"))
                                        .build(), event -> {
                                    if (pagination.isLast()) {
                                        sendMessage(player, "You are already on the last page.");
                                        return;
                                    }

                                    RyseInventory currentInventory = pagination.inventory();
                                    currentInventory.open(player, pagination.next().page());
                                }));
                            }
                        })
                        .build(inst)
                        .open(player);
                break;
            default: sendMessage(player, lg.missing_arg);
        }

        return true;
    }

    private static void broadcastMessage(String message) {
        Audience playersAudience = audiences.players();
        playersAudience.sendMessage(mm.deserialize(message));
    }

    private static void sendMessage(Player player, String message) {
        Audience playerAudience = audiences.player(player);
        playerAudience.sendMessage(mm.deserialize(lg.prefix + message));
    }
}
