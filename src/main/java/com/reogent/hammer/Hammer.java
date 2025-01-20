package com.reogent.hammer;

import com.reogent.hammer.Listeners.InventoryOpenListener;
import com.reogent.hammer.Listeners.ItemListeners;
import com.reogent.hammer.Listeners.MoveListener;
import com.reogent.hammer.commands.HammerCommand;
import com.reogent.hammer.commands.HammerTabCompleter;
import de.tr7zw.nbtapi.NBT;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Hammer extends JavaPlugin {
    private ConfigManager mainconfig;
    private ConfigManager langconfig;
    public static Hammer instance;
    public static MiniMessage mm = MiniMessage.miniMessage();

    @Getter
    private final InventoryManager manager = new InventoryManager(this);


    private BukkitAudiences audiences;

    public BukkitAudiences adventure() {
        if(this.audiences == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.audiences;

    }

    @Override
    public Logger getLogger() {
        return super.getLogger();
    }

    @Override
    public void onEnable() {
        if (!NBT.preloadApi()) {
            getLogger().warning("NBT-API не найден! Выключаю плагин..");
            getPluginLoader().disablePlugin(this);
            return;
        }
        mainconfig = new ConfigManager(this, "config.yml");
        langconfig = new ConfigManager(this, "messages.yml");
        instance = this;
        manager.invoke();
        this.audiences = BukkitAudiences.create(this);
        getLogger().info("Плагин успешно загрузился!");
        Bukkit.getPluginManager().registerEvents(new MoveListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemListeners(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryOpenListener(this), this);
        getCommand("hammer").setExecutor(new HammerCommand());
        getCommand("hammer").setTabCompleter(new HammerTabCompleter());

    }

    @Override
    public void onDisable() {
        getLogger().info("Выключение..");
        if(this.audiences != null) {
            this.audiences.close();
            this.audiences = null;
        }
    }

    public static Hammer getInstance() {
        return instance;
    }

    public ConfigManager getMainConfig() {
        return mainconfig;
    }

    public ConfigManager getLangConfig() {
        return langconfig;
    }

    public BukkitAudiences getAudiences() {
        return audiences;
    }

    public void reloadPlugin() {
        getServer().getPluginManager().disablePlugin(this);
        getServer().getPluginManager().enablePlugin(this);
    }

    public InventoryManager getManager() {
        return manager;
    }
}
