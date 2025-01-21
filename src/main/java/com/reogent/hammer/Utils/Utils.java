package com.reogent.hammer.Utils;

import com.reogent.hammer.Hammer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class Utils {
    private static final Hammer inst = Hammer.getInstance();
    private static final BukkitAudiences audiences = inst.getAudiences();
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final LangGetter lg = new LangGetter();

    public static void broadcastMessage(String message) {
        Audience playersAudience = audiences.players();
        playersAudience.sendMessage(mm.deserialize(message));
    }

    public static void sendMessage(Player player, String message) {
        Audience playerAudience = audiences.player(player);
        playerAudience.sendMessage(mm.deserialize(lg.prefix + message));
    }
}
