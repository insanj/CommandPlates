package me.insanj.commandplates;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Math;
import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ShapedRecipe;
import org.apache.commons.lang.exception.ExceptionUtils;

public class CommandPlatesPlugin extends JavaPlugin {
    private CommandPlatesConfig config;
    private CommandExecutor executor;
    private CommandPlatesListener listener;

    @Override
    public void onEnable() {
      // (1) setup config, which reads all existing pressure plates from config.yml (or saves default config.yml if not)
      config = new CommandPlatesConfig(this);

      // (2) setup listeners for pressure plate steps, which will then execute commands based on config
      listener = new CommandPlatesListener(this, config);
      Bukkit.getPluginManager().registerEvents(listener, this);

      // (3) setup commands to allow for list, create, and info (each needs permissions)
      executor = new CommandPlatesCommandExecutor(this, config);
      getCommand("pplates").setExecutor(executor);
    }

    public void logError(Throwable e) {
      getLogger().info(ExceptionUtils.getStackTrace(e));
    }
}
